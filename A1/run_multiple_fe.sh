#!/usr/bin/env bash

echo "Spawning 10 FE clients"

for i in `seq 1 2` ;
do
    pport=$((i*10000+6720))
    mport=$((i*10000+4850))
    ncores=$(( ( RANDOM % 3 )  + 1 ))
    echo "Password Port: $pport"
    echo "Management Port: $mport"
    echo "Number of Cores: $ncores"
    (java -Xss1m -Xmx1g -cp "dist/lib/ece454750s15a1.jar:lib/*" ece454750s15a1.FEServer -pport $pport -mport $mport -ncores $ncores -seeds localhost:14850 &)
done
