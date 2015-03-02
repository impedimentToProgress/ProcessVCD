package vcd;

import java.util.ArrayList;

/**
 SignalHistory contains information about a variable from a VCD file; this
 includes the signal definition, its current value, time of last update, a
 history of values for this signal, and performance counters of toggle rate and
 duty cycle.  This signal focused tracking of values is in contrast to the time
 focused tracking implemented by {@link vcd.TimePoint}.
 
 @author Matthew Hicks
 @see ValueTimeTuple
 @see TimePoint
 @see VCD
*/
public class SignalHistory extends Signal
{
    // This version of a signal keeps a full history of values and times
    private final ArrayList<ValueTimeTuple> values = new ArrayList<ValueTimeTuple>(1000);
    
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
    public SignalHistory(String pPath, String pName, SignalType pType, int pWidth, String pSymbol)
    {
        super(pPath, pName, pType, pWidth, pSymbol);
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
    public SignalHistory(String pName, SignalType pType, int pWidth, String pSymbol)
    {
        super(pName, pType, pWidth, pSymbol);
    }
    
    /**
     Updates the value and time of last update of this signal. Adds the value
     and update time to the list of values and update times. Appropriately
     adjusts the performance counters.
     @author Matthew Hicks
     @param pValue New value of the signal.
     @param pTime Time of the value update.
    */
    public void setValue(String pValue, long pTime)
    {
        super.setValue(pValue, pTime);
        values.add(new ValueTimeTuple(pValue, pTime));
    }
    
    /**
     Resets the value and update time history of this signal.  The current value
     and time of last update are maintained.
     @author Matthew Hicks
    */
    public void resetHistory()
    {
        values.clear();
    }
    
    /**
     Returns a list of values that this signal has had and the times those values
     were set.  This includes the current value and time of last update.
     @author Matthew Hicks
     @return A list of values and times that this signal has had.
     @see java.util.ArrayList
     @see ValueTimeTuple
    */
    public ArrayList<ValueTimeTuple> getValues()
    {
        return values;
    }
}
