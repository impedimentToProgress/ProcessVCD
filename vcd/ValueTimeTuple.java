package vcd;

public class ValueTimeTuple
{
    private final String value;
    private final long time;
    
    public ValueTimeTuple(String pValue, long pTime)
    {
        value = pValue;
        time = pTime;
    }
    
    public long getTime()
    {
        return time;
    }
    
    public String getValue()
    {
        return value;
    }
}
