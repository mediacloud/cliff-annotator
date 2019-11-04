CLIFF: Extract Named Entities and Geoparse the News
===================================================

CLIFF is a lightweight server to allow HTTP requests to the Stanford Named Entity 
Recognizer and a modified [CLAVIN 2.1.0 geoparser](http://clavin.bericotechnologies.com).  
It allows you to submit unstructured text over HTTP and a receive in reply JSON 
results with information about organizations mentioned, locations mentioned, 
people mentioned, and countries the text is "about".  The geoparsing is tuned 
to identify cities, states and countries.

# Installing

You can try CLIFF out on our public website: http://cliff.mediacloud.org.
We don't host a public installation of CLIFF for you to use.  If you want to install and 
use CLIFF, @ahalterman created [an awesome vagrant script](https://github.com/c4fcm/CLIFF-up) 
that will install it to a virtual host you can use.  Or check out @johnb30's 
[docker config](https://github.com/johnb30/cliff-docker).  Use one of those to get this 
installed quickly and easily.

If you want to access CLIFF's results from Python, use our 
[Python Client API Library](https://github.com/c4fcm/CLIFF-API-Client):
```
pip install mediacloud-cliff
```

# Using

To test it out, hit this url in a browser and you should get some JSON back:

```
http://localhost:8080/cliff-2.6.0/parse/text?q=This is some text about New York City, and maybe about Accra as well, and maybe Boston as well.
```

Of course, when you use this in a script you should do an HTTP POST, not a GET!

## Public API Endpoints

###/parse/text

The reason CLIFF exists! This parses some text and returns the entities mentioned (people, places and organizations).

|Parameter|Default|Notes|
|----------|----------|----------|
|q|(required)|Raw text of a news story that you want to parse|
|replaceAllDemonyms|false|"true" if you want to count things like "Chinese" as a mention of the country China| 

Example Query:
`http://localhost:8080/CLIFF-2.1.1/parse/text?q=Some%20clever%20text%20mentioning%20places%20like%20New%20Delhi,%20and%20people%20like%20Einstein.%20Perhaps%20also%20we%20want%20mention%20an%20organization%20like%20the%20United%20Nations?`

Response:
```json
{
  "results": {
    "organizations": [
      {
        "count": 1,
        "name": "United Nations"
      }
    ],
    "places": {
      "focus": {
        "cities": [
          {
            "id": 1261481,
            "lon": 77.22445,
            "name": "New Delhi",
            "score": 1,
            "countryGeoNameId": "1269750",
            "countryCode": "IN",
            "featureCode": "PPLC",
            "featureClass": "P",
            "stateCode": "07",
            "lat": 28.63576,
            "stateGeoNameId": "1273293",
            "population": 317797
          }
        ],
        "states": [
          {
            "id": 1273293,
            "lon": 77.1,
            "name": "National Capital Territory of Delhi",
            "score": 1,
            "countryGeoNameId": "1269750",
            "countryCode": "IN",
            "featureCode": "ADM1",
            "featureClass": "A",
            "stateCode": "07",
            "lat": 28.6667,
            "stateGeoNameId": "1273293",
            "population": 16787941
          }
        ],
        "countries": [
          {
            "id": 1269750,
            "lon": 79,
            "name": "Republic of India",
            "score": 1,
            "countryGeoNameId": "1269750",
            "countryCode": "IN",
            "featureCode": "PCLI",
            "featureClass": "A",
            "stateCode": "00",
            "lat": 22,
            "stateGeoNameId": "",
            "population": 1173108018
          }
        ]
      },
      "mentions": [
        {
          "id": 1261481,
          "lon": 77.22445,
          "source": {
            "charIndex": 40,
            "string": "New Delhi"
          },
          "name": "New Delhi",
          "countryGeoNameId": "1269750",
          "countryCode": "IN",
          "featureCode": "PPLC",
          "featureClass": "P",
          "stateCode": "07",
          "confidence": 1,
          "lat": 28.63576,
          "stateGeoNameId": "1273293",
          "population": 317797
        }
      ]
    },
    "people": [
      {
        "count": 1,
        "name": "Einstein"
      }
    ]
  },
  "status": "ok",
  "milliseconds": 4004,
  "version": "2.6.0"
}
```

###/geonames

A convenience method to help you lookup places by their geonames ids.

|Parameter|Default|Notes|
|----------|----------|----------|
|id|(required)|The unique id that identifies a place in the [geonames.org](geonames.org) database|

Example Query:
`http://localhost:8080/cliff-2.6.0/geonames?id=4930956`

Response:
```json
{
  "results": {
    "id": 4930956,
    "lon": -71.05977,
    "name": "Boston",
    "countryGeoNameId": "6252001",
    "countryCode": "US",
    "featureCode": "PPLA",
    "featureClass": "P",
    "parent": {
      "id": 4952349,
      "lon": -71.06575,
      "name": "Suffolk County",
      "countryGeoNameId": "6252001",
      "countryCode": "US",
      "featureCode": "ADM2",
      "featureClass": "A",
      "parent": {
        "id": 6254926,
        "lon": -71.10832,
        "name": "Massachusetts",
        "countryGeoNameId": "6252001",
        "countryCode": "US",
        "featureCode": "ADM1",
        "featureClass": "A",
        "parent": {
          "id": 6252001,
          "lon": -98.5,
          "name": "United States",
          "countryGeoNameId": "6252001",
          "countryCode": "US",
          "featureCode": "PCLI",
          "featureClass": "A",
          "stateCode": "00",
          "lat": 39.76,
          "stateGeoNameId": "",
          "population": 310232863
        },
        "stateCode": "MA",
        "lat": 42.36565,
        "stateGeoNameId": "6254926",
        "population": 6433422
      },
      "stateCode": "MA",
      "lat": 42.3555,
      "stateGeoNameId": "6254926",
      "population": 722023
    },
    "stateCode": "MA",
    "lat": 42.35843,
    "stateGeoNameId": "6254926",
    "population": 617594
  },
  "status": "ok",
  "version": "2.6.0"
}
```

###/extract

A convenience method to help you get the raw text of the story from a URL.  This uses the [boilerpipe](https://code.google.com/p/boilerpipe/) library.

|Parameter|Default|Notes|
|----------|----------|----------|
|url|(required)|The url of a news story to extract the text of|

Example Query:
`http://localhost:8080/cliff-2.6.0/extract?url=http://www.theonion.com/articles/woman-thinks-she-can-just-waltz-back-into-work-aft,38349/`

Response:
```json
{
  "results": {
    "text": "Business\nPresented by\nWoman Thinks She Can Just Waltz Back Into Work After Maternity Leave Without Bringing Baby To Office\nClose\nVol 51 Issue 13  \u00b7  Local \u00b7 Workplace \u00b7 Parents \u00b7 Kids \u00b7 After Birth\nKENWOOD, OH\u2014Saying she has a lot of nerve to try and pull something like this, employees of insurance agency Boland & Sons told reporters Wednesday that coworker Emily Nelson seems to believe she can just waltz back into work after her maternity leave without once bringing her baby into the office. \u201cI don\u2019t know where she gets off thinking she doesn\u2019t need to come in here with that baby strapped around her in a bjorn,\u201d said Greg Sheldrick, adding that Nelson is out of her goddamn mind if she seriously believes showing off a few measly pictures of the newborn on her cell phone is an adequate substitute for bringing him around to meet everyone in their department. \u201cShe\u2019s been back for three weeks already, so the grace period is over. She needs to come in with that baby in a stroller, roll it by my desk, and say \u2018Somebody wants to say hello,\u2019 or, frankly, she might as well never show her face here again. Seriously, every single person here better get a chance to lean in and smile at that baby, and God help her if she shows up the rest of this week empty-handed.\u201d Sheldrick reportedly expressed equal astonishment that Nelson\u2019s husband thinks he can get away with not once arriving with the infant to pick up his wife from work.\nShare This Story:\nSign-up For The Onion's Newsletter\nGive your spam filter something to do.\nDaily Headlines\nThe Onion is not intended for readers under 18 years of age.\n\u00a9 Copyright 2015 Onion Inc. All rights reserved\n",
    "title": "Woman Thinks She Can Just Waltz Back Into Work After Maternity Leave Without Bringing Baby To Office - The Onion - America's Finest News Source",
    "url": "http:\/\/www.theonion.com\/articles\/woman-thinks-she-can-just-waltz-back-into-work-aft,38349\/"
  },
  "status": "ok",
  "milliseconds": 625,
  "version": "2.6.0"
}
```

## Options

You can configure how CLIFF runs by editing environment variables.

 * `INDEX_PATH` - controls where CLIFF will look for the geonames index (defaults to `/etc/cliff2/IndexDirectory` for reverse compatability)

# Developing

You need maven and java (1.8).  We develop in Eclipse Kepler: Java EE.

## Eclipse & Java on Mac
You might need to update your version of the JRE for developing on Mac. If so, you can do:
```
brew update
brew cask install Java
```

## Install CLAVIN

You need to download and install the latest version of CLAVIN in order to build the Geonames Gazetteer Index 
for geoparsing. The idea is that you build all that, and then create a symlink at 
`/etc/cliff2/IndexDirectory` to the CLAVIN index you just built.

Instead of building the CLAVIN geonames index yourself, consider downloading a prebuilt one from [`mediacloud-clavin-build-geonames-index` repository's releases page](https://github.com/berkmancenter/mediacloud-clavin-build-geonames-index/releases).

To build, run `mvn` in the top level directory.  You can do `mvn package -DskipTests` and then you will get a 
.war file in `webapp/target/`.

## Tomcat Setup

CLIFF is setup to be run inside a Java servlet container (ie. Tomcat 8).  For development 
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

## Building and Deploying to Tomcat

First make sure tomcat is running (ie. `catalina run`). Now run `mvn tomcat7:deploy -DskipTests` 
to deploy the app, or `mvn tomcat7:redeploy -DskipTests` to redeploy once you've already got 
the app deployed.

## Pluggable Entity Extraction

CLIFF leverages a pluggable entity extractor(s) via a java SPI system. This works by discovering
which providers implement the EntityExtractor interface on the classpath. To work, the jar
containing the entity extractor must exist in the classpath of the CLIFF Webapp. By default, this
project utilizes a maven profile which will select the stanford-ner model by default. However,
the Stanford NER leverages a GNU / Commercial License.

###Running the MITIE NER
Download and compile the MITIE NER package for your system following the instructions at
 [MITIE](https://github.com/mit-nlp/MITIE). As MITIE is not published to a maven repo, prior to
 compiling CLIFF you must install MITIE via mvn such as
 `mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=$PWD/javamitie.jar -DgroupId=edu.mit.ll.mitie -DartifactId=mitie -Dversion=0.4 -Dpackaging=jar`
Once compiled, the MITIE binary library must also be on your tomcat / java's `java.libray.path`. You
can either set this on your system to the folder of the MITIE binary or copy the MITIE binary to
`$JAVA_HOME/jre/lib/`. MITIE requires a model which is a fairly large download. Follow the instructions
on the MITIE site to retrieve this and point `cliff.properties` at the location you choose to store this
in your deployment.

The final step is during your build, you must disable the stanford-ner maven
profile and activate the mitie profile. For example `mvn -P mitie,\!stanford-ner clean install`,
`mvn -P mitie,\!stanford-ner tomcat7:run`. Cliff will then use MITIE instead of Stanford NER.

##Using your own NER
To use your own NER, create a mvn module modeled after the MITIE example. You must modify the
webapp/pom.xml for a correct profile which includes the MITIE NER sub-module and includes the
transitive depenencies of your NER system. Then make sure you install any modules you need for your
NER system and can activate the module as described in the MITIE section.

## Testing

We have a number of unit tests that can be run with `mvn test`.

## Releasing

To build a release:
1. first update the version numbers in all the `pom.xml` files (`pom.xml`, `webapp/pom.xml`, `common/pom.xml`, `stanford-entity-extractor/pom.xml` ) 
2. also update the version number in `org.mediacloud.cliff.ParseManager`
3. to create the WAR file, run `mvn package`
4. update the examples in `README.md`
5. tag the release with the version number `vX.Y.Z`
6. author a new release for that tag on GitHub, write a description of the changes, and upload the .war 

## Deploying on Ubuntu

We run our servers on Ubuntu - here's some tips for deploying to that type of server:

1. First make sure you have java7: `sudo apt-get install openjdk-7-jdk`
2. First install tomcat7: `sudo apt-get install tomcat7`.
3. Point tomcat at the correct java: open `/etc/default/tomcat7` then uncomment and change the `JAVA_HOME` var to `/usr/lib/jvm/java-7-openjdk-amd64`
4. Increase tomcat's memory: open `/etc/default/tomcat7` and change the `JAVA_OPTS` var to inclue something like `-Xmx4024m`
5. Put your war in the right place: on Ubuntu this is `/var/lib/tomcat7/webapps/`
