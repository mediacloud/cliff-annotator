#!/bin/sh
mvn exec:java -Dexec.mainClass="edu.mit.civic.mediacloud.ParseServer" -Dexec.args="-Xmx2g"
