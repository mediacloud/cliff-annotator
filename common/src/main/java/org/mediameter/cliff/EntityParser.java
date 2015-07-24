package org.mediameter.cliff;

import java.util.List;
import java.util.Map;

import org.mediameter.cliff.extractor.EntityExtractorService;
import org.mediameter.cliff.extractor.ExtractedEntities;
import org.mediameter.cliff.orgs.OrganizationResolver;
import org.mediameter.cliff.orgs.ResolvedOrganization;
import org.mediameter.cliff.people.PersonResolver;
import org.mediameter.cliff.people.ResolvedPerson;
import org.mediameter.cliff.places.CliffLocationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Patterned after com.bericotech.clavin.GeoParser
 * @author rahulb
 *
 */
public class EntityParser {

    private static final Logger logger = LoggerFactory.getLogger(EntityParser.class);
    
    // entity stanford to find location names in text
    private EntityExtractorService extractor;
    
    private CliffLocationResolver locationResolver;
    private PersonResolver personResolver;
    private OrganizationResolver organizationResolver;
    
    // switch controlling use of fuzzy matching
    private final boolean fuzzy;
    private final int maxHitDepth;
    
    public EntityParser(EntityExtractorService extractor, CliffLocationResolver resolver,
            boolean fuzzy, int maxHitDepth) {
        this.extractor = extractor;
        this.locationResolver = resolver;
        this.personResolver = new PersonResolver();
        this.organizationResolver = new OrganizationResolver();
        this.fuzzy = fuzzy;
        this.maxHitDepth = maxHitDepth;
    }

    public ExtractedEntities extractAndResolve(String inputText, boolean manuallyReplaceDemonyms) throws Exception {
        logger.trace("input: {}", inputText);
        long startTime = System.nanoTime();
        ExtractedEntities extractedEntities = extractor.extractEntities(inputText,manuallyReplaceDemonyms);
        long extract = System.nanoTime() - startTime;
        logger.trace("extracted: {}", extractedEntities.getLocations());
        startTime = System.nanoTime();
        ExtractedEntities entities = resolve(extractedEntities);
        long resolve = System.nanoTime() - startTime;
        logger.debug("extractAndResolve: "+extract+" / "+resolve);
        return entities;
    }

    @SuppressWarnings("rawtypes")
    public ExtractedEntities extractAndResolveFromSentences(Map[] sentences, boolean manuallyReplaceDemonyms) throws Exception {
        logger.trace("input: {}", sentences);
        long startTime = System.nanoTime();
        ExtractedEntities extractedEntities = extractor.extractEntitiesFromSentences(sentences,manuallyReplaceDemonyms);
        long extract = System.nanoTime() - startTime;
        logger.trace("extracted: {}", extractedEntities.getLocations());
        startTime = System.nanoTime();
        ExtractedEntities entities = resolve(extractedEntities);
        long resolve = System.nanoTime() - startTime;
        logger.debug("extractAndResolve: "+extract+" / "+resolve);
        return entities;
    }

    public ExtractedEntities resolve(ExtractedEntities entities) throws Exception{
        
        // resolve the extracted location names against a
        // gazetteer to produce geographic entities representing the
        // locations mentioned in the original text
        List<ResolvedLocation> resolvedLocations = locationResolver.resolveLocations(
                entities.getLocations(), this.maxHitDepth, -1, this.fuzzy);
        entities.setResolvedLocations( resolvedLocations );
        logger.trace("resolvedLocations: {}", resolvedLocations);
        
        // Disambiguate people
        List<ResolvedPerson> resolvedPeople = personResolver.resolve(entities.getPeople());
        entities.setResolvedPeople( resolvedPeople );
        logger.trace("resolvedPeople: {}", resolvedLocations);

        // Disambiguate organizations
        List<ResolvedOrganization> resolvedOrganizations = organizationResolver.resolve(entities.getOrganizations());
        entities.setResolvedOrganizations( resolvedOrganizations );
        logger.trace("resolvedOrganizations: {}", resolvedOrganizations);

        return entities;
        
    }
    
}
