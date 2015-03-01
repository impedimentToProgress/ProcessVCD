import java.io.IOException;
import java.util.*;
import java.io.FileWriter;
import vcd.*;

public class testVCD implements TimeCallback
{
    // Bin the results into .25% toggle rate bins
    public static final int NUM_BINS = 400;
    
    static VCD vcd;
    static long maxTime;
    static int percent = 0;
    
    public void timeUpdate(long pTime)
    {
        reportProgress(pTime);
    }
    
    private static void reportProgress(long pTime)
    {
        if((pTime * 100 / maxTime) > percent)
        {
            percent++;
            System.err.println("" + percent + "%");
            
            outputHistorgram();
            System.out.println("########################");
            vcd.resetPerformanceCounters();
        }
    }
    
    private static void outputHistorgram()
    {
        try{
        FileWriter out = new FileWriter("hist_" + percent + ".txt");
        
        // Sort the signals by increasing toggle counts
        ArrayList<Signal> signalList = new ArrayList<Signal>(vcd.signals.values());
        Collections.sort(signalList, new Comparator<Signal>() {
            public int compare(Signal pA, Signal pB)
            {
                return (int)(pA.getToggles() - pB.getToggles());
            }
        });
        
        // Print the sorted list
        //for(Signal sig : signalList)
        //{
        //    System.out.println(sig.getName() + "\t" + sig.getToggles());
        //}
        //System.out.println("########################");
        
        // For each toggle count, count the number of signals with that count
        int binIndex = 0;
        int[] bins = new int[NUM_BINS];
        long maxToggles = signalList.get(signalList.size() - 1).getToggles();
        long binSize = maxToggles / NUM_BINS + 1;
        long currentToggleBinCap = binSize;
        for(Signal sig : signalList)
        {
            // Be careful, there may be more than one bin between signals
            while(sig.getToggles() > currentToggleBinCap)
            {
                currentToggleBinCap += binSize;
                ++binIndex;
            }
            
            bins[binIndex]++;
        }
        
        // Print out each toggle count as a toggle rate, with the number of signals with that count/rate
        for(binIndex = 0; binIndex < NUM_BINS; ++binIndex)
        {
            out.write(String.format("%d\t%d\t%.4f\t%.4f\n", (binIndex * binSize), bins[binIndex], ((double)binIndex)/NUM_BINS, ((double)bins[binIndex])/signalList.size()));
        }
        
        out.close();
        }
        catch(IOException ioe)
        {
            ;
        }
    }
    
    public static void main(String args[])throws IOException
    {
        vcd = new VCD(args[0]);
        maxTime = vcd.getLastTime();
        vcd.setTimeUpdateCallback(new testVCD());
        
        //vcd.printHeader();
        System.out.println("########################");
        System.out.println(vcd.getTimescale());
        System.out.println("########################");
        //vcd.printInitialValues();
        System.out.println("########################");
        System.out.println("Last time: " + maxTime);
        System.out.println("########################");
        vcd.readValuesFromVCD();
        //System.out.println("Signals: " + vcd.signals.size());
        System.out.println("########################");
    }
}
