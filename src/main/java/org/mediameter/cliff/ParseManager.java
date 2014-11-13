package org.mediameter.cliff;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mediameter.cliff.extractor.CliffConfig;
import org.mediameter.cliff.extractor.ExtractedEntities;
import org.mediameter.cliff.extractor.SentenceLocationOccurrence;
import org.mediameter.cliff.extractor.StanfordNamedEntityExtractor;
import org.mediameter.cliff.extractor.StanfordNamedEntityExtractor.Model;
import org.mediameter.cliff.orgs.ResolvedOrganization;
import org.mediameter.cliff.people.ResolvedPerson;
import org.mediameter.cliff.places.CliffLocationResolver;
import org.mediameter.cliff.places.focus.FocusLocation;
import org.mediameter.cliff.places.focus.FocusStrategy;
import org.mediameter.cliff.places.focus.FrequencyOfMentionFocusStrategy;
import org.mediameter.cliff.util.MuckUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.gazetteer.query.Gazetteer;
import com.bericotech.clavin.gazetteer.query.LuceneGazetteer;
import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Singleton-style wrapper around a GeoParser.  Call GeoParser.locate(someText) to use this class.
 */
public class ParseManager {

    /**
     * Major: new features or capabilities
     * Minor: change in json result format
     * Revision: minor change or bug fix
     */
    static final String PARSER_VERSION = "2.0.0";
    
    private static final Logger logger = LoggerFactory.getLogger(ParseManager.class);

    public static EntityParser parser = null;
    
    public static StanfordNamedEntityExtractor peopleExtractor = null;

    private static CliffLocationResolver resolver;   // HACK: pointer to keep around for stats logging
    
    private static FocusStrategy focusStrategy = new FrequencyOfMentionFocusStrategy();
    
    public static final String PATH_TO_GEONAMES_INDEX = "/etc/cliff2/IndexDirectory";
    
    // these two are the statuses used in the JSON responses
    private static final String STATUS_OK = "ok";
    private static final String STATUS_ERROR = "error";
    
