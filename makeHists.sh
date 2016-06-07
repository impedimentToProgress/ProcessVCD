#!/bin/sh

for i in {1..1000}; do
    cp hist_"$i".txt histogram.txt
    gnuplot histogram.gp
    mv histogram.png hist_"$i".png
done;

# Combine the png images into a video

# Version that is small and good quality
ffmpeg -v quiet -i hist_%d.png -c:v libx264 -preset medium -qp 0 -r 30 -y histogram.mkv

# Version that work well enough in PowerPoint
ffmpeg -v quiet -i hist_%d.png -f lavfi -i aevalsrc=0 -shortest -c:v mpeg1video -qscale:v 2 -c:a libmp3lame -r 30 -y histogram.mpg

for i in {1..1000}; do
    rm hist_"$i".png
done;

rm histogram.txt
