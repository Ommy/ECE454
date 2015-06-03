#!/usr/bin/env bash

echo "Spawning 20 BE nodes"

for i in `seq 1 10` ;
do
    pport=$((i+14580))
    mport=$((i+15670))
    ncores=$(( ( RANDOM % 5 )  + 1 ))
    echo "Password Port: $pport"
    echo "Management Port: $mport"
    echo "Number of Cores: $ncores"
    (java -cp "dist/lib/ece454750s15a1.jar:lib/*" ece454750s15a1.BEServer -pport $pport -mport $mport -ncores $ncores -seeds localhost:1331,localhost:1332,localhost:1333 & )
done