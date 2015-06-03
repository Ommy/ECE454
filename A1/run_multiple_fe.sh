#!/usr/bin/env bash

echo "Spawning 10 FE clients"

for i in `seq 1 10` ;
do
    pport=$((i+14560))
    mport=$((i+1330))
    ncores=$(( ( RANDOM % 3 )  + 1 ))
    echo "Password Port: $pport"
    echo "Management Port: $mport"
    echo "Number of Cores: $ncores"
    (java -Xmx1024M -cp "dist/lib/ece454750s15a1.jar:lib/*" ece454750s15a1.FEServer -pport $pport -mport $mport -ncores $ncores -seeds localhost:1331,localhost:1332,localhost:1333 &)
done
