package vcd;

import java.util.ArrayList;

public class TimePoint
{
    private final long time;
    private final ArrayList<SigVal> pairs;
    
    public TimePoint(long pTime)
    {
        time = pTime;
        pairs = new ArrayList<SigVal>(10);
    }
    
    public long getTime()
    {
        return time;
    }
    
    public void addPair(SigVal pNewPair)
    {
        pairs.add(pNewPair);
    }
    
    public int getPairCount()
    {
        return pairs.size();
    }
    
    public SigVal getPair(int pIndex)
    {
        return pairs.get(pIndex);
    }
    
    public ArrayList<SigVal> getPairs()
    {
        return pairs;
    }
}
