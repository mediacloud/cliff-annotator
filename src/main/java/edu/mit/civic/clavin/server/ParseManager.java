package edu.mit.civic.clavin.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berico.clavin.GeoParserFactory;
import com.berico.clavin.extractor.LocationExtractor;
import com.berico.clavin.extractor.coords.RegexCoordinateExtractor;
import com.berico.clavin.extractor.opennlp.ApacheExtractor;
import com.berico.clavin.gazetteer.Place;
import com.berico.clavin.nerd.ExternalSequenceClassifierProvider;
import com.berico.clavin.nerd.NerdLocationExtractor;
import com.berico.clavin.nerd.SequenceClassifierProvider;
import com.berico.clavin.resolver.LocationResolver;
import com.berico.clavin.resolver.ResolvedLocation;
import com.berico.clavin.resolver.impl.CoordinateCandidateSelectionStrategy;
import com.berico.clavin.resolver.impl.CoordinateIndex;
import com.berico.clavin.resolver.impl.DefaultLocationResolver;
import com.berico.clavin.resolver.impl.LocationCandidateSelectionStrategy;
import com.berico.clavin.resolver.impl.LocationNameIndex;
import com.berico.clavin.resolver.impl.ResolutionResultsReductionStrategy;
import com.berico.clavin.resolver.impl.lucene.LuceneComponents;
import com.berico.clavin.resolver.impl.lucene.LuceneComponentsFactory;
import com.berico.clavin.resolver.impl.lucene.LuceneCoordinateIndex;
import com.berico.clavin.resolver.impl.lucene.LuceneLocationNameIndex;
import com.berico.clavin.resolver.impl.strategies.IdentityReductionStrategy;
import com.berico.clavin.resolver.impl.strategies.WeightedCoordinateScoringStrategy;
import com.berico.clavin.resolver.impl.strategies.locations.ContextualOptimizationStrategy;
import com.google.gson.Gson;

import edu.mit.civic.clavin.CustomGeoParser;
import edu.mit.civic.clavin.resolver.impl.strategies.locatons.NewsHeuristicsStrategy;

/**
 * Singleton-style wrapper around a GeoParser.  Call GeoParser.locate(someText) to use this class.
 */
public class ParseManager {

    private static final Boolean BE_NERDY = false;   // controls using the Stanford NER or not
    private static final Boolean USE_CUSTOM_LOCATION_STRATEGY = true; 
    
    private static final Logger logger = LoggerFactory.getLogger(ParseManager.class);

    public static CustomGeoParser parser = null;

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
                Place place = resolvedLocation.getPlace();
                loc.put("confidence", resolvedLocation.getConfidence()); // low is good
                loc.put("id",place.getId());
                loc.put("name",place.getName());
                loc.put("countryCode",place.getPrimaryCountryCode().toString());
                loc.put("lat",place.getCenter().getLatitude());
                loc.put("lon",place.getCenter().getLongitude());
                HashMap sourceInfo = new HashMap();
                sourceInfo.put("string",resolvedLocation.getLocation().getText());
                sourceInfo.put("charIndex",resolvedLocation.getLocation().getPosition());
                loc.put("source",sourceInfo);
                loc.put("type",place.getFeatureClass().type);
                locationList.add(loc);
            }            
            results.put("results",locationList);
            return gson.toJson(results);
        } catch (Exception e) {
            return getErrorText(e.toString());
        }
    }
    
    public static List<ResolvedLocation> locateRaw(String text) throws Exception{
        return getParserInstance().parse(text).getLocations();        
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
    private static CustomGeoParser getParserInstance() throws Exception{

        if(ParseManager.parser==null){

            LocationExtractor locationExtractor = null;
            
            // use the Stanford NER location extractor?
            if(BE_NERDY) {
                SequenceClassifierProvider sequenceClassifierProvider = 
                        new ExternalSequenceClassifierProvider(PATH_TO_NER_ZIP);
                locationExtractor = 
                        new NerdLocationExtractor(sequenceClassifierProvider);                
            } else {
                locationExtractor = new ApacheExtractor();
            }
            
            // custom disambiguation strategy
            LocationCandidateSelectionStrategy locationSelectionStrategy = null;
            if(USE_CUSTOM_LOCATION_STRATEGY){
                locationSelectionStrategy = new NewsHeuristicsStrategy();
            } else {
                locationSelectionStrategy = new ContextualOptimizationStrategy();
            }
            
            // default things (@see GeoParserFactory)
            RegexCoordinateExtractor coordinateExtractor = 
                    new RegexCoordinateExtractor(GeoParserFactory.DefaultCoordinateParsingStrategies);
            LuceneComponentsFactory factory = new LuceneComponentsFactory(PATH_TO_GEONAMES_INDEX);
            factory.initializeSearcher();
            LuceneComponents lucene = factory.getComponents();
            LocationNameIndex locationNameIndex = new LuceneLocationNameIndex(lucene);
            CoordinateIndex coordinateIndex = new LuceneCoordinateIndex(lucene);
            CoordinateCandidateSelectionStrategy coordinateSelectionStrategy = 
                new WeightedCoordinateScoringStrategy(GeoParserFactory.DefaultCoordinateWeighers);
            ResolutionResultsReductionStrategy reductionStrategy = 
                    new IdentityReductionStrategy();
            
            // Instantiate the LocationResolver with my custom disambiguation
            LocationResolver myLocationResolver = new DefaultLocationResolver(
                    locationNameIndex, 
                    coordinateIndex, 
                    locationSelectionStrategy, 
                    coordinateSelectionStrategy, 
                    reductionStrategy);

            parser = new CustomGeoParser(locationExtractor, coordinateExtractor, myLocationResolver);
            logger.info("Created GeoParser successfully");
        }
        
        return parser;
    }

}
