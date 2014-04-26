package org.mediameter.cliff;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mediameter.cliff.extractor.ExtractedEntities;
import org.mediameter.cliff.extractor.StanfordThreeClassExtractor;
import org.mediameter.cliff.muck.MuckUtils;
import org.mediameter.cliff.orgs.ResolvedOrganization;
import org.mediameter.cliff.people.ResolvedPerson;
import org.mediameter.cliff.places.CustomLuceneLocationResolver;
import org.mediameter.cliff.places.aboutness.AboutnessStrategy;
import org.mediameter.cliff.places.aboutness.FrequencyOfMentionAboutnessStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.resolver.LocationResolver;
import com.bericotech.clavin.resolver.ResolvedLocation;
import com.google.gson.Gson;

/**
 * Singleton-style wrapper around a GeoParser.  Call GeoParser.locate(someText) to use this class.
 */
public class ParseManager {

    // increment each time we change an algorithm or json structure so we know when parsed results already saved in a DB are stale!
    static final String PARSER_VERSION = "0.6";
    
    private static final Logger logger = LoggerFactory.getLogger(ParseManager.class);

    public static EntityParser parser = null;
    
    public static StanfordThreeClassExtractor peopleExtractor = null;

    private static Gson gson = new Gson();
    
    private static LocationResolver resolver;   // HACK: pointer to keep around for stats logging
    
    private static AboutnessStrategy aboutness = new FrequencyOfMentionAboutnessStrategy();
    //private static AboutnessStrategy aboutness = new LocationScoredAboutnessStrategy();
    
    private static final String PATH_TO_GEONAMES_INDEX = "/etc/cliff/IndexDirectory";
    
    // these two are the statuses used in the JSON responses
    private static final String STATUS_OK = "ok";
    private static final String STATUS_ERROR = "error";
    
    /**
     * Public api method - call this statically to extract locations from a text string 
     * @param text  unstructured text that you want to parse for location mentions
     * @return      json string with details about locations mentioned
     */
    public static String parseFromText(String text) {
        if(text.trim().length()==0){
            return getErrorText("No text");
        }
        try {
            ExtractedEntities entities = extractAndResolve(text);
            return parseFromEntities(entities);
        } catch (Exception e) {
            return getErrorText(e.toString());
        }
    }
    
