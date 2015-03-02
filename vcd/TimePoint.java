package vcd;

import java.util.ArrayList;

/**
 TimePoint objects represent a point in time that corresponds to a timespec in a
 VCD file.  Each TimePoint object contains a list of signal name, value tuples
 that represent the signals updated at this point in time and what value they
 were updated to.  This time focused way of tracking value updates contrasts with
 signal focused tracking as seen in {@link vcd.SignalHistory}.
 
 @author Matthew Hicks
 @see SigVal
 @see SignalHistory
 @see VCD
*/
public class TimePoint
{
    private final long time;
    private final ArrayList<SigVal> pairs;
    
    /**
     Constructs a new TimePoint object that represents the passed time with an
     empty list of signal name, value tuples.
     
     @author Matthew Hicks
     @param pTime the time that this object holds signal data for
     @see VCD
     @see SigVal
    */
    public TimePoint(long pTime)
    {
        time = pTime;
        pairs = new ArrayList<SigVal>(10);
    }
    
    /**
     Returns the time that this TimePoint object represents.
     
     @author Matthew Hicks
     @return the time
    */
    public long getTime()
    {
        return time;
    }
    
    /**
     Appends the passed signal name, value tuple to the list of tuples.
     
     @author Matthew Hicks
     @param pNewPair the tuple to append to the list of tuples
    */
    public void addPair(SigVal pNewPair)
    {
        pairs.add(pNewPair);
    }
    
    /**
     Returns the number of signal name, value tuples in the list of tuples.
     
     @author Matthew Hicks
     @return the number of tuples in the list
    */
    public int getPairCount()
    {
        return pairs.size();
    }
    
    /**
     Returns the signal name, value tuple at the specified index in the list of
     tuples.
     
     @author Matthew Hicks
     @param pIndex Index of the tuple to return
     @return The signal name, value tuple at the specified index in the list
     of tuples.
     @throws java.lang.IndexOutOfBoundsException if the supplied index is out
     of range
    */
    public SigVal getPair(int pIndex)
    {
        return pairs.get(pIndex);
    }
    
    /**
     Returns the list of of signals and values updated during this moment in
     time.
     @author Matthew Hicks
     @return A list of signal name, value tuples.
    */
    public ArrayList<SigVal> getPairs()
    {
        return pairs;
    }
}
