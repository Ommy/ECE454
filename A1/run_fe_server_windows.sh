#!/usr/bin/env bash
cd build
java -classpath "../lib/libthrift-0.9.1.jar;../lib/slf4j-simple-1.7.12.jar;../lib/slf4j-api-1.7.12.jar;../lib/jbcrypt.jar;../dist/lib/A1-20150528.jar;../build/" servers.FEServer $*