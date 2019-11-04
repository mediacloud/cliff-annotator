package org.mediacloud.cliff.places;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mediacloud.cliff.places.disambiguation.HeuristicDisambiguationStrategy;
import org.mediacloud.cliff.places.disambiguation.LocationDisambiguationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.gazetteer.query.FuzzyMode;
import com.bericotech.clavin.gazetteer.query.Gazetteer;
import com.bericotech.clavin.gazetteer.query.QueryBuilder;
import com.bericotech.clavin.resolver.ClavinLocationResolver;
import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Override the deafult location resolving to include demonyms and use our disambiguation. 
 * @author rahulb
 */
public class CliffLocationResolver extends ClavinLocationResolver{

    private static final Logger logger = LoggerFactory.getLogger(CliffLocationResolver.class);

    public static final int MAX_HIT_DEPTH = 10;
    
    // my custom wrapper to let us try out multiple different disambiguation strategies
    private LocationDisambiguationStrategy disambiguationStrategy;

    private boolean filterOutDemonyms = false;
    
    public CliffLocationResolver(Gazetteer gazetteer) {
        super(gazetteer);
        disambiguationStrategy = new HeuristicDisambiguationStrategy();
    }

    public GeoName getByGeoNameId(int geoNameId) throws UnknownGeoNameIdException {
        try {
            return getGazetteer().getGeoName(geoNameId);
        } catch (ClavinException ce){
            throw new UnknownGeoNameIdException(geoNameId);
        }
    }

    /**
     * Resolves the supplied list of location names into
     * {@link ResolvedLocation}s containing {@link com.bericotech.clavin.gazetteer.GeoName} objects.
     *
     * Calls {@link Gazetteer#getClosestLocations} on
     * each location name to find all possible matches, then uses
     * heuristics to select the best match for each by calling
     * {@link ClavinLocationResolver#pickBestCandidates}.
     *
     * @param locations          list of location names to be resolved
     * @param maxHitDepth        number of candidate matches to consider
     * @param maxContextWindow   how much context to consider when resolving
     * @param fuzzy              switch for turning on/off fuzzy matching
     * @return                   list of {@link ResolvedLocation} objects
     * @throws ClavinException   if an error occurs parsing the search terms
     **/
    @SuppressWarnings("unchecked")
    public List<ResolvedLocation> resolveLocations(final List<LocationOccurrence> locations, final int maxHitDepth,
            final int maxContextWindow, final boolean fuzzy) throws ClavinException {
        // are you forgetting something? -- short-circuit if no locations were provided
        if (locations == null || locations.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        // RB: turn off demonym filtering because we want those
        List<LocationOccurrence> filteredLocations;
        if(filterOutDemonyms){
            /* Various named entity recognizers tend to mistakenly extract demonyms
             * (i.e., names for residents of localities (e.g., American, British))
             * as place names, which tends to gum up the works, so we make sure to
             * filter them out from the list of {@link LocationOccurrence}s passed
             * to the resolver.
             */
            filteredLocations = new ArrayList<LocationOccurrence>();
            for (LocationOccurrence location : locations)
                if (!isDemonym(location))
                    filteredLocations.add(location);            
        } else {
            filteredLocations = locations;
        }
        // did we filter *everything* out?
        if (filteredLocations.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        QueryBuilder builder = new QueryBuilder()
                .maxResults(maxHitDepth)
                // translate CLAVIN 1.x 'fuzzy' parameter into NO_EXACT or OFF; it isn't
                // necessary, or desirable to support FILL for the CLAVIN resolution algorithm
                .fuzzyMode(fuzzy ? FuzzyMode.NO_EXACT : FuzzyMode.OFF)
                .includeHistorical(true);

        if (maxHitDepth > 1) { // perform context-based heuristic matching
            // stores all possible matches for each location name
            List<List<ResolvedLocation>> allCandidates = new ArrayList<List<ResolvedLocation>>();

            long startTime = System.nanoTime();
            // loop through all the location names
            for (LocationOccurrence location : filteredLocations) {
                // get all possible matches
                List<ResolvedLocation> candidates = getGazetteer().getClosestLocations(builder.location(location).build());

                // if we found some possible matches, save them
                if (candidates.size() > 0) {
                    allCandidates.add(candidates);
                }
            }
            long gazetteerTime = System.nanoTime() - startTime;

            // initialize return object
            List<ResolvedLocation> bestCandidates = new ArrayList<ResolvedLocation>();

            //RB: use out heuristic disambiguation instead of the CLAVIN default
            startTime = System.nanoTime();
            bestCandidates = disambiguationStrategy.select(this, allCandidates);
            long disambiguationTime = System.nanoTime() - startTime;
            /*
            // split-up allCandidates into reasonably-sized chunks to
            // limit computational load when heuristically selecting
            // the best matches
            for (List<List<ResolvedLocation>> theseCandidates : ListUtils.chunkifyList(allCandidates, maxContextWindow)) {
                // select the best match for each location name based
                // based on heuristics
                bestCandidates.addAll(pickBestCandidates(theseCandidates));
            }
            */
            logger.debug("gazetterAndDisambiguation: "+gazetteerTime+" / "+disambiguationTime);

            return bestCandidates;
        } else { // use no heuristics, simply choose matching location with greatest population
            // initialize return object
            List<ResolvedLocation> resolvedLocations = new ArrayList<ResolvedLocation>();

            // stores possible matches for each location name
            List<ResolvedLocation> candidateLocations;

            // loop through all the location names
            for (LocationOccurrence location : filteredLocations) {
                // choose the top-sorted candidate for each individual
                // location name
                candidateLocations = getGazetteer().getClosestLocations(builder.location(location).build());

                // if a match was found, add it to the return list
                if (candidateLocations.size() > 0) {
                    resolvedLocations.add(candidateLocations.get(0));
                }
            }

            return resolvedLocations;
        }
    }
    
    public void logStats(){
        disambiguationStrategy.logStats();
    }
    
}