@echo off 

set JRE_HOME="C:\Programmi\Java\jre1.6.0_03\bin"
set XMS_DEFAULT=1400
set XMX_DEFAULT=1400


echo %JRE_HOME%\java -Xms%XMS_DEFAULT%m -Xmx%XMX_DEFAULT%m -jar .\JPCE.jar

%JRE_HOME%\java -Xms%XMS_DEFAULT%m -Xmx%XMX_DEFAULT%m -jar .\JPCE.jar


pause






