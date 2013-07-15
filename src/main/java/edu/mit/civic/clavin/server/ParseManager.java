package edu.mit.civic.clavin.server;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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

import edu.mit.civic.clavin.resolver.ResolvedLocationAggregator;
import edu.mit.civic.clavin.resolver.ResolvedLocationGroup;

/**
 * Singleton-style wrapper around a GeoParser
 */
public class ParseManager {

    private static final Boolean BE_NERDY = true;   // controls using the Stanford NER or not

    private static final Logger logger = LoggerFactory.getLogger(ParseManager.class);

    private static GeoParser parser = null;

    @SuppressWarnings("unchecked")
    public static String locate(String text) throws Exception{
        if(text.trim().length()==0){
            return getErrorText("No text");
        }
        JSONObject results = new JSONObject();
        results.put("status","ok");
        List<ResolvedLocation> resolvedLocations = getParserInstance().parse(text).getLocations();
        // group the results by place
        ResolvedLocationAggregator aggregateResolvedLocations = new ResolvedLocationAggregator();  
        for (ResolvedLocation resolvedLocation : resolvedLocations){
            aggregateResolvedLocations.add(resolvedLocation);
        }
        // assemble some JSON back
        JSONArray locationList = new JSONArray();
        for (ResolvedLocationGroup resolvedLocationGroup : aggregateResolvedLocations.getAllResolvedLocationGroups()){
            JSONObject loc = new JSONObject();
            Place place = resolvedLocationGroup.getPlace();
            loc.put("confidence", resolvedLocationGroup.getAverageConfidence()); // low is good
            loc.put("occurrences", resolvedLocationGroup.getOccurrenceCount());
            loc.put("id",place.getId());
            loc.put("name",place.getName());
            loc.put("countryCode",place.getPrimaryCountryCode().toString());
            loc.put("lat",place.getCenter().getLatitude());
            loc.put("lon",place.getCenter().getLongitude());
            JSONArray alternateNames = new JSONArray();
            for(String name: place.getAlternateNames()){
                alternateNames.add(name);
            }
            loc.put("alternateNames",alternateNames);
            locationList.add(loc);
        }
        results.put("results",locationList);
        return results.toString();
    }
    
    @SuppressWarnings("unchecked")
    public static String getErrorText(String msg){
        JSONObject info = new JSONObject();
        info.put("status","error");
        info.put("details",msg);
        return info.toString();
    }
    
    private static GeoParser getParserInstance() throws Exception{

        if(ParseManager.parser==null){

            GeoParser defaultParser = GeoParserFactory.getDefault("./IndexDirectory");

            if(BE_NERDY) {
                SequenceClassifierProvider sequenceClassifierProvider = 
                    new ExternalSequenceClassifierProvider(
                            "src/main/resources/all.3class.distsim.crf.ser.gz");

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
