package edu.mit.civic.clavin.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berico.clavin.GeoParser;
import com.berico.clavin.extractor.ApacheExtractor;
import com.berico.clavin.extractor.LocationExtractor;
import com.berico.clavin.gazetteer.GeoName;
import com.berico.clavin.nerd.ExternalSequenceClassifierProvider;
import com.berico.clavin.nerd.NerdLocationExtractor;
import com.berico.clavin.nerd.SequenceClassifierProvider;
import com.berico.clavin.resolver.LocationResolver;
import com.berico.clavin.resolver.ResolvedLocation;
import com.google.gson.Gson;

import edu.mit.civic.clavin.resolver.lucene.CustomLuceneLocationResolver;

/**
 * Singleton-style wrapper around a GeoParser.  Call GeoParser.locate(someText) to use this class.
 */
public class ParseManager {

    private static final Boolean BE_NERDY = true;   // controls using the Stanford NER or not
    
    private static final Logger logger = LoggerFactory.getLogger(ParseManager.class);

    public static GeoParser parser = null;

    private static Gson gson = new Gson();
    
    private static final String PATH_TO_GEONAMES_INDEX = "./IndexDirectory";
    private static final String PATH_TO_NER_ZIP = "src/main/resources/all.3class.distsim.crf.ser.gz";
    
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
            ArrayList locationList = new ArrayList();
            List<ResolvedLocation> resolvedLocations = locateRaw(text);
            for (ResolvedLocation resolvedLocation: resolvedLocations){
                HashMap loc = new HashMap();
                GeoName place = resolvedLocation.geoname;
                loc.put("confidence", resolvedLocation.confidence); // low is good
                loc.put("id",place.geonameID);
                loc.put("name",place.name);
                loc.put("countryCode",place.primaryCountryCode.toString());
                //loc.put("lat",place.latitude);
                //loc.put("lon",place.longitude);
                HashMap sourceInfo = new HashMap();
                sourceInfo.put("string",resolvedLocation.location.text);
                sourceInfo.put("charIndex",resolvedLocation.location.position);
                loc.put("source",sourceInfo);
                //loc.put("type",place.featureClass.type);
                locationList.add(loc);
            }            
            results.put("results",locationList);
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
                SequenceClassifierProvider sequenceClassifierProvider = 
                        new ExternalSequenceClassifierProvider(PATH_TO_NER_ZIP);
                locationExtractor = 
                        new NerdLocationExtractor(sequenceClassifierProvider);                
            } else {
                locationExtractor = new ApacheExtractor();
            }
            
            int numberOfResultsToFetch = 10;
            boolean useFuzzyMatching = false;

            LocationResolver resolver = new CustomLuceneLocationResolver(new File(PATH_TO_GEONAMES_INDEX), 
                    numberOfResultsToFetch);

            parser = new GeoParser(locationExtractor, resolver, useFuzzyMatching);
            
            logger.info("Created GeoParser successfully");
        }
        
        return parser;
    }

}
