CLIFF: Extract Named Entities and Geoparse the News
===================================================

CLIFF is a lightweight server to allow HTTP requests to the Stanford Named Entity 
Recognized and a modified [CLAVIN 2.0.0 geoparser](http://clavin.bericotechnologies.com).  
It allows you to submit unstructured text over HTTP and a receive in reply JSON 
results with information about organizations mentioned, locations mentioned, 
people mentioned, and countries the text is "about".  The geoparsing is tuned 
to identify cities, states and countries.

## Developing

You need maven and java (1.7).  We develop in Eclipse Kepler: Java EE.

You need to build CLAVIN in order to build the Geonames Gazetteer Index for geoparsing. 
The idea is that you build all that, and then create a symlink at `/etc/cliff2/IndexDirectory` 
to the CLAVIN index you just built.

### Tomcat Setup

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

### Building and Deploying to Tomcat

First make sure tomcat is running (ie. `catalina run`). Now run `mvn tomcat7:deploy -DskipTests` 
to deploy the app, or `mvn tomcat7:redeploy -DskipTests` to redeploy once you've already got 
the app deployed.

### Options

You can configure how CLIFF runs by editing the following properties in the `src/main/resources/cliff.properties` 
file.

#### ner.modelToUse

Controls which Stanford NER Model to use while extracting entities:

| Value | Default | Model | Notes |
| ----- | ------- | ----- | ----- |
|ENGLISH_ALL_3CLASS|*|english.all.3class.distsim.crf|Quick, but doesn't catch all demonyms|
|ENGLISH_CONLL_4CLASS||english.conll.4class.distsim.crf|Catches most demonyms, but is about 30% slower|

## Using

To test it out, hit this url in a browser and you should get some JSON back:

```
http://localhost:8080/CLIFF-2.0.0/parse/text?q=This is some text about New York City, and maybe about Accra as well, and maybe Boston as well.
```

Of course, when you use this in a script you should do an HTTP POST, not a GET!

### Optional Arguments

If you instance of CLIFF is using the default `english.all.3class.distsim.crf` model, you can force it do 
manual really slow extra parsing to catch more demonyms by including a `replaceAllDemonyms=true` parameter 
on your query

## Testing

We have a number of unit tests that can be run with `mvn test`.

## Releasing

To build a release first update the version numbers in the `pom.xml` file, and in 
`org.mediameter.cliff.ParseManager`. Then to create the WAR file, run `mvn package -DskipTests`.

### Deploying on Ubuntu

1. First make sure you have java7: `sudo apt-get install openjdk-7-jdk`
2. First install tomcat7: `sudo apt-get install tomcat7`.
3. Point tomcat at the correct java: open `/etc/default/tomcat7` then uncomment and change the `JAVA_HOME` var to `/usr/lib/jvm/java-7-openjdk-amd64`
4. Increase tomcat's memory: open `/etc/default/tomcat7` and change the `JAVA_OPTS` var to inclue something like `-Xmx4024m`
5. Put your war in the right place: on Ubuntu this is `/var/lib/tomcat7/webapps/`
