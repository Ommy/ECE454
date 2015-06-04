#!/usr/bin/env bash

java  -Xmx128m -Xss32m -cp "dist/lib/ece454750s15a1.jar:lib/*" clients.JavaFEClient $*
