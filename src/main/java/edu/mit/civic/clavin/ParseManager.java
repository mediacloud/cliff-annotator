package edu.mit.civic.clavin;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berico.clavin.GeoParser;
import com.berico.clavin.GeoParserFactory;
import com.berico.clavin.extractor.LocationExtractor;
import com.berico.clavin.nerd.ExternalSequenceClassifierProvider;
import com.berico.clavin.nerd.NerdLocationExtractor;
import com.berico.clavin.nerd.SequenceClassifierProvider;
import com.berico.clavin.resolver.ResolvedLocation;

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
        List<ResolvedLocation> resolvedLocations = getParserInstance().parse(text).getLocations();
        JSONArray locationList = new JSONArray();
        for (ResolvedLocation resolvedLocation : resolvedLocations){
            JSONObject loc = new JSONObject();
            loc.put("confidence", resolvedLocation.getConfidence());
            loc.put("matchedName", resolvedLocation.getMatchedName());
            loc.put("lat",resolvedLocation.getPlace().getCenter().getLatitude());
            loc.put("lon",resolvedLocation.getPlace().getCenter().getLongitude());
            locationList.add(loc);
        }
        return locationList.toString();
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
