@ECHO OFF
SET MAVEN_WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar
SET MAVEN_WRAPPER_PROPERTIES=.mvn\wrapper\maven-wrapper.properties

IF NOT EXIST "%MAVEN_WRAPPER_JAR%" (
    FOR /F "tokens=1,* delims==" %%A IN ('findstr "^wrapperUrl=" "%MAVEN_WRAPPER_PROPERTIES%"') DO SET DOWNLOAD_URL=%%B
    curl -fsSL -o "%MAVEN_WRAPPER_JAR%" "%DOWNLOAD_URL%"
)

java -classpath "%MAVEN_WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
