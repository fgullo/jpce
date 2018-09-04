#!/bin/sh

JRE_HOME="/usr/bin"
XMS_DEFAULT="1400m"
XMX_DEFAULT="1400m"


$JRE_HOME/java -Xms$XMS_DEFAULT -Xmx$XMX_DEFAULT -jar JPCE.jar

