CLIFF: News Parsing Server
--------------------------

CLIFF is a lightweight server to allow socket-based requests to the Stanford Named Entity 
Recognized and CLAVIN geoparser.  It allows you to submit unstructured text over a socket 
and a receive in reply JSON results with information about locations mentioned, people 
mentioned, and countries the text is "about".  The geoparsing is tuned to identify cities 
and countries.

Developing
----------

You need maven and java (1.7).  We develop in Eclipse Kepler: Java EE.

You need to build CLAVIN in order to build the Geonames Gazetteer Index for geoparsing. 
The idea is that you build all that, and then create a symlink at `/etc/cliff/IndexDirectory` 
to the CLAVIN index you just built.

Execution
---------

CLIFF is setup to be run inside a Java servlet container (ie. Tomcat).  You can run it inside 
of Eclipse like this.

Use
---

To test it out, hit this url in a browser and you should get some JSON back:

```
http://localhost:8080/CLIFF-0.4/parse/text?q=This is some text about New York City, and maybe about Harari as well
```

Testing
-------

```
mvn test
```

Releasing
---------

CLIFF is setup to be run inside a Java servlet container (ie. Tomcat).  To create the 
WAR file, run `mvn package`.

Copy `target/CLIFF-[version].war` to your Tomcat release dir.  Our default development 
environment is running Tomcat installed via HomeBrew on Mac OS X.  With that in mind, 
you can simply run `deploy-to-tomcat.sh` and that will copy over the WAR over for you.
