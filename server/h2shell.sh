#!/bin/bash
url="jdbc:h2:file:./dinowolf-repository/metadata/dinowolf;MVCC=TRUE;DB_CLOSE_ON_EXIT=TRUE;FILE_LOCK=NO"
user=dinowolf
password=dinowolf
java -Dfile.encoding=UTF-8 -cp target/server*.jar org.h2.tools.Shell -url "$url" -user $user -password $password "$@"

