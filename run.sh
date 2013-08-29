#!/bin/sh
mvn exec:java -Dexec.mainClass="edu.mit.civic.clavin.server.GeoServer" -Dexec.args="-Xmx2g"
