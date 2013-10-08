package edu.mit.civic.clavin.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.extractor.ApacheExtractor;
import com.bericotech.clavin.extractor.LocationExtractor;
import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.nerd.StanfordExtractor;
import com.bericotech.clavin.resolver.LocationResolver;
import com.bericotech.clavin.resolver.ResolvedLocation;
import com.google.gson.Gson;

import edu.mit.civic.clavin.aboutness.AboutnessStrategy;
import edu.mit.civic.clavin.aboutness.FrequencyOfMentionAboutnessStrategy;
import edu.mit.civic.clavin.resolver.lucene.CustomLuceneLocationResolver;

/**
 * Singleton-style wrapper around a GeoParser.  Call GeoParser.locate(someText) to use this class.
 */
public class ParseManager {

    private static final Boolean BE_NERDY = true;   // controls using the Stanford NER or not
    
    private static final Logger logger = LoggerFactory.getLogger(ParseManager.class);

    public static GeoParser parser = null;

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
    public static String locate(String text) {
        if(text.trim().length()==0){
            return getErrorText("No text");
        }
        try {
            HashMap results = new HashMap();
            results.put("status",STATUS_OK);
            ArrayList places = new ArrayList();
            List<ResolvedLocation> resolvedLocations = locateRaw(text);
            for (ResolvedLocation resolvedLocation: resolvedLocations){
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
            results.put("primaryCountries", aboutness.select(resolvedLocations));
            //results.put("primaryCountries", PercentageOfMentionsAboutnessStrategy.select(resolvedLocations));
            return gson.toJson(results);
        } catch (Exception e) {
            return getErrorText(e.toString());
        }
    }
    
    public static List<ResolvedLocation> locateRaw(String text) throws Exception{
        return getParserInstance().parse(text);        
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
        ((CustomLuceneLocationResolver) resolver).logStats();
    }
    
    /**
     * Lazy instantiation of singleton GeoParser
     * @return
     * @throws Exception
     */
    private static GeoParser getParserInstance() throws Exception{

        if(ParseManager.parser==null){

            // use the Stanford NER location extractor?
            LocationExtractor locationExtractor = null;
            if(BE_NERDY) {
                logger.info("Being NERdy!");
                locationExtractor = new StanfordExtractor();                
            } else {
                locationExtractor = new ApacheExtractor();
            }
            
            int numberOfResultsToFetch = 10;
            boolean useFuzzyMatching = false;

            resolver = new CustomLuceneLocationResolver(new File(PATH_TO_GEONAMES_INDEX), 
                    numberOfResultsToFetch);

            parser = new GeoParser(locationExtractor, resolver, useFuzzyMatching);
            
            logger.info("Created GeoParser successfully");
        }
        
        return parser;
    }

}
