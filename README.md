CLAVIN Server
-------------

A lightweight server to allow socket-based requests to the CLAVIN geoparser.  It allows you to submit 
unstructured text over a socket and a receive in reply JSON results with information about any locations 
that are mentoned (name, lat/lon, confidence, etc).

Installation
------------

You need maven and java.

You also need to build CLAVIN in order to build the Geonames Gazetteer Index.  The idea is that you build all 
that, and then create a symlink in this directory from `./IndexDirectory` to the index you just built.

If you are using Eclipse, don't forget to do `mvn eclipse:eclipse` in this directory to finish setting things up
correctly.

On Ubuntu, make sure you do this:
```
sudo apt-get install maven2
sudo apt-get install openjdk-7-jdk
```

Running
-------

Running this command will start up a server on port 8080.

```
mvn exec:java -Dexec.mainClass="edu.mit.civic.clavin.server.GeoServer" -Dexec.args="-Xmx2g"
```

Use
---

To test it out, hit this url in a browser and you should get some JSON back.

```
http://localhost:8080/parse?text=This is some text about New York City, and maybe about Harari as well
```

and to get some basic status hit this url:

```
http://localhost:8080/status
```

Testing
-------

```
mvn test
```

```
mvn exec:java -Dexec.mainClass="edu.mit.civic.clavin.aboutness.AboutnessCheck" -Dexec.args="-Xmx2g"
```
