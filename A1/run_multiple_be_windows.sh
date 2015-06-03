#!/usr/bin/env bash
echo "Spawning 10 BE nodes"

for i in `seq 1 10` ;
do
    pport=$((i+11200))
    mport=$((i+11100))
    ncores=$(( ( RANDOM % 25 )  + 1 ))
    echo "Password Port: $pport"
    echo "Management Port: $mport"
    echo "Number of Cores: $ncores"
    (java -cp "dist/lib/ece454750s15a1.jar;lib/*" ece454750s15a1.BEServer -pport $pport -mport $mport -ncores $ncores -seeds localhost:6720,localhost:6721,localhost:6722 & )
done
