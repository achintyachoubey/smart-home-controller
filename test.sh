#!/bin/sh
# Compiles src/ and test/ together into out-test/ and runs every test.
# Exits with a non-zero status if compilation or any test fails.
set -e
cd "$(dirname "$0")"
rm -rf out-test
mkdir out-test
find src test -name "*.java" > sources.txt
if ! javac -Xlint:all -d out-test @sources.txt; then
    rm -f sources.txt
    exit 1
fi
rm -f sources.txt
java -cp out-test com.smarthome.TestRunner
