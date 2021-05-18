import java.io.Serializable;

public class CSVFileStructure implements Serializable
{
    private long id;
    private String name;

    public CSVFileStructure()
    {
    }

    public CSVFileStructure(CSVFileStructure other)
    {
        this.id   = other.id;
        this.name = other.name;
    }


    @Override
    public String toString()
    {
        return "{id= " + id
                + ", name= " + name + "}";
    }

    public String toCSVFormat()
    {
        return id + "," + name;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
