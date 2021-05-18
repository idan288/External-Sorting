public class MinHeapObj
{
    private final CSVFileStructure item;
    private final int fileIndex;
    private final int curItr;

    public MinHeapObj(CSVFileStructure item, int fileIndex, int curItr)
    {
        this.fileIndex = fileIndex;
        this.item      = item;
        this.curItr = curItr;
    }

    public CSVFileStructure getItem()
    {
        return item;
    }

    public int getFileIndex()
    {
        return fileIndex;
    }

    public int getCurItr()
    {
        return curItr;
    }
}
