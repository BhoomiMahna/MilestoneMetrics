@ECHO OFF
REM ----------------------------------------------------------------------------
REM Maven Wrapper startup batch script for Windows
REM
REM maven-wrapper.jar 3.x has no Main-Class manifest entry. It must be
REM invoked via -classpath, not -jar.
REM
REM Maven 3.3.1+ requires -Dmaven.multiModuleProjectDirectory to be set.
REM NOTE: %DP0% ends with a backslash. A path ending in \ inside a quoted
REM string (e.g. "C:\foo\") terminates the quote early because the backslash
REM escapes the closing quote. Strip the trailing backslash before quoting.
REM ----------------------------------------------------------------------------
SETLOCAL

SET DP0=%~dp0

REM Strip trailing backslash from DP0 for safe quoting
SET MAVEN_PROJECTBASEDIR=%DP0:~0,-1%

SET MAVEN_WRAPPER_JAR=%DP0%.mvn\wrapper\maven-wrapper.jar
SET DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar

REM Resolve java executable
IF "%JAVA_HOME%"=="" (
    SET JAVA_EXE=java
) ELSE (
    SET JAVA_EXE=%JAVA_HOME%\bin\java
)

REM Download wrapper JAR if missing
IF NOT EXIST "%MAVEN_WRAPPER_JAR%" (
    ECHO Downloading Maven Wrapper JAR...
    powershell -Command "Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%MAVEN_WRAPPER_JAR%'"
    IF ERRORLEVEL 1 (
        ECHO ERROR: Failed to download Maven Wrapper JAR.
        EXIT /B 1
    )
)

REM Launch wrapper via -classpath (no Main-Class in manifest)
"%JAVA_EXE%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" -classpath "%MAVEN_WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*

EXIT /B %ERRORLEVEL%
