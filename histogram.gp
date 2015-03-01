set terminal png enhanced 20; 
set size 1.0,1.0;
set output 'histogram.png';
set grid xtics ytics;
set xlabel 'Toggle Rate';
set ylabel "Proportion of Signals";
unset key;
set yrange [0.0:0.03]
set xrange [0:0.5]

plot 'histogram.txt' using 3:4 with boxes lc rgb "blue" linetype 1 linewidth 4 title 'Base' ;
