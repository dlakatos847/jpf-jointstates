
set JPF_CREATE_HOME=%~dp0..

set JVM_FLAGS=-Xmx1024m -ea

java %JVM_FLAGS% -classpath "%JPF_CREATE_HOME%\build\jpf-template.jar;%JPF_CREATE_HOME%\lib\*" gov.nasa.jpf.template.CreateProject %*
