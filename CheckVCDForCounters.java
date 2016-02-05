import java.io.IOException;
import java.util.*;
import java.io.FileWriter;
import vcd.*;

public class CheckVCDForCounters
{
    static VCD vcd;
    static long maxTime;
    static ArrayList<SignalHistory> suspects;

    private static boolean PRINT_VALUES = false;
    private static boolean REPORT_EACH_STEP = false;
    private static boolean REPORT_FINAL_STEP = true;
    
    public static void main(String args[])throws IOException
    {
        if(args.length != 1)
        {
            System.err.println("Usage: java testVCD file.vcd");
            System.exit(1);
        }
        
        vcd = new VCD(args[0], true);
        vcd.readValuesFromVCD();
        System.out.println("Signals: " + vcd.signals.size());

	if(REPORT_EACH_STEP)
	    System.out.println("Creating initial suspects from signals with no repeated values");
	generateInitialSuspects();
	if(REPORT_EACH_STEP)
	    printSuspects();

	if(REPORT_EACH_STEP)
	    System.out.println("Removing identical suspects with different names");
	removeDuplicateSuspects();
	if(REPORT_EACH_STEP)
	    printSuspects();

	if(REPORT_EACH_STEP)
	    System.out.println("Removing suspects that are fully-expressed");
	removeFullyExpressedSuspects();
	if(REPORT_EACH_STEP)
	    printSuspects();

	if(REPORT_EACH_STEP)
	    System.out.println("Identifying constants");
	printSuspectsConstants();
    }

    // After processing the VCD file, look for signals without repeating values
    // Add any such signals to the suspects list
    private static void generateInitialSuspects()
    {
	if(vcd == null || vcd.signals == null || vcd.signals.size() == 0)
	{
	    System.err.println("ERROR: Cannot generate initial set of suspects");
	    return;
	}
	
	suspects = new ArrayList<SignalHistory>(vcd.signals.size() / 10);

	// Look for value repeats on every signal
	for(Signal sig: vcd.signals.values())
	{
	    //System.out.println(sig.getName());
	    ArrayList<ValueTimeTuple> vtts = ((SignalHistory)sig).getValues();

	    // Sort the list to make finding repeat values faster
	    Collections.sort(vtts, (ValueTimeTuple vtt1, ValueTimeTuple vtt2) -> vtt1.getValue().compareTo(vtt2.getValue()));

	    // Due to sorted list, only need to check adjacent values
	    // for repeats
	    String lastValue = "";
	    boolean foundDupe = false;
	    for(ValueTimeTuple vtt: vtts)
	    {
		//System.out.println("\t" + vtt.getValue());
		if(vtt.getValue().equals(lastValue))
		{
		    foundDupe = true;
		    break;
		}

		lastValue = vtt.getValue();
	    }

	    if(!foundDupe)
	    {
		suspects.add((SignalHistory)sig);
	    }
	}
    }

    // Given a set of suspects signals, remove signals that are identical
    private static void removeDuplicateSuspects()
    {
	// Make sure there are suspects
	if(suspects == null || suspects.size() == 0)
	{
	    System.err.println("ERROR: No suspects yet");
	    return;
	}

	// Compare every signal to every other signal
	// Remove the suspects with the shorter path
	int numSuspects = suspects.size();
	for(int susA = 0; susA < numSuspects; ++susA)
	{
	    // Skip suspects with only init values
	    if(suspects.get(susA).getValues().size() == 0)
		continue;
	    
	    SignalHistory susSigA = suspects.get(susA);
	    for(int susB = susA + 1; susB < numSuspects; ++susB)
	    {
		SignalHistory susSigB = suspects.get(susB);
		if(susSigA.equals(susSigB))
		{
		    SignalHistory susWithLongerPath = susSigA.getPath().length() >= susSigB.getPath().length() ? susSigA : susSigB;
		    suspects.remove(susB);
		    suspects.set(susA, susWithLongerPath);
		    --susB;
		    --numSuspects;
		}
	    }
	}
	
    }

    // Given a set of suspects signals, remove signals that are fully-expressed (all values seen)
    private static void removeFullyExpressedSuspects()
    {
	// Make sure there are suspects
	if(suspects == null || suspects.size() == 0)
	{
	    System.err.println("ERROR: No suspects yet");
	    return;
	}


	for(int sus = 0; sus < suspects.size(); ++sus)
	{
	    SignalHistory suspect = suspects.get(sus);

	    // Only check for smallish signals
	    if(suspect.getWidth() > 28)
		continue;
	    
	    int numValues = suspect.getValues().size();
	    int numPossibleValues = 0x1 << suspect.getWidth();

	    if(numValues == numPossibleValues)
	    {
		suspects.remove(sus);
		--sus;
	    }
	}
    }

    // Print the suspects
    private static void printSuspects()
    {	
	for(SignalHistory sig: suspects)
	{
	    System.out.println("Possible counter: " + sig.getName());

	    if(PRINT_VALUES)
	    {
		for(ValueTimeTuple ttv: sig.getValues())
		{
		    System.out.println("\t" + ttv.getValue());
		}
	    }
	}
    }

    // Print the suspects and identify constants
    private static void printSuspectsConstants()
    {
	int constants = 0;
	
	for(SignalHistory sig: suspects)
	{
	    if(sig.getValues().size() < 2)
	    {
		++constants;
		if(REPORT_FINAL_STEP)
		    System.out.println("Constant: " + sig.getName());
	    }
	}

	if(REPORT_FINAL_STEP)
	{
	    for(SignalHistory sig: suspects)
	    {
		if(sig.getValues().size() > 1)
		{
		    System.out.println("Possible counter: " + sig.getName());
		    if(PRINT_VALUES)
		    {
			for(ValueTimeTuple ttv: sig.getValues())
			{
			    System.out.println("\t" + ttv.getValue());
			}
		    }
		}
	    }
	}

	System.out.println(constants + " constants");
	System.out.println((suspects.size() - constants) + " possible counters");
    }
}
