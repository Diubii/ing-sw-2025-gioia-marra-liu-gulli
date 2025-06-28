#!/bin/bash

# Per impostare UTF-8
export LANG=it_IT.UTF-8
export LC_ALL=it_IT.UTF-8

JARPATH="$(realpath "../..")"
JARNAME=GalaxyTrucker-Server.jar

java -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -jar "$JARPATH/$JARNAME"
