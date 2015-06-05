#!/usr/bin/env bash

export JAVA_TOOL_OPTIONS=-Xmx512m

java -cp "dist/lib/ece454750s15a1.jar" clients.JavaFEClient $*
