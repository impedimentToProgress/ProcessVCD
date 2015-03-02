package vcd;

/**
 SigVal is a tuple containing a signal name and a value. This is useful for
 tracking signal value updates associated with specific points in time without
 the memory overhead of using a {@link Signal} object.  Immutable.
 
 @author Matthew Hicks
 @see TimePoint
*/
public class SigVal
{
    private final String value;
    private final String name;
 
    /**
     Constructs a signal name, value tuple.
     @author Matthew Hicks
     @param pName String that represents a signal.
     @param pValue Current value of the signal.
    */
    public SigVal(String pName, String pValue)
    {
        value = pValue;
        name = pName;
    }
    
    /**
     Returns the name of this signal.
     @author Matthew Hicks
     @return String representing a signal.
    */
    public String getName()
    {
        return name;
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
