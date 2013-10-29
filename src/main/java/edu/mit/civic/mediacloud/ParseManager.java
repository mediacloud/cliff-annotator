package edu.mit.civic.mediacloud;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.resolver.LocationResolver;
import com.bericotech.clavin.resolver.ResolvedLocation;
import com.google.gson.Gson;

import edu.mit.civic.mediacloud.extractor.ExtractedEntities;
import edu.mit.civic.mediacloud.extractor.PersonOccurrence;
import edu.mit.civic.mediacloud.extractor.StanfordThreeClassExtractor;
import edu.mit.civic.mediacloud.where.CustomLuceneLocationResolver;
import edu.mit.civic.mediacloud.where.aboutness.AboutnessStrategy;
import edu.mit.civic.mediacloud.where.aboutness.FrequencyOfMentionAboutnessStrategy;

/**
 * Singleton-style wrapper around a GeoParser.  Call GeoParser.locate(someText) to use this class.
 */
public class ParseManager {

    private static final String PARSER_VERSION = "0.2"; // increment each time we change an algorithm so we know when parsed results already saved in a DB are stale!
    
    private static final Logger logger = LoggerFactory.getLogger(ParseManager.class);

    public static EntityParser parser = null;
    
    public static StanfordThreeClassExtractor peopleExtractor = null;

    private static Gson gson = new Gson();
    
    private static LocationResolver resolver;   // HACK: pointer to keep around for stats logging
    
    private static AboutnessStrategy aboutness = new FrequencyOfMentionAboutnessStrategy();
    
    private static final String PATH_TO_GEONAMES_INDEX = "./IndexDirectory";
    
    // these two are the statuses used in the JSON responses
    private static final String STATUS_OK = "ok";
    private static final String STATUS_ERROR = "error";
    
    /**
     * Public api method - call this statically to extract locations from a text string 
     * @param text  unstructured text that you want to parse for location mentions
     * @return      json string with details about locations mentioned
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })  // I'm generating JSON... don't whine!
    public static String parse(String text) {
        if(text.trim().length()==0){
            return getErrorText("No text");
        }
        try {
            HashMap results = new HashMap();
            results.put("status",STATUS_OK);
            results.put("version", PARSER_VERSION);
            ArrayList places = new ArrayList();
            ExtractedEntities entities = extractAndResolve(text);

            for (ResolvedLocation resolvedLocation: entities.getResolvedLocations()){
                HashMap loc = new HashMap();
                GeoName place = resolvedLocation.geoname;
                loc.put("confidence", resolvedLocation.confidence); // low is good
                loc.put("id",place.geonameID);
                loc.put("name",place.name);
                String primaryCountryCodeAlpha2 = ""; 
                if(place.primaryCountryCode!=CountryCode.NULL){
                    primaryCountryCodeAlpha2 = place.primaryCountryCode.toString();
                }
                loc.put("countryCode",primaryCountryCodeAlpha2);
                loc.put("lat",place.latitude);
                loc.put("lon",place.longitude);
                HashMap sourceInfo = new HashMap();
                sourceInfo.put("string",resolvedLocation.location.text);
                sourceInfo.put("charIndex",resolvedLocation.location.position);
                loc.put("source",sourceInfo);
                places.add(loc);
            }
            results.put("places",places);
            results.put("primaryCountries", aboutness.select(entities.getResolvedLocations()));


            List<PersonOccurrence> resolvedPeople = entities.getPeople();
            List<HashMap> names = new ArrayList<HashMap>();
            for (PersonOccurrence person: resolvedPeople){
                HashMap sourceInfo = new HashMap();
                sourceInfo.put("name", person.text);
                sourceInfo.put("charIndex", person.position);
                names.add(sourceInfo);
            }
            results.put("people",names);

            // return it as JSON
            return gson.toJson(results);
        } catch (Exception e) {
            return getErrorText(e.toString());
        }
    }
    
    public static ExtractedEntities extractAndResolve(String text){
        try {
            return getParserInstance().parse(text);
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

    private static StanfordThreeClassExtractor getPeopleExtractorInstance() {
        if(peopleExtractor==null){
            try {
                peopleExtractor = new StanfordThreeClassExtractor();
                logger.info("Created People Extractor successfully");
            } catch (Exception e) {
                logger.error("Unable to create Stanford People Extractor! "+e.toString());
            }
        }
        return peopleExtractor;
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
