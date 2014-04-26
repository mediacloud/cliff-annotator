CLIFF: Extract Named Entities and Geoparsing the News
-----------------------------------------------------

CLIFF is a lightweight server to allow HTTP requests to the Stanford Named Entity 
Recognized and a modified CLAVIN geoparser.  It allows you to submit unstructured text 
over HTTP and a receive in reply JSON results with information about organizations 
mentioned, locations mentioned, people mentioned, and countries the text is "about".  
The geoparsing is tuned to identify cities and countries.

Developing
----------

You need maven and java (1.7).  We develop in Eclipse Kepler: Java EE.

You need to build CLAVIN in order to build the Geonames Gazetteer Index for geoparsing. 
The idea is that you build all that, and then create a symlink at `/etc/cliff/IndexDirectory` 
to the CLAVIN index you just built.

Deployment
----------

# Setup

CLIFF is setup to be run inside a Java servlet container (ie. Tomcat7).  For development 
we use the [Maven Tomcat plugin](http://tomcat.apache.org/maven-plugin.html).  To deploy, 
add this to your `%TOMCAT_PATH%/conf/tomcat-users.xml` file:
```xml
  <role rolename="manager"/>
  <role rolename="manager-gui"/>
  <role rolename="manager-script"/>
  <user username="cliff" password="beer" roles="manager,manager-gui,manager-script"/>
```
Also add this to your `~/.m2/settings.xml`:
```xml
  <servers>
    <server>
	  <id>CliffTomcatServer</id>
      <username>cliff</username>
      <password>beer</password>
    </server>
  </servers>
```
That lets the Maven Tomcat plugin upload the WAR it builds over the website control panel.

# Building and Deploying

First make sure tomcat is running (ie. `catalina run`). Now run `mvn tomcat7:deploy -DskipTests` 
to deploy the app, or `mvn tomcat7:redeploy -DskipTests` to redeploy once you've already got 
the app deployed.

Using
-----

To test it out, hit this url in a browser and you should get some JSON back:

```
http://localhost:8080/CLIFF/parse/text?q=This is some text about New York City, and maybe about Accra as well
```

Testing
-------

We have a number of unit tests that can be run with `mvn test`.

Releasing
---------

To create the WAR file, run `mvn package -DskipTests`.
