# ProcessVCD
Java package for processing Value Change Dumps (VCD) from hardware simulations

testVCD.java is a test program that exercises many parts of the VCD package. testVCD outputs, to text files, a histogram of signal toggles for every signal in the passed VCD file, broken into 1% chunks.

To build the javadoc: javadoc -public vcd

To compile: javac testVCD.java

To run: java testVCD vcdFile.vcd[.gz]

To make a cool video of the switching activity of the design (requires gnuplot and ffmpeg): sh makeHists.sh

Feedback and improvement is encouraged.
