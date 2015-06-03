#!/usr/bin/env bash

echo "Spawning 10 FE clients"

for i in `seq 1 10`;
do
    pport=$((i+4848))
    mport=$((i+6719))
    ncores=$(( ( RANDOM % 3 )  + 1 ))
    echo "Password Port: $pport"
    echo "Management Port: $mport"
    echo "Number of Cores: $ncores"
    (java -Xmx64m -cp "dist/lib/ece454750s15a1.jar;lib/*" ece454750s15a1.FEServer -pport $pport -mport $mport -ncores $ncores -seeds localhost:6722,localhost:6720,localhost:6721 &)
done
