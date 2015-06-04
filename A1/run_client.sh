#!/usr/bin/env bash

java -Xss1m -Xmx1g -cp "dist/lib/ece454750s15a1.jar:lib/*" clients.JavaFEClient $*
