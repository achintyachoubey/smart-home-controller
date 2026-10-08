#!/bin/sh
# Compiles the program into out/ and starts it. Run from the project folder.
set -e
cd "$(dirname "$0")"
rm -rf out
mkdir out
find src -name "*.java" > sources.txt
if ! javac -Xlint:all -d out @sources.txt; then
    rm -f sources.txt
    exit 1
fi
rm -f sources.txt
java -cp out com.smarthome.Main
