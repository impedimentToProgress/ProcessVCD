package vcd;

enum SignalType
{
    wire, reg
}


/**
 Signal contains information about a variable from a VCD file; this includes the
 signal definition, its current value, time of last update, and performance
 counters of toggle rate and duty cycle.  Signal contains only this Signal's
 current value and the time that the value was set.  For a complete history of
 the times and values of this signal's update, see {@link SignalHistory}, which
 extends Signal.
 
 @author Matthew Hicks
 @see VCD
 @see SignalHistory
*/
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
    
    /**
     Constructs a signal with the specified properties, when path and name are seperated.
     @author Matthew Hicks
     @param pPath Path in the design hierarchy of the signal. Assumes an ending '/'.
     @param pName Short name of the signal.
     @param pType Signal type; reg or wire.
     @param pWidth Number of bits in the signal.
     @param pSymbol Symbol used in the VCD file to concisely represent this signal.
     @see SignalType
    */
    public Signal(String pPath, String pName, SignalType pType, int pWidth, String pSymbol)
    {
        path = pPath;
        name = pName;
        type = pType;
        width = pWidth;
        symbol = pSymbol;
    }
    
    /**
     Constructs a signal with the specified properties, when path and name are combined.
     @author Matthew Hicks
     @param pName Full name of the signal.
     @param pType Signal type; reg or wire.
     @param pWidth Number of bits in the signal.
     @param pSymbol Symbol used in the VCD file to concisely represent this signal.
     @see SignalType
     */
    public Signal(String pName, SignalType pType, int pWidth, String pSymbol)
    {
        // Seperate the passed name into a path prefix and a shortname
        // Gracefully handles the case where there is no path prefix
        this(pName.substring(0, pName.lastIndexOf('/') + 1), pName.substring(pName.lastIndexOf('/') + 1), pType, pWidth, pSymbol);
    }
    
    /**
     Updates the value and time of last update of this signal. Appropriately
     adjusts the performance counters.
     @author Matthew Hicks
     @param pValue New value of the signal.
     @param pTime Time of the value update.
    */
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
    
    /**
     Resets the performance counters, but maintains the value and time of last
     update for this signal.
     @author Matthew Hicks
    */
    public void resetCounters()
    {
        timeLow = 0;
        timeHigh = 0;
        toggles = 0;
    }
    
    // Getters
    
    /**
     Returns the path in the design hierarchy leading up to this signal.
     @author Matthew Hicks
     @return A string representing the path to this signal.
    */
    public String getPath()
    {
        return path;
    }
    
    /**
     Returns the name of this signal.
     @author Matthew Hicks
     @return A string representing the short name of this signal.
    */
    public String getShortName()
    {
        return name;
    }
    
    /**
     Returns the fully-qualified name of this signal.
     @author Matthew Hicks
     @return A string representing the combined path and name of this signal.
    */
    public String getName()
    {
        return (path + name);
    }
    
    /**
     Returns the number of bits in this signal.
     @author Matthew Hicks
     @return Bit width.
    */
    public int getWidth()
    {
        return width;
    }
    
    /**
     Returns the type of this signal
     @author Matthew Hicks
     @return A enumeration set to the type of this signal.
    */
    public SignalType getType()
    {
        return type;
    }
    
    /**
     Returns the symbol used in the VCD file to refer to this signal.
     @author Matthew Hicks
     @return A string representing the symbol the VCD files uses to refer to this signal.
    */
    public String getSymbol()
    {
        return symbol;
    }
    
    /**
     Returns the current value of this signal.
     @author Matthew Hicks
     @return A string representing the current value.
    */
    public String getValue()
    {
        return currentValue;
    }
    
    /**
     Returns the time that this signal was last updated.
     @author Matthew Hicks
     @return A number in simulation time units when this signal was last updated.
    */
    public long getTimeOfLastUpdate()
    {
        return timeOfLastUpdate;
    }
    
    /**
     Returns the amount of time that this signal had a high value since creation or the last counter reset.
     @author Matthew Hicks
     @return A number in simulation time units that this signal had a high value.
    */
    public long getTimeHigh()
    {
        return timeHigh;
    }
    
    /**
     Returns the amount of time that this signal had a low value since creation or the last counter reset.
     @author Matthew Hicks
     @return A number in simulation time units that this signal had a low value.
    */
    public long getTimeLow()
    {
        return timeLow;
    }
    
    /**
     Returns the number of times that this signal was updated since creation or the last counter reset.
     @author Matthew Hicks
     @return The number of times this signal was updated.
    */
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
