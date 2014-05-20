#!/usr/bin/env bash

GRADLE_OPTS="-Xms128m -Xmx512m -XX:MaxPermSize=512m $GRADLE_OPTS"

gradle/build_sds $@
