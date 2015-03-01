package vcd;

import java.util.ArrayList;

public class SignalHistory extends Signal
{
    // This version of a signal keeps a full history of values and times
    private final ArrayList<ValueTimeTuple> values = new ArrayList<ValueTimeTuple>(1000);
    
    public SignalHistory(String pPath, String pName, SignalType pType, int pWidth, String pSymbol)
    {
        super(pPath, pName, pType, pWidth, pSymbol);
    }
    
    public SignalHistory(String pName, SignalType pType, int pWidth, String pSymbol)
    {
        super(pName, pType, pWidth, pSymbol);
    }
    
    
    // Overload what happens with every value update
    // Updates the value of the signal and appropriately adjusts the performance counters
    // Adds the value, time to the list
    public void setValue(String pValue, long pTime)
    {
        super.setValue(pValue, pTime);
        values.add(new ValueTimeTuple(pValue, pTime));
    }
    
    // Resets the the values and times
    public void resetHistory()
    {
        values.clear();
    }
    
    // Getter
    public ArrayList<ValueTimeTuple> getValues()
    {
        return values;
    }
}
