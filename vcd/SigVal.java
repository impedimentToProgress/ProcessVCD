package vcd;

public class SigVal
{
    private final String value;
    private final String name;
    
    public SigVal(String pName, String pValue)
    {
        value = pValue;
        name = pName;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getValue()
    {
        return value;
    }
}
