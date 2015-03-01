package vcd;

enum SignalType
{
    wire, reg
}

public class Signal
{
    // Immutable signal properties
    private final String path;
    private final String name;
    private final int width;
    private final SignalType type;
    private final String symbol;
    
    private String currentValue = null;
    private long timeOfLastUpdate = 0;
    
    // Performance counters
    private long timeLow = 0;
    private long timeHigh = 0;
    private long toggles = 0;
    
    public Signal(String pPath, String pName, SignalType pType, int pWidth, String pSymbol)
    {
        path = pPath;
        name = pName;
        type = pType;
        width = pWidth;
        symbol = pSymbol;
    }
    
    public Signal(String pName, SignalType pType, int pWidth, String pSymbol)
    {
        // Seperate the passed name into a path prefix and a shortname
        // Gracefully handles the case where there is no path prefix
        this(pName.substring(0, pName.lastIndexOf('/') + 1), pName.substring(pName.lastIndexOf('/') + 1), pType, pWidth, pSymbol);
    }
    
    // Updates the value of the signal and appropriately adjusts the performance counters
    public void setValue(String pValue, long pTime)
    {
        //long diffTime = pTime - timeOfLastUpdate;
        
        if(currentValue == null)
        {
            ;
        }
        /*else if(isLowValue(currentValue))
        {
            timeLow += diffTime;
        }
        else
        {
            timeHigh += diffTime;
        }*/
        
        timeOfLastUpdate = pTime;
        currentValue = pValue;
        ++toggles;
    }
    
    // Resets the performance counters
    public void resetCounters()
    {
        timeLow = 0;
        timeHigh = 0;
        toggles = 0;
    }
    
    // Getters
    public String getPath()
    {
        return path;
    }
    
    public String getShortName()
    {
        return name;
    }
    
    public String getName()
    {
        return (path + name);
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public SignalType getType()
    {
        return type;
    }
    
    public String getSymbol()
    {
        return symbol;
    }
    
    public String getValue()
    {
        return currentValue;
    }
    
    public long getTimeOfLastUpdate()
    {
        return timeOfLastUpdate;
    }
    
    public long getTimeHigh()
    {
        return timeHigh;
    }
    
    public long getTimeLow()
    {
        return timeLow;
    }
    
    public long getToggles()
    {
        return toggles;
    }
    
    // Private helper functions
    private static boolean isLowValue(String pValue)
    {
        return true;
    }
}
