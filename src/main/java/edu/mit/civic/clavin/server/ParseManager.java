package edu.mit.civic.clavin.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berico.clavin.GeoParser;
import com.berico.clavin.GeoParserFactory;
import com.berico.clavin.extractor.LocationExtractor;
import com.berico.clavin.gazetteer.Place;
import com.berico.clavin.nerd.ExternalSequenceClassifierProvider;
import com.berico.clavin.nerd.NerdLocationExtractor;
import com.berico.clavin.nerd.SequenceClassifierProvider;
import com.berico.clavin.resolver.ResolvedLocation;
import com.google.gson.Gson;

import edu.mit.civic.clavin.resolver.ResolvedLocationAggregator;
import edu.mit.civic.clavin.resolver.ResolvedLocationGroup;

/**
 * Singleton-style wrapper around a GeoParser.  Call GeoParser.locate(someText) to use this class.
 */
public class ParseManager {

    private static final Boolean BE_NERDY = true;   // controls using the Stanford NER or not

    private static final Logger logger = LoggerFactory.getLogger(ParseManager.class);

    private static GeoParser parser = null;

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
            List<ResolvedLocation> resolvedLocations = getParserInstance().parse(text).getLocations();
            // group the results by place
            ResolvedLocationAggregator aggregateResolvedLocations = new ResolvedLocationAggregator();  
            for (ResolvedLocation resolvedLocation : resolvedLocations){
                aggregateResolvedLocations.add(resolvedLocation);
            }
            // assemble some JSON back
            ArrayList locationList = new ArrayList();
            for (ResolvedLocationGroup resolvedLocationGroup : aggregateResolvedLocations.getAllResolvedLocationGroups()){
                HashMap loc = new HashMap();
                Place place = resolvedLocationGroup.getPlace();
                loc.put("confidence", resolvedLocationGroup.getAverageConfidence()); // low is good
                loc.put("occurrences", resolvedLocationGroup.getOccurrenceCount());
                loc.put("id",place.getId());
                loc.put("name",place.getName());
                loc.put("countryCode",place.getPrimaryCountryCode().toString());
                loc.put("lat",place.getCenter().getLatitude());
                loc.put("lon",place.getCenter().getLongitude());
/*                ArrayList<String> alternateNames = new ArrayList<String>();
                for(String name: place.getAlternateNames()){
                    alternateNames.add(name);
                }
                loc.put("alternateNames",alternateNames);*/
                locationList.add(loc);
            }
            results.put("results",locationList);
            return gson.toJson(results);
        } catch (Exception e) {
            return getErrorText(e.toString());
        }
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
     * Lazy instantiation of GeoParser
     * @return
     * @throws Exception
     */
    private static GeoParser getParserInstance() throws Exception{

        if(ParseManager.parser==null){

            GeoParser defaultParser = GeoParserFactory.getDefault(PATH_TO_GEONAMES_INDEX);

            if(BE_NERDY) {
                SequenceClassifierProvider sequenceClassifierProvider = 
                    new ExternalSequenceClassifierProvider(PATH_TO_NER_ZIP);

                LocationExtractor nerdyLocationExtractor = 
                    new NerdLocationExtractor(sequenceClassifierProvider);
    
                parser = new GeoParser(nerdyLocationExtractor, 
                    defaultParser.getCoordinateExtractor(), 
                    defaultParser.getLocationResolver());

            } else {
                parser = defaultParser;
            }

            logger.info("Created GeoParser successfully");
        }
        
        return parser;
    }

}
