CLIFF: Extract Named Entities and Geoparse the News
===================================================

CLIFF is a lightweight server to allow HTTP requests to the Stanford Named Entity 
Recognized and a modified [CLAVIN geoparser v1.1.x](https://github.com/Berico-Technologies/CLAVIN/tree/stable/1.1.x).  
It allows you to submit unstructured text over HTTP and a receive in reply JSON 
results with information about organizations mentioned, locations mentioned, 
people mentioned, and countries the text is "about".  The geoparsing is tuned 
to identify cities, states and countries.

## Developing

You need maven and java (1.7).  We develop in Eclipse Kepler: Java EE.

You need to build CLAVIN in order to build the Geonames Gazetteer Index for geoparsing. 
The idea is that you build all that, and then create a symlink at `/etc/cliff/IndexDirectory` 
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
http://localhost:8080/CLIFF-1.1.0/parse/text?q=This is some text about New York City, and maybe about Accra as well, and maybe Boston as well.
```

Of course, when you use this in a script you should do an HTTP POST, not a GET!

### Public API Endpoints

####/parse/text

The reason CLIFF exists! This parses some text and returns the entities mentioned (people, places and organizations).

|Parameter|Default|Notes|
|----------|----------|----------|
|q|(required)|Raw text of a news story that you want to parse|
|replaceAllDemonyms|false|"true" if you want to count things like "Chinese" as a mention of the country China| 

Example Query:
`http://localhost:8080/CLIFF-1.4.0/parse/text?q=Some%20clever%20text%20mentioning%20places%20like%20New%20Delhi,%20and%20people%20like%20Einstein.%20Perhaps%20also%20we%20want%20mention%20an%20organization%20like%20the%20United%20Nations?`

Response:
```json
{
  "status": "ok",
  "version": "1.3.0",
  "results": {
    "organizations": [
      {
        "count": 1,
        "name": "United Nations"
      }
    ],
    "places": {
      "mentions": [
        {
          "confidence": 1,
          "name": "New Delhi",
          "countryCode": "IN",
          "featureCode": "PPLC",
          "lon": 77.22445,
          "countryGeoNameId": "1269750",
          "source": {
            "charIndex": 40,
            "string": "New Delhi"
          },
          "stateCode": "07",
          "featureClass": "P",
          "lat": 28.63576,
          "stateGeoNameId": "1273293",
          "id": 1261481,
          "population": 317797
        }
      ],
      "focus": {
        "states": [
          {
            "name": "National Capital Territory of Delhi",
            "countryCode": "IN",
            "featureCode": "ADM1",
            "lon": 77.1,
            "countryGeoNameId": "1269750",
            "score": 1,
            "stateCode": "07",
            "featureClass": "A",
            "lat": 28.6667,
            "stateGeoNameId": "1273293",
            "id": 1273293,
            "population": 15766943
          }
        ],
        "cities": [
          {
            "name": "New Delhi",
            "countryCode": "IN",
            "featureCode": "PPLC",
            "lon": 77.22445,
            "countryGeoNameId": "1269750",
            "score": 1,
            "stateCode": "07",
            "featureClass": "P",
            "lat": 28.63576,
            "stateGeoNameId": "1273293",
            "id": 1261481,
            "population": 317797
          }
        ],
        "countries": [
          {
            "name": "Republic of India",
            "countryCode": "IN",
            "featureCode": "PCLI",
            "lon": 79,
            "countryGeoNameId": "1269750",
            "score": 1,
            "stateCode": "00",
            "featureClass": "A",
            "lat": 22,
            "stateGeoNameId": "",
            "id": 1269750,
            "population": 1173108018
          }
        ]
      }
    },
    "people": [
      {
        "count": 1,
        "name": "Einstein"
      }
    ]
  },
  "milliseconds": 5
}
```

####/geonames

A convenience method to help you lookup places by their geonames ids.

|Parameter|Default|Notes|
|----------|----------|----------|
|id|(required)|The unique id that identifies a place in the [geonames.org](geonames.org) database|

Example Query:
`http://localhost:8080/CLIFF-1.4.0/geonames?id=4930956`

Response:
```json
{
  "status": "ok", 
  "version": "1.4.0", 
  "results": {
    "name": "Boston", 
    "countryCode": "US", 
    "featureCode": "PPLA", 
    "lon": -71.05977, 
    "countryGeoNameId": "6252001", 
    "stateCode": "MA", 
    "featureClass": "P", 
    "lat": 42.35843, 
    "stateGeoNameId": "6254926", 
    "id": 4930956, 
    "population": 617594
  }
}
```

####/extract

A convenience method to help you get the raw text of the story from a URL.  This uses the [boilerpipe](https://code.google.com/p/boilerpipe/) library.

|Parameter|Default|Notes|
|----------|----------|----------|
|url|(required)|The url of a news story to extract the text of|

Example Query:
`http://localhost:8080/CLIFF-1.4.0/extract?url=http://www.theonion.com/articles/woman-thinks-she-can-just-waltz-back-into-work-aft,38349/`

Response:
```json
{
  "results":{
    "text":" \n \nKENWOOD, OH—Saying she has a lot of nerve to try and pull something like this, employees of insurance agency Boland \u0026 Sons told reporters Wednesday that coworker Emily Nelson seems to believe she can just waltz back into work after her maternity leave without once bringing her baby into the office. “I don’t know where she gets off thinking she doesn’t need to come in here with that baby strapped around her in a bjorn,” said Greg Sheldrick, adding that Nelson is out of her goddamn mind if she seriously believes showing off a few measly pictures of the newborn on her cell phone is an adequate substitute for bringing him around to meet everyone in their department. “She’s been back for three weeks already, so the grace period is over. She needs to come in with that baby in a stroller, roll it by my desk, and say ‘Somebody wants to say hello,’ or, frankly, she might as well never show her face here again. Seriously, every single person here better get a chance to lean in and smile at that baby, and God help her if she shows up the rest of this week empty-handed.” Sheldrick reportedly expressed equal astonishment that Nelson’s husband thinks he can get away with not once arriving with the infant to pick up his wife from work.\n \n",
    "url":"http://www.theonion.com/articles/woman-thinks-she-can-just-waltz-back-into-work-aft,38349/"
  },
  "status":"ok",
  "milliseconds":185,
  "version":"1.4.0"
}
```

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
