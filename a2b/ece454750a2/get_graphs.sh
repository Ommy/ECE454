#!/usr/bin/env bash

check_file="graph100K_A.txt"
if [ -f "$check_file" ]
then
    echo "graph100K_A.txt found, skipping"
else
    echo "graph100K_A.txt not found, downloading"
    rm graph*.txt
    wget http://ece.uwaterloo.ca/~wgolab/graphs.tar.gz
    tar -xzvf graphs.tar.gz
    rm graphs.tar.gz
fi

rm graph*.sort
wget https://ece.uwaterloo.ca/~wgolab/graph1K_B.sort
wget https://ece.uwaterloo.ca/~wgolab/graph1K_C.sort
wget https://ece.uwaterloo.ca/~wgolab/graph10K_B.sort
wget https://ece.uwaterloo.ca/~wgolab/graph10K_C.sort
wget https://ece.uwaterloo.ca/~wgolab/graph100K_A.sort
wget https://ece.uwaterloo.ca/~wgolab/graph100K_B.sort
wget https://ece.uwaterloo.ca/~wgolab/graph100K_C.sort
wget https://ece.uwaterloo.ca/~wgolab/graph1M_B.sort
