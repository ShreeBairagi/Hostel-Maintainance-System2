@echo off
echo Compiling...
javac -encoding UTF-8 -d bin -sourcepath src src\com\hostel\gui\MainApp.java
if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)
echo Running Application...
java -cp bin com.hostel.gui.MainApp
