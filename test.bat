@echo off
rem Compiles src\ and test\ together into out-test\ and runs every test.
cd /d "%~dp0"
if exist out-test rmdir /s /q out-test
mkdir out-test
dir /s /b src\*.java test\*.java > sources.txt
javac -Xlint:all -d out-test @sources.txt
if errorlevel 1 (
    del sources.txt
    exit /b 1
)
del sources.txt
java -cp out-test com.smarthome.TestRunner
if errorlevel 1 exit /b 1
