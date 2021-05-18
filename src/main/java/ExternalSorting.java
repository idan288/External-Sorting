import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ExternalSorting
{
    private static final String CSV_DELIMITER = ",";
    private static final String SORTED_FILES_DIR = "./SortedFiles/";
    private static final String SORTED_FILE_PATH = SORTED_FILES_DIR + "sortedFile_";
    private static final String FILE_EXTENSION = ".csv";
    private static final String OUTPUT_FILE_NAME = "finalFile.csv";

    private static int compare(MinHeapObj a, MinHeapObj b)
    {
        return (int) (a.getItem().getId() - b.getItem().getId());
    }

    public void run(String filePath, String outputFilePath, int maxNumOfRec) throws IOException
    {
        // Part one will split the original CSV file to separate sorted files
        Files.deleteIfExists(Paths.get(OUTPUT_FILE_NAME));
        FilesInfo filesInfo = splitOriginalCSVFile(filePath, maxNumOfRec);

        // Part two is to merged all this file to one sorted file.
        mergeKFiles(filesInfo, outputFilePath);
        FileUtils.deleteDirectory(new File(SORTED_FILES_DIR));
    }


    private void mergeKFiles(FilesInfo filesInfo, String outputFilePath) throws IOException
    {
        PriorityQueue<MinHeapObj> minHeap = new PriorityQueue<>(ExternalSorting::compare);
        Map<Integer, List<CSVFileStructure>> sortedFilesMap;
        List<CSVFileStructure> mergedList = new ArrayList<>();
        String fullOutputFileName = outputFilePath + OUTPUT_FILE_NAME;
        sortedFilesMap = getKFiles(filesInfo);
        addingFirstItemFromKFilesToMinHeap(sortedFilesMap, minHeap);

        do
        {
            while (!minHeap.isEmpty())
            {
                MinHeapObj curMinItem = minHeap.poll();
                mergedList.add(curMinItem.getItem());
                LinkedList<CSVFileStructure> listMinItem = (LinkedList<CSVFileStructure>) sortedFilesMap.get(curMinItem.getFileIndex());

                if (listMinItem != null && !listMinItem.isEmpty())
                {
                    CSVFileStructure item = listMinItem.poll();
                    minHeap.add(new MinHeapObj(item, curMinItem.getFileIndex(), curMinItem.getCurItr() + 1));
                }
                else
                {
                    addFileContent(filesInfo, curMinItem.getCurItr() + 1, sortedFilesMap, curMinItem.getFileIndex());
                    if (sortedFilesMap.containsKey(curMinItem.getFileIndex()))
                    {
                        LinkedList<CSVFileStructure> list = (LinkedList<CSVFileStructure>) sortedFilesMap.get(curMinItem.getFileIndex());
                        minHeap.add(new MinHeapObj(list.pop(), curMinItem.getFileIndex(), curMinItem.getCurItr() + 1));
                    }
                }

                if (mergedList.size() == filesInfo.getMaxLinesLimit())
                {
                    writeListContentToFile(mergedList, fullOutputFileName);
                    mergedList.clear();
                }
            }
        }
        while (sortedFilesMap.size() > 0);

        if (mergedList.size() > 0)
        {
            writeListContentToFile(mergedList, fullOutputFileName);
        }
    }

    private void addingFirstItemFromKFilesToMinHeap(Map<Integer, List<CSVFileStructure>> sortedFiles, PriorityQueue<MinHeapObj> minHeap)
    {
        if (sortedFiles != null)
        {
            for (Map.Entry<Integer,List<CSVFileStructure>> entry : sortedFiles.entrySet())
            {
                LinkedList<CSVFileStructure> list = (LinkedList<CSVFileStructure>) entry.getValue();
                minHeap.add(new MinHeapObj(list.pop(), entry.getKey(), 0));
            }
        }
    }

    private Map<Integer, List<CSVFileStructure>> getKFiles(FilesInfo filesInfo) throws IOException
    {
        Map<Integer, List<CSVFileStructure>> filesContentSet = new LinkedHashMap<>();

        for (int i = 0; i < filesInfo.getFilesAmount(); i++)
        {
            addFileContent(filesInfo, 0, filesContentSet, i);
        }

        return filesContentSet;
    }

    private void addFileContent(FilesInfo filesInfo, int curItr, Map<Integer, List<CSVFileStructure>> filesContentSet, int fileIndex) throws IOException
    {
        String filePath = SORTED_FILE_PATH + fileIndex + FILE_EXTENSION;
        List<CSVFileStructure> fileContent = getFileContent(filePath, filesInfo.getMinLinesAmount(), curItr * filesInfo.getMinLinesAmount());
        if (!fileContent.isEmpty())
        {
            filesContentSet.put(fileIndex, fileContent);
        }
        else
        {
            filesContentSet.remove(fileIndex);
        }
    }

    private List<CSVFileStructure> getFileContent(String filePath, int minLinesAmount, int linesToSkip) throws IOException
    {
        List<CSVFileStructure> list = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {
            String line;

            for (int i = 0; i < minLinesAmount+linesToSkip && (line = br.readLine()) != null; i++)
            {
                if (i >= linesToSkip)
                {
                    CSVFileStructure csvFileStructure = mapToCSVFileStructure(line.split(CSV_DELIMITER));
                    list.add(csvFileStructure);
                }
            }
        }

        return list;
    }

    private FilesInfo splitOriginalCSVFile(String filePath, int maxNumOfRec) throws IOException
    {
        int filesAmount = 0;
        int minLinesAmount = maxNumOfRec;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {
            List<CSVFileStructure> curFileRec = new ArrayList<>(maxNumOfRec);
            String line;

            while ((line = br.readLine()) != null)
            {
                CSVFileStructure csvFileStructure = mapToCSVFileStructure(line.split(CSV_DELIMITER));
                curFileRec.add(csvFileStructure);
                if (curFileRec.size() == maxNumOfRec)
                {
                    addCurFileRecToNewFile(curFileRec, filesAmount);
                    filesAmount++;
                    curFileRec.clear();
                }
            }

            if (curFileRec.size() > 0)
            {
                addCurFileRecToNewFile(curFileRec, filesAmount);
                minLinesAmount = curFileRec.size();
                filesAmount++;
            }
        }

        return new FilesInfo(filesAmount, minLinesAmount, maxNumOfRec);
    }

    private void addCurFileRecToNewFile(List<CSVFileStructure> curFileRec, int filesAmount) throws IOException
    {
        curFileRec.sort((a, b) -> (int) (a.getId() - b.getId()));
        Files.createDirectories(Paths.get(SORTED_FILES_DIR));
        String filePath = SORTED_FILE_PATH + filesAmount + FILE_EXTENSION;
        writeListContentToFile(curFileRec, filePath);
    }

    private void writeListContentToFile(List<CSVFileStructure> curFileRec, String filePath) throws IOException
    {
        Path path = Paths.get(filePath);
        StandardOpenOption writeOption = StandardOpenOption.CREATE;
        if (Files.exists(path))
        {
            writeOption = StandardOpenOption.APPEND;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, writeOption))
        {
            for (CSVFileStructure csvFileStructure : curFileRec)
            {
                writer.write(csvFileStructure.toCSVFormat() + System.lineSeparator());
            }
        }
    }

    private CSVFileStructure mapToCSVFileStructure(String[] tokens)
    {
        CSVFileStructure csvFileStructure = new CSVFileStructure();
        if (tokens != null && tokens.length > 0)
        {
            csvFileStructure.setId(Integer.parseInt(tokens[0]));
            csvFileStructure.setName(tokens[1]);
        }

        return csvFileStructure;
    }
}