    /**
     * Public api method - call this statically to extract locations from a text string 
     * @param text  unstructured text that you want to parse for location mentions
     * @return      json string with details about locations mentioned
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static HashMap parseFromText(String text,boolean manuallyReplaceDemonyms) {
        long startTime = System.currentTimeMillis();
        HashMap results = null;
        if(text.trim().length()==0){
            return getErrorText("No text");
        }
        try {
            ExtractedEntities entities = extractAndResolve(text,manuallyReplaceDemonyms);
            results = parseFromEntities(entities);
        } catch (Exception e) {
            results = getErrorText(e.toString());
        }
        long endTime = System.currentTimeMillis();
        long elapsedMillis = endTime - startTime;
        results.put("milliseconds", elapsedMillis);
        return results;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static HashMap parseFromNlpJson(String nlpJsonString){
        long startTime = System.currentTimeMillis();
        HashMap results = null;
        if(nlpJsonString.trim().length()==0){
            return getErrorText("No text");
        }
        try {
            ExtractedEntities entities = MuckUtils.entitiesFromJsonString(nlpJsonString);
            entities = getParserInstance().resolve(entities);;
            results = parseFromEntities(entities);
        } catch (Exception e) {
            results = getErrorText(e.toString());
        } 
        long endTime = System.currentTimeMillis();
        long elapsedMillis = endTime - startTime;
        results.put("milliseconds", elapsedMillis);
        return results;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })  // I'm generating JSON... don't whine!
    public static HashMap parseFromEntities(ExtractedEntities entities){
        if (entities == null){
            return getErrorText("No place or person entitites detected in this text.");
        } 
        HashMap response = new HashMap();
        response.put("status",STATUS_OK);
        response.put("version", PARSER_VERSION);
        
        logger.debug("Adding Mentions:");
        HashMap results = new HashMap();
        // assemble the "where" results
        HashMap placeResults = new HashMap();
        ArrayList resolvedPlaces = new ArrayList();
        for (ResolvedLocation resolvedLocation: entities.getResolvedLocations()){
            HashMap loc = writeResolvedLocationToHash(resolvedLocation);
            resolvedPlaces.add(loc);
        }
        placeResults.put("mentions",resolvedPlaces);
        
        logger.debug("Adding Focus:");
        HashMap focusResults = new HashMap();
        if (resolvedPlaces.size() > 0){
            ArrayList focusLocationInfoList;
            logger.debug("Adding Country Focus:");
            focusLocationInfoList = new ArrayList<HashMap>();
            for(FocusLocation loc:focusStrategy.selectCountries(entities.getResolvedLocations())) {
                focusLocationInfoList.add( writeAboutnessLocationToHash(loc) );
            }
            focusResults.put("countries", focusLocationInfoList);
            logger.debug("Adding State Focus:");
            focusLocationInfoList = new ArrayList<HashMap>();
            for(FocusLocation loc:focusStrategy.selectStates(entities.getResolvedLocations())) {
                focusLocationInfoList.add( writeAboutnessLocationToHash(loc) );
            }
            focusResults.put("states", focusLocationInfoList);
            logger.debug("Adding City Focus:");
            focusLocationInfoList = new ArrayList<HashMap>();
            for(FocusLocation loc:focusStrategy.selectCities(entities.getResolvedLocations())) {
                focusLocationInfoList.add( writeAboutnessLocationToHash(loc) );
            }
            focusResults.put("cities", focusLocationInfoList);
        }
        placeResults.put("focus",focusResults);
        results.put("places",placeResults);

        logger.debug("Adding People:");
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

        logger.debug("Adding Organizations:");
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

        response.put("results",results);
        return response;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static HashMap writeAboutnessLocationToHash(FocusLocation location){
        HashMap loc = new HashMap();
        GeoName place = location.getGeoName();
        loc.put("score", location.getScore());
        loc.put("id",place.getGeonameID());
        loc.put("name",place.getName());
        String primaryCountryCodeAlpha2 = ""; 
        if(place.getPrimaryCountryCode()!=CountryCode.NULL){
            primaryCountryCodeAlpha2 = place.getPrimaryCountryCode().toString();
        }
        String admin1Code = "";
        
        if(place.getAdmin1Code() !=null){
            admin1Code = place.getAdmin1Code();
        }
        String featureCode = place.getFeatureCode().toString();
        loc.put("featureClass", place.getFeatureClass().toString());
        loc.put("featureCode", featureCode);
        loc.put("population", place.getPopulation());
        loc.put("stateCode", admin1Code);
        loc.put("countryCode",primaryCountryCodeAlpha2);
        loc.put("lat",place.getLatitude());
        loc.put("lon",place.getLongitude());
        
        return loc;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static HashMap writeResolvedLocationToHash(ResolvedLocation resolvedLocation){
    	HashMap loc = new HashMap();
    	int charIndex = resolvedLocation.getLocation().getPosition();
    	GeoName place = resolvedLocation.getGeoname();
        loc.put("confidence", resolvedLocation.getConfidence()); // low is good
        loc.put("id",place.getGeonameID());
        loc.put("name",place.getName());
        String primaryCountryCodeAlpha2 = ""; 
        if(place.getPrimaryCountryCode()!=CountryCode.NULL){
            primaryCountryCodeAlpha2 = place.getPrimaryCountryCode().toString();
        }
        String admin1Code = "";
        
        if(place.getAdmin1Code() !=null){
            admin1Code = place.getAdmin1Code();
        }
        String featureCode = place.getFeatureCode().toString();
        loc.put("featureClass", place.getFeatureClass().toString());
        loc.put("featureCode", featureCode);
        loc.put("population", place.getPopulation());
        loc.put("stateCode", admin1Code);
        loc.put("countryCode",primaryCountryCodeAlpha2);
        loc.put("lat",place.getLatitude());
        loc.put("lon",place.getLongitude());
        HashMap sourceInfo = new HashMap();
        sourceInfo.put("string",resolvedLocation.getLocation().getText());
        sourceInfo.put("charIndex",charIndex);  
        if(resolvedLocation.getLocation() instanceof SentenceLocationOccurrence){
            sourceInfo.put("storySentencesId", ((SentenceLocationOccurrence) resolvedLocation.getLocation()).storySentenceId);
        }
        loc.put("source",sourceInfo);
        
    	return loc;
    }
    
    public static ExtractedEntities extractAndResolve(String text) throws Exception{
        return extractAndResolve(text, false);
    }
    
    public static ExtractedEntities extractAndResolve(String text,boolean manuallyReplaceDemonyms) throws Exception{
        return getParserInstance().extractAndResolve(text,manuallyReplaceDemonyms);
    }

    /**
     * We want all error messages sent to the client to have the same format 
     * @param msg
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })  // I'm generating JSON... don't whine!
    public static HashMap getErrorText(String msg){
        HashMap info = new HashMap();
        info.put("version", PARSER_VERSION);
        info.put("status",STATUS_ERROR);
        info.put("details",msg);
        return info;
    }
    
    public static void logStats(){
        if(resolver!=null){
            ((CliffLocationResolver) resolver).logStats();
        }
    }
    
    /**
     * Lazy instantiation of singleton parser
     * @return
     * @throws Exception
     */
    private static EntityParser getParserInstance() throws Exception{

        if(parser==null){

            // use the Stanford NER location extractor
            String modelToUse = CliffConfig.getInstance().getNerModelName();
            logger.debug("Creating extractor with "+modelToUse);
            StanfordNamedEntityExtractor locationExtractor = new StanfordNamedEntityExtractor(Model.valueOf(modelToUse));                
            
            boolean useFuzzyMatching = false;
            Gazetteer gazetteer = new LuceneGazetteer(new File(PATH_TO_GEONAMES_INDEX));
            resolver = new CliffLocationResolver(gazetteer);

            parser = new EntityParser(locationExtractor, resolver, 
                    useFuzzyMatching, CliffLocationResolver.MAX_HIT_DEPTH);
                        
            logger.info("Created parser successfully");
        }
        
        return parser;
    }

    public static CliffLocationResolver getResolver() throws Exception {
        ParseManager.getParserInstance();
        return resolver;
    }

    public static FocusStrategy getFocusStrategy() throws Exception {
        ParseManager.getParserInstance();
        return focusStrategy;
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
