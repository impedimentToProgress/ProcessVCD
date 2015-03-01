# ProcessVCD
Java package for processing Value Change Dumps (VCD) from hardware simulations

testVCD.java is a test program that exercises many parts of the VCD package. testVCD outputs, to text files, a histogram of signal toggles for every signal in the passed VCD file, broken into 1% chunks.

To compile: javac testVCD.java

To run: java testVCD vcdFile.vcd[.gz]



Feedback and improvement is encouraged.
