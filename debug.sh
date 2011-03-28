#!/bin/bash
jar=$(find target/ -name "hibernate-jconsole*.jar")
java -jar $jar -J-Xdebug -J-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
