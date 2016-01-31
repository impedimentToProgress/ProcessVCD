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

    /**
     Returns true if the passed {@link vcd.SignalHistory} object has
     the same values and times as this object.
     @author Matthew Hicks
     @param pSH A SignalHistory object to compare this one to.
     @return true if the passed object has the same history, false otherwise.
    */
    public boolean equals(SignalHistory pSH)
    {
	// Perform the fast checks first
	if(this.getValues().size() != pSH.getValues().size())
	    return false;
	if(this.getTimeLow() != pSH.getTimeLow())
	    return false;
	if(this.getTimeHigh() != pSH.getTimeHigh())
	    return false;
	if(this.getToggles() != pSH.getToggles())
	    return false;
	if(this.getWidth() != pSH.getWidth())
	    return false;

	// Now we have to check update-by-update
	for(int vtt = 0; vtt < this.getValues().size(); ++ vtt)
	{
	    if(this.getValues().get(vtt).getTime() != pSH.getValues().get(vtt).getTime())
		return false;
	    if(!this.getValues().get(vtt).getValue().equals(pSH.getValues().get(vtt).getValue()))
		return false;
	}

	return true;
    }
  }
