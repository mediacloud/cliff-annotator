package edu.mit.civic.mediacloud;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.resolver.LocationResolver;
import com.bericotech.clavin.resolver.ResolvedLocation;

import edu.mit.civic.mediacloud.extractor.ExtractedEntities;
import edu.mit.civic.mediacloud.extractor.StanfordThreeClassExtractor;

/**
 * Patterned after com.bericotech.clavin.GeoParser
 * @author rahulb
 *
 */
public class EntityParser {

    private static final Logger logger = LoggerFactory.getLogger(EntityParser.class);
    
    // entity extractor to find location names in text
    private StanfordThreeClassExtractor extractor;
    
    // resolver to match location names against gazetteer records
    private LocationResolver resolver;
    
    // switch controlling use of fuzzy matching
    private final boolean fuzzy;

    public EntityParser(StanfordThreeClassExtractor extractor, LocationResolver resolver,
            boolean fuzzy) {
        this.extractor = extractor;
        this.resolver = resolver;
        this.fuzzy = fuzzy;
    }

    public ExtractedEntities parse(String inputText) throws Exception {
        
        logger.trace("input: {}", inputText);
        
        // first, extract location names from the text
        ExtractedEntities entities = extractor.extractEntities(inputText);
        
        logger.trace("extracted: {}", entities.getLocations());
        
        // then, resolve the extracted location names against a
        // gazetteer to produce geographic entities representing the
        // locations mentioned in the original text
        List<ResolvedLocation> resolvedLocations = resolver.resolveLocations(entities.getLocations(), fuzzy);
        entities.setResolvedLocations( resolvedLocations );
        
        logger.trace("resolved: {}", resolvedLocations);
                
        return entities;
    }
    
}
