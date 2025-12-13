@echo off
setlocal

set "JAVA_HOME="

if exist "C:\Program Files\Android\Android Studio\jbr" (
    set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
) else if exist "C:\Program Files\Android\Android Studio\jre" (
    set "JAVA_HOME=C:\Program Files\Android\Android Studio\jre"
)

if "%JAVA_HOME%"=="" (
    echo "Could not find Android Studio JDK."
    exit /b 1
)

echo Using JDK at %JAVA_HOME%
call gradlew.bat %*
endlocal
