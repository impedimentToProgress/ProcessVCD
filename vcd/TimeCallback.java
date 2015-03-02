package vcd;

/**
 TimeCallback is an interface that must be implemented by classes that wish to
 be called when {@link vcd.VCD#readValuesFromVCD} processes the values section
 of a VCD file.  Before the callback will work, it needs to be registered by
 passing an object that implements {@link vcd.TimeCallback#timeUpdate} to
 {@link vcd.VCD#setTimeUpdateCallback}.
 
 @author Matthew Hicks
 @see VCD
*/
public interface TimeCallback
{
    /**
     Callback function that {@link vcd.VCD#readValuesFromVCD} looks for when
     processing the values section of a VCD file.
     @author Matthew Hicks
     @param pTime Time of the value update.
     @see VCD
     */
    void timeUpdate(long pTime);
}