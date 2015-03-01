#!/bin/sh

for i in {1..100}
do
    cp hist_"$i".txt histogram.txt
    gnuplot histogram.gp
    mv histogram.png hist_"$i".png
done

rm histogram.txt

ffmpeg -v quiet -i hist_%d.png -c:v libx264 -preset medium -qp 0 -r 24 -y histogram.mkv