    public static String parseFromNlpJson(String nlpJsonString){
        if(nlpJsonString.trim().length()==0){
            return getErrorText("No text");
        }
        try {
            ExtractedEntities entities = MuckUtils.entitiesFromJsonString(nlpJsonString);
            entities = getParserInstance().resolve(entities);;
            return parseFromEntities(entities);
        } catch (Exception e) {
            return getErrorText(e.toString());
        } 
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })  // I'm generating JSON... don't whine!
    public static String parseFromEntities(ExtractedEntities entities){
        if (entities == null){
            return getErrorText("No place or person entitites detected in this text.");
        } 
        HashMap results = new HashMap();
        results.put("status",STATUS_OK);
        results.put("version", PARSER_VERSION);
        
        // assemble the "where" results
        HashMap placeResults = new HashMap();
        ArrayList resolvedPlaces = new ArrayList();
        for (ResolvedLocation resolvedLocation: entities.getResolvedLocations()){
            HashMap loc = writeResolvedLocationToHash(resolvedLocation);
            resolvedPlaces.add(loc);
        }
        placeResults.put("all",resolvedPlaces);
        
        HashMap aboutResults = new HashMap();
        if (resolvedPlaces.size() > 0){
            aboutResults.put("countries", aboutness.selectCountries(entities.getResolvedLocations()));
            aboutResults.put("states", aboutness.selectStates(entities.getResolvedLocations()));
            ArrayList primaryCities = new ArrayList();            
            for (ResolvedLocation resolvedLocation: aboutness.selectCities(entities.getResolvedLocations())){
                HashMap loc = writeResolvedLocationToHash(resolvedLocation);
                primaryCities.add(loc);
            }
            aboutResults.put("cities",primaryCities);
        }
        placeResults.put("about",aboutResults);
        results.put("places",placeResults);

        // assemble the "who" results
        List<ResolvedPerson> resolvedPeople = entities.getResolvedPeople();
        List<HashMap> personResults = new ArrayList<HashMap>();
        for (ResolvedPerson person: resolvedPeople){
            HashMap sourceInfo = new HashMap();
            sourceInfo.put("name", person.getName());
            sourceInfo.put("count", person.getOccurenceCount());
            personResults.add(sourceInfo);
        }
        results.put("people",personResults);

        // assemble the org results
        List<ResolvedOrganization> resolvedOrganizations = entities.getResolvedOrganizations();
        List<HashMap> organizationResults = new ArrayList<HashMap>();
        for (ResolvedOrganization organization: resolvedOrganizations){
            HashMap sourceInfo = new HashMap();
            sourceInfo.put("name", organization.getName());
            sourceInfo.put("count", organization.getOccurenceCount());
            organizationResults.add(sourceInfo);
        }
        results.put("organizations",organizationResults);

        // return it as JSON
        return gson.toJson(results);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static HashMap writeResolvedLocationToHash(ResolvedLocation resolvedLocation){
    	HashMap loc = new HashMap();
    	int charIndex = resolvedLocation.location.position;
    	GeoName place = resolvedLocation.geoname;
        loc.put("confidence", resolvedLocation.confidence); // low is good
        loc.put("id",place.geonameID);
        loc.put("name",place.name);
        String primaryCountryCodeAlpha2 = ""; 
        if(place.primaryCountryCode!=CountryCode.NULL){
            primaryCountryCodeAlpha2 = place.primaryCountryCode.toString();
        }
        String admin1Code = "";
        
        if(place.admin1Code !=null){
            admin1Code = place.admin1Code;
        }
        String featureCode = place.featureCode.toString();
        loc.put("featureClass", place.featureClass.toString());
        loc.put("featureCode", featureCode);
        loc.put("population", place.population);
        loc.put("stateCode", admin1Code);
        loc.put("countryCode",primaryCountryCodeAlpha2);
        loc.put("lat",place.latitude);
        loc.put("lon",place.longitude);
        HashMap sourceInfo = new HashMap();
        sourceInfo.put("string",resolvedLocation.location.text);
        sourceInfo.put("charIndex",charIndex);  
        loc.put("source",sourceInfo);
        
    	return loc;
    	
    }
    public static ExtractedEntities extractAndResolve(String text){
        try {
            return getParserInstance().extractAndResolve(text);
        } catch (Exception e) {
            logger.error("Lucene Resolving Error: "+e.toString());
        }
        return new ExtractedEntities();
    }

    /**
     * We want all error messages sent to the client to have the same format 
     * @param msg
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })  // I'm generating JSON... don't whine!
    public static String getErrorText(String msg){
        HashMap info = new HashMap();
        info.put("status",STATUS_ERROR);
        info.put("details",msg);
        return gson.toJson(info);
    }
    
    public static void logStats(){
        if(resolver!=null){
            ((CustomLuceneLocationResolver) resolver).logStats();
        }
    }
    
    /**
     * Lazy instantiation of singleton parser
     * @return
     * @throws Exception
     */
    private static EntityParser getParserInstance() throws Exception{

        if(parser==null){

            // use the Stanford NER location extractor?
            StanfordThreeClassExtractor locationExtractor = new StanfordThreeClassExtractor();                
            
            int numberOfResultsToFetch = 10;
            boolean useFuzzyMatching = false;
            resolver = new CustomLuceneLocationResolver(new File(PATH_TO_GEONAMES_INDEX), 
                    numberOfResultsToFetch);

            parser = new EntityParser(locationExtractor, resolver, useFuzzyMatching);
                        
            logger.info("Created parser successfully");
        }
        
        return parser;
    }

    public static LocationResolver getResolver() throws Exception {
        ParseManager.getParserInstance();
        return resolver;
    }

    public static AboutnessStrategy getAboutness() throws Exception {
        ParseManager.getParserInstance();
        return aboutness;
    }

    static {
     // instatiate and load right away
        try {
            ParseManager.getParserInstance();  
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("Unable to create parser "+e);
        }
    }
    
}
