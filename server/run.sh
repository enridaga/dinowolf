#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $DIR
cachedir="$DIR/dinowolf-http-caching"
spotlight="http://anne.kmi.open.ac.uk/rest/annotate"
java -jar -Dlog4j.configurationFile=src/test/resources/log4j2.xml -Ddinowolf.dinowolf.httpcache="$cachedir" -Ddinowolf.spotlight="$spotlight"  target/server-0.0.1-SNAPSHOT.jar "$@"
