package vcd;

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.zip.GZIPInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import vcd.*;

public class VCD
{
    private final String vcdFile;
    private BufferedReader file = null;
    private TimePoint currentTimePoint = null;
    private long lastTime = -1;
    private TimeCallback timeUpdateCallback = null;
    public ArrayList<TimePoint> timeSeries;

    // Create a hashmap of signals in the vcd
    public HashMap<String, Signal> signals;
    
    // Constructor that takes the name of a vcd file
    public VCD(String pFileName)throws IOException
    {
        vcdFile = pFileName;
    }
    
    // Functions that move to given sections in the VCD file
    private void seekHeader()throws IOException
    {
        if(file != null)
            file.close();
        
        // Can silently handle raw VCD files and gzipped VCD files
        if(vcdFile.endsWith(".vcd.gz"))
            file = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(vcdFile))));
        else if(vcdFile.endsWith(".vcd"))
            file = new BufferedReader(new FileReader(vcdFile));
        else
        {
            throw new IOException("ERROR: File must end in .vcd or .vcd.gz");
        }
    }
    
    private void seekInitialValues()throws IOException
    {
        seekHeader();
        
        String line;
        while((line = file.readLine()) != null)
        {
            if(isStartOfInitialValues(line))
            {
                return;
            }
        }
        
        throw new IOException("ERROR: no initial values (dumpvars) section");
    }
    
    private void seekValues()throws IOException
    {
        seekHeader();
        seekInitialValues();
        
        String line;
        while((line = file.readLine()) != null)
        {
            // VCD times and vals sections follows immediately the init vals section
            if(isEndOfInitialValues(line))
            {
                return;
            }
        }
        
        throw new IOException("ERROR: no times and values section");
    }
    
    
    // Print the time unit the VCD file uses
    public String getTimescale()throws IOException
    {
        seekHeader();
        
        String line;
        boolean inTS = false;
        String result = "";
        while((line = file.readLine()) != null)
        {
            if(!inTS && line.startsWith("$timescale"))
            {
                inTS = true;
                
                // Check for one liner
                if(line.endsWith("$end"))
                {
                    return line.substring("$timescale".length(), line.length() - "$end".length()).trim();
                }
                
                result = line.substring("$timescale".length()).trim();
            }
            else if(inTS && line.endsWith("$end"))
            {
                return result.trim() + " " + line.substring(0, line.length() - "$end".length()).trim();
            }
            else if(inTS)
            {
                result += result + " " + line.trim();
            }
        }
        
        throw new IOException("ERROR: no timescale section");
    }

    
    // Print the header of the passed VCD file
    public void printHeader()throws IOException
    {
        seekHeader();
        
        String line;
        while((line = file.readLine()) != null)
        {
            // VCD header ends with enddefinitions line
            if(isEndOfHeader(line))
            {
                return;
            }
            
            System.out.println(line);
        }
    }
    
    // Print the initial values section
    public void printInitialValues()throws IOException
    {
        seekInitialValues();
        
        String line;
        while((line = file.readLine()) != null)
        {
            // Look for section end
            if(isEndOfInitialValues(line))
            {
                return;
            }
        
            System.out.println(line);
        }
    }
    
    // Print the times and values section
    public void printTimesAndValues()throws IOException
    {
        seekValues();
        
        String line;
        while((line = file.readLine()) != null)
        {
            System.out.println(line);
        }
    }
    
    // Search the tail of the VCD file for that last timespec
    // Return the last timespec found
    public long getLastTime()throws IOException
    {
        if(this.lastTime != -1)
            return this.lastTime;
            
        seekValues();
        
        // Get the number of chars in the values section
        long totalChars = file.skip(Long.MAX_VALUE);
        
        // Binary search for a tail of the file with a time in it
        for(long tail = 1000; tail < totalChars; tail <<= 1)
        {
            // Reset the stream and try a new skip distance
            seekValues();
            file.skip(totalChars - tail);
            
            // Search the tail for a time
            String line;
            String lastTime = null;
            while((line = file.readLine()) != null)
            {
                if(isTimespec(line))
                {
                    lastTime = line.substring(1);
                }
            }
            
            // Return the last time if this tail had one
            if(lastTime != null)
            {
                return (this.lastTime = Long.parseLong(lastTime));
            }
        }
        
        throw new IOException("ERROR: No times found in the VCD file");
    }
    
    // Create a table of signals in the VCD
    // Each signal has a name and a symbol
    private void createSymbolTable()throws IOException
    {
        if(signals != null)
            return;
        
        signals = new HashMap<String, Signal>(2000);
        
        seekHeader();
        
        String currentPath = "";
        String line;
        while((line = file.readLine()) != null)
        {
            if(isEndOfHeader(line))
            {
                return;
            }
            
            // Keep track of scope for full path signal names
            if(isDownScope(line))
            {
                currentPath += "/" + line.split(" ")[2];
            }
            else if(isUpScope(line))
            {
                currentPath = currentPath.substring(0, currentPath.lastIndexOf('/'));
            }
            if(isVarDecl(line))
            {
                String[] parts = line.split(" ");
                
                // Check that the declaration is in a format that we can handle
                if(parts.length != 6 && parts.length != 7)
                    throw new IOException("ERROR: Variable declaration not in a useable format: " +  line);
                
                // VCD allows same symbol for mult signals if they are the same
                // We want the one higher-up in the hierarchy
                if(signals.get(parts[3]) == null)
                {
                    SignalType st = (parts[1].charAt(0) == 'r') ? SignalType.reg : SignalType.wire;
                    String slice = parts.length == 7 ? parts[5] : "";
                    int bits = Integer.parseInt(parts[2]);
                    
                    signals.put(parts[3], new Signal(currentPath + "/", parts[4] + slice, st, bits, parts[3]));
                }
            }
        }
    }
    
    // Read the values section of the VCD and update the signals accordingly
    public void readValuesFromVCD()throws IOException
    {
        createSymbolTable();
        
        seekValues();
        
        long currentTime = 0;
        String line;
        while((line = file.readLine()) != null)
        {
            String[] parts = line.split(" ");
            
            // Check for new time points
            if(isTimespec(line))
            {
                currentTime = Long.parseLong(line.substring(1));
                
                // If there is a callback, then call it
                if(timeUpdateCallback != null)
                    timeUpdateCallback.timeUpdate(currentTime);
            }
            // One bit signals have no space between value and symbol
            else if(parts.length == 1)
            {
                signals.get(line.substring(1)).setValue("" + line.charAt(0), currentTime);
            }
            else if(parts.length == 2)
            {
                signals.get(parts[1]).setValue(parts[0], currentTime);
            }
            else
            {
                System.err.println("ERROR: Something went wrong with reading the VCD values");
                System.exit(1);
            }
        }
    }
    
    // Resets the performance counters of all signals
    // Useful as part of a callback function used when processing the value section
    // of a VCD file
    public void resetPerformanceCounters()
    {
        for(Signal sig : signals.values())
        {
            sig.resetCounters();
        }
    }
    
    // Set the function to call every time update
    public void setTimeUpdateCallback(TimeCallback pTCB)
    {
        timeUpdateCallback = pTCB;
    }
    
    // Go through the values section, record all updates for each time unit
    public void collectTimes()throws IOException
    {
        if(timeSeries != null)
            return;
        
        timeSeries = new ArrayList<TimePoint>(1000);
        
        seekValues();
        
        String line;
        while((line = file.readLine()) != null)
        {
            String[] parts = line.split(" ");
            
            // Check for new time points
            if(isTimespec(line))
            {
                timeSeries.add(new TimePoint(Long.parseLong(line.substring(1))));
                currentTimePoint = timeSeries.get(timeSeries.size() - 1);
            }
            // One bit signals have no space between value and symbol
            else if(parts.length == 1)
            {
                currentTimePoint.addPair(new SigVal(line.substring(1), line.substring(0,0)));
            }
            else if(parts.length == 2)
            {
                currentTimePoint.addPair(new SigVal(parts[1], parts[0]));
            }
            else
            {
                System.err.println("ERROR: Something went wrong with collect times");
                System.exit(1);
            }
        }
    }
    
    // Given a signal name and tye of signal,
    // this method scans the VCD header for the symbol used in the
    // value dump portion of the file
    // This method returns the short name or null if not found
    public String signalNameToSymbol(String pSignalName, String pSignalType)throws IOException
    {
        seekHeader();
        
        String line;
        String result = null;
        while((line = file.readLine()) != null)
        {
            if(isEndOfHeader(line))
            {
                break;
            }
            
            // We assume the signal is a register and look
            // for that version, because the wire instances are
            // broken down into bits
            if(line.contains(" " + pSignalName + " ") && line.contains(" " + pSignalType + " "))
            {
                return line.split(" ")[3];
            }
        }
        
        return null;
    }
    
    private void reportTimesSignalIsValue(String pSignalString, long pValue)throws IOException
    {
        seekValues();
        
        String line;
        long currentTime = 0;
        
        while((line = file.readLine()) != null)
        {
            // Keep track of which time step we are at
            if(isTimespec(line))
            {
                currentTime = Long.parseLong(line.substring(1));
            }
            
            // Look for a signal match, then compare values
            if(line.endsWith(" " + pSignalString))
            {
                String valuePortion = line.substring(0, line.indexOf(' '));
                long valueAtTime = convertValueFormat(valuePortion);
                
                if(valueAtTime == pValue)
                {
                    System.out.println("" + currentTime);
                }
            }
            // If the value is one bit then there is no space between value and symbol
            else if(((line.charAt(0) == '0' || line.charAt(0) == '1') && line.substring(1, line.length()).equals(pSignalString)))
            {
                long valueAtTime = line.charAt(0) - '0';
                    
                if(valueAtTime == pValue)
                {
                    System.out.println("" + currentTime);
                }
            }
        }
    }
    
    // Converts passed bxxxxxx string into an integer and returns it
    private static long convertValueFormat(String value)
    {
        long result = 0;
        int index;
        
        for(index = 1; index < value.length(); ++index)
        {
            int bit = value.charAt(index) - '0';
            
            // Watch those X's
            if(bit > 1)
                bit = 0;
            
            result = (result * 2) + bit;
        }
        
        return result;
    }
    
    // Takes a string and returns true if that string can be parsed as the end
    // of the header section
    private static boolean isEndOfHeader(String pLine)
    {
        return pLine.startsWith("$enddefinitions $end");
    }
    
    // Takes a string and returns true if that string can be parsed as the end
    // of the dumpvars section
    private static boolean isEndOfInitialValues(String pLine)
    {
        return pLine.startsWith("$end");
    }
    
    // Takes a string and returns true if that string can be parsed as a signal
    // declaration line from the header section
    private static boolean isVarDecl(String pLine)
    {
        return pLine.startsWith("$var ");
    }
    
    // Takes a string and returns true if that string can be parsed as a
    // timespec
    private static boolean isTimespec(String pLine)
    {
        return pLine.charAt(0) == '#';
    }
    
    // Takes a string and returns true if that string can be parsed as a
    // down scope
    private static boolean isDownScope(String pLine)
    {
        return pLine.startsWith("$scope ");
    }
    
    // Takes a string and returns true if that string can be parsed as a
    // going up in scope
    private static boolean isUpScope(String pLine)
    {
        return pLine.startsWith("$upscope ");
    }
    
    // VCD init vals sections starts with dumpvar
    private static boolean isStartOfInitialValues(String pLine)
    {
        return pLine.startsWith("$dumpvars");
    }
}
