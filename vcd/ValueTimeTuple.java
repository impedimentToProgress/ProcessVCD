package vcd;

/**
 ValueTimeTuple is a tuple containing a signal value and a time that value was
 set. This is useful for tracking all the value updates for a signal without the
 memory overhead of using a {@link SignalHistory} object.  Immutable.
 
 @author Matthew Hicks
 @see SignalHistory
*/
public class ValueTimeTuple
{
    private final String value;
    private final long time;
    
    /**
     Constructs a signal value and update time tuple.
     @author Matthew Hicks
     @param pValue Current value of the signal.
     @param pTime Time the value was set.
    */
    public ValueTimeTuple(String pValue, long pTime)
    {
        value = pValue;
        time = pTime;
    }
    
    /**
     Returns the time associated with the signal value.
     @author Matthew Hicks
     @return Time that the signal value was set.
    */
    public long getTime()
    {
        return time;
    }
    
    /**
     Returns the value of this signal.
     @author Matthew Hicks
     @return String representing a signal value.
    */
    public String getValue()
    {
        return value;
    }
}
