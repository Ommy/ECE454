#!/usr/bin/env bash
cd build

echo "Spawning 20 BE nodes"

for i in `seq 1 20` ;
do
    pport=$((i+14580))
    mport=$((i+15670))
    ncores=$(( ( RANDOM % 50 )  + 1 ))
    echo "Password Port: $pport"
    echo "Management Port: $mport"
    echo "Number of Cores: $ncores"
    (java -classpath "../lib/libthrift-0.9.1.jar:../lib/slf4j-simple-1.7.12.jar:../lib/slf4j-api-1.7.12.jar:../lib/jbcrypt.jar:../dist/lib/A1-20150528.jar:../build/" servers.BEServer -pport $pport -mport $mport -ncores $ncores -seeds localhost:1331,localhost:1332,localhost:1333 & )
done
