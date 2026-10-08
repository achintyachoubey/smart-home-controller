@echo off
rem Compiles the program into out\ and starts it. Run from the project folder.
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > sources.txt
javac -Xlint:all -d out @sources.txt
if errorlevel 1 (
    del sources.txt
    exit /b 1
)
del sources.txt
java -cp out com.smarthome.Main
