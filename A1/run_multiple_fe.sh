#!/usr/bin/env bash
cd build

echo "Spawning 10 FE clients"

for i in `seq 1 10` ;
do
    pport=$((i+14560))
    mport=$((i+1330))
    ncores=$(( ( RANDOM % 3 )  + 1 ))
    echo "Password Port: $pport"
    echo "Management Port: $mport"
    echo "Number of Cores: $ncores"
    (java -classpath "../lib/libthrift-0.9.1.jar:../lib/slf4j-simple-1.7.12.jar:../lib/slf4j-api-1.7.12.jar:../lib/jbcrypt.jar:../dist/lib/A1-20150528.jar:../build/" servers.FEServer -pport $pport -mport $mport -ncores $ncores -seeds localhost:1331,localhost:1332,localhost:1333 &)
done
