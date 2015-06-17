#!/usr/bin/env bash

export JAVA_TOOL_OPTIONS='-Xmx4g -Xss4m'

java -cp "ece454750s15a1.jar:../lib/*" clients.JavaFEClient $*
