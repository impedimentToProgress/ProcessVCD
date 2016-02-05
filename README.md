# ProcessVCD
Java package for processing Value Change Dumps (VCD) from hardware simulations

testVCD.java is a test program that exercises many parts of the VCD package. testVCD outputs, to text files, a histogram of signal toggles for every signal in the passed VCD file, broken into 1% chunks.

CheckVCDForCounters.java is an example program that uses SignalHistory objects to keep a detailed history of every value of every signal in the VCD file.  The output is a list of signals that behave like counters or behave like constants given the value updates in the passed VCD file.

To build the javadoc: javadoc -public vcd

To compile:
    javac testVCD.java
    javac CheckVCDForCounters.java
    
To run:
    java testVCD vcdFile.vcd[.gz]
    java CheckVCDForCounters vcdFile.vcd[.gz]

To make a cool video of the switching activity of the design (requires gnuplot and ffmpeg): sh makeHists.sh

Feedback and improvement is encouraged.
