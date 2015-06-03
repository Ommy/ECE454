#!/usr/bin/env bash

for i in `seq 1 10` ;
do
    pport=$((i+11200))
    mport=$((i+11100))
    ncores=$(( ( RANDOM % 10 )  + 1 ))
    echo "Password Port: $pport"
    echo "Management Port: $mport"
    echo "Number of Cores: $ncores"
    java -cp "dist/lib/ece454750s15a1.jar;lib/*" clients.ManagementClient -pport $pport -mport $mport -ncores $ncores -seeds localhost:6720,localhost:6721,localhost:6722
done

