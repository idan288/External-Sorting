import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        int size = 1000000;
        List<CSVFileStructure> list = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < size; i++)
        {
            CSVFileStructure csvFileStructure = new CSVFileStructure();
            csvFileStructure.setName(Character.toString((char) (rand.nextInt(26) + 'A')));
            csvFileStructure.setId(rand.nextInt(100));
            list.add(csvFileStructure);
        }

        writeListContentToFile(list, "./inputCSVFile.csv");

        ExternalSorting externalSorting = new ExternalSorting();
        Instant start = Instant.now();
        externalSorting.run("./inputCSVFile.csv", "./", 1000);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("TimeTake: " + timeElapsed.getSeconds());
    }

    private static void writeListContentToFile(List<CSVFileStructure> curFileRec, String filePath) throws IOException
    {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath)))
        {
            for (CSVFileStructure csvFileStructure : curFileRec)
            {
                writer.write(csvFileStructure.toCSVFormat() + System.lineSeparator());
            }
        }
    }
}