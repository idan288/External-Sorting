public class FilesInfo
{
    private final int filesAmount;
    private final int minLinesAmount;
    private final int maxLinesLimit;

    public FilesInfo(int filesAmount, int minLinesAmount, int maxLinesLimit)
    {
        this.filesAmount    = filesAmount;
        this.minLinesAmount = minLinesAmount;
        this.maxLinesLimit  = maxLinesLimit;
    }

    public int getMinLinesAmount()
    {
        return minLinesAmount;
    }

    public int getFilesAmount()
    {
        return filesAmount;
    }

    public int getMaxLinesLimit()
    {
        return maxLinesLimit;
    }
}