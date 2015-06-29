#!/usr/bin/env bash

check_file="graph100K_A.txt"
if [ -f "$check_file" ]
then
    echo "graph100K_A.txt found, skipping"
else
    echo "graph100K_A.txt not found, downloading"
    rm graph*.txt
    wget --no-check-certificate http://ece.uwaterloo.ca/~wgolab/graphs.tar.gz
    tar -xzvf graphs.tar.gz
    rm graphs.tar.gz
fi

rm graph*.sort
sorts=($(ls graph*.txt | sed 's/.txt/.sort/'))

echo "Getting sorted files..."

for i in "${sorts[@]}"
do
    2>/dev/null 1>&2 wget --no-check-certificate https://ece.uwaterloo.ca/~wgolab/$i &
done
pid=$!
spin='-\|/'

i=0
while kill -0 $pid 2>/dev/null
do
    i=$(( (i+1) %4 ))
    printf "\r${spin:$i:1}"
    sleep .1
done
