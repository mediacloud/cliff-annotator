package edu.mit.civic.mediacloud.where;

import static org.apache.lucene.queryparser.classic.QueryParserBase.escape;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.analyzing.AnalyzingQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.extractor.LocationExtractor;
import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.index.BinarySimilarity;
import com.bericotech.clavin.index.WhitespaceLowerCaseAnalyzer;
import com.bericotech.clavin.resolver.LocationResolver;
import com.bericotech.clavin.resolver.ResolvedLocation;

import edu.mit.civic.mediacloud.where.disambiguation.DisambiguationStrategy;
import edu.mit.civic.mediacloud.where.disambiguation.HeuristicDisambiguationStrategy;

/*#####################################################################
 * 
 * CLAVIN (Cartographic Location And Vicinity INdexer)
 * ---------------------------------------------------
 * 
 * Copyright (C) 2012-2013 Berico Technologies
 * http://clavin.bericotechnologies.com
 * 
 * ====================================================================
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * ====================================================================
 * 
 * CustomLuceneLocationResolver.java
 * 
 *###################################################################*/

/**
 * Resolves location names into GeoName objects.
 * 
 * Takes location names extracted from unstructured text documents by
 * {@link LocationExtractor} and resolves them into the appropriate
 * geographic entities (as intended by the document's author based on
 * context) by finding the best match in a gazetteer.
 * 
 */
public class CustomLuceneLocationResolver implements LocationResolver {
	
	public final static Logger logger = LoggerFactory.getLogger(CustomLuceneLocationResolver.class);
	
	// Lucene index built from GeoNames gazetteer
	private FSDirectory index;
	private IndexSearcher indexSearcher;
	private static Analyzer indexAnalyzer;
	
	// maximum number of matches to be fetched from Lucene index
	// (i.e., search depth) -- use a value of 1 to simply retrieve the
	// matching geo entity having the highest population
	private int maxHitDepth;
	
	// custom Lucene sorting based on Lucene match score and the
	// population of the GeoNames gazetteer entry represented by the
	// matched index document
	private static final Sort populationSort = new Sort(new SortField[]
			{SortField.FIELD_SCORE, new SortField("population", SortField.Type.LONG, true)});
	
	// my custom wrapper to let us try out multiple different disambiguation strategies
	private DisambiguationStrategy disambiguationStrategy;
	
	/**
	 * Builds a {@link CustomLuceneLocationResolver} by loading a pre-built Lucene
	 * index from disk and setting configuration parameters for
	 * resolving location names to GeoName objects.
	 * 
	 * @param indexDir				Lucene index directory to be loaded
	 * @param maxHitDepth			number of candidate matches to consider
	 * @param maxContextWindow		how much context to consider when resolving
	 * @throws IOException
	 * @throws ParseException
	 */
	public CustomLuceneLocationResolver(File indexDir, int maxHitDepth) throws IOException, ParseException {
		
		// load the Lucene index directory from disk
		index = FSDirectory.open(indexDir);
		
		// index employs simple lower-casing & tokenizing on whitespace
		indexAnalyzer = new WhitespaceLowerCaseAnalyzer();
		indexSearcher = new IndexSearcher(DirectoryReader.open(index));
		
		// override default TF/IDF score to ignore multiple appearances
		indexSearcher.setSimilarity(new BinarySimilarity());
		
		this.maxHitDepth = maxHitDepth;
		
		// run an initial throw-away query just to "prime the pump" for
		// the cache, so we can accurately measure performance speed
		// per: http://wiki.apache.org/lucene-java/ImproveSearchingSpeed
		indexSearcher.search(new AnalyzingQueryParser(Version.LUCENE_40,
				"indexName", indexAnalyzer).parse("Reston"), null, maxHitDepth, populationSort);
		
		//set up the disambiguator
		disambiguationStrategy = new HeuristicDisambiguationStrategy();
	}
		
	/**
	 * Finds all matches (capped at {@link CustomLuceneLocationResolver#maxHitDepth})
	 * in the Lucene index for a given location name.
	 * 
	 * @param locationName		name of the geographic location to be resolved
	 * @param fuzzy				switch for turning on/off fuzzy matching
	 * @return					list of ResolvedLocation objects as potential matches
	 * @throws IOException
	 * @throws ParseException
	 */
	private List<ResolvedLocation> getCandidateMatches(LocationOccurrence locationName, boolean fuzzy)
			throws IOException, ParseException{
		
		// santize the query input
		String sanitizedLocationName = escape(locationName.text.toLowerCase());
		
		try{
		    
	  		// Lucene query used to look for matches based on the
			// "indexName" field
	  		Query q = new AnalyzingQueryParser(Version.LUCENE_40,
	  				"indexName", indexAnalyzer).parse("\"" + sanitizedLocationName + "\"");
	  		
	  		// collect all the hits up to maxHits, and sort them based
	  		// on Lucene match score and population for the associated
	  		// GeoNames record
	  		TopDocs results = indexSearcher.search(q, null, maxHitDepth, populationSort);
	  	    
	  		// initialize the return object
	  	    List<ResolvedLocation> candidateMatches = new ArrayList<ResolvedLocation>();
	  	    
	  	    // see if anything was found
	  	    if (results.scoreDocs.length > 0) {
	  	    	// one or more exact String matches found for this location name
	  	    	for (int i = 0; i < results.scoreDocs.length; i++) {
	  	    		// add each matching location to the list of candidates
	  	    		ResolvedLocation location = new ResolvedLocation(indexSearcher.doc(results.scoreDocs[i].doc), locationName, false);
			  	    logger.debug("{}", location);
			  	    candidateMatches.add(location);
	  	    	}
	  	    } else if (fuzzy) { // only if fuzzy matching is turned on
	  	    	// no exact String matches found -- fallback to fuzzy search
	  	    	
	  	    	// Using the tilde "~" makes this a fuzzy search. I compared this to FuzzyQuery
	  	  		// with TopTermsBoostOnlyBooleanQueryRewrite, I like the output better this way.
	  	  		// With the other method, we failed to match things like "Stra��enhaus Airport"
	  	  		// as <Stra��enhaus>, and the match scores didn't make as much sense.
	  	    	q = new AnalyzingQueryParser(Version.LUCENE_40, "indexName", indexAnalyzer).parse(sanitizedLocationName + "~");
	  	    	
	  	  		// collect all the fuzzy matches up to maxHits, and sort
	  	  		// them based on Lucene match score and population for the
	  	  		// associated GeoNames record
	  	    	results = indexSearcher.search(q, null, maxHitDepth, populationSort);
	  	    	
	  	    	// see if anything was found with fuzzy matching
	  	    	if (results.scoreDocs.length > 0) {
	  	    		// one or more fuzzy matches found for this location name
	  	    		for (int i = 0; i < results.scoreDocs.length; i++) {
	  	    			// add each matching location to the list of candidates
	  	    			ResolvedLocation location = new ResolvedLocation(indexSearcher.doc(results.scoreDocs[i].doc), locationName, true);
		  		  	    logger.debug(location + "{fuzzy}");
		  		  	    candidateMatches.add(location);
	  	    		}
	  	    	} else {
	  	    		// drats, foiled again! no fuzzy matches found either!
	  	    		// in this case, we'll return an empty list of
	  	    		// candidate matches
		  	    	logger.debug("No match found for: '{}'", locationName);
	  	    	}
	  	    } else {
  	    		// no matches found and fuzzy matching is turned off
	  	    	logger.debug("No match found for: '{}'", locationName);
  	    	}
	  	    
	  	    return candidateMatches;
	  	    
		} catch (ParseException e) {
			logger.error(String.format("Error resolving location for : '%s'", locationName), e);
			throw e;
		} catch (IOException e) {
			logger.error(String.format("Error resolving location for : '%s'", locationName), e);
			throw e;
		}
  	}
  	
  	/**
  	 * Uses heuristics to select the best match for each location name
  	 * extracted from a document, choosing from among a list of lists
  	 * of candidate matches.
  	 * 
  	 * Although not guaranteeing an optimal solution (enumerating &
  	 * evaluating each possible combination is too costly), it does a
  	 * decent job of cracking the "Springfield Problem" by selecting
  	 * candidates that would make sense to appear together based on
  	 * common country and admin1 codes (i.e., states or provinces).
  	 * 
  	 * For example, if we also see "Boston" mentioned in a document 
  	 * that contains "Springfield," we'd use this as a clue that we
  	 * ought to choose Springfield, MA over Springfield, IL or
  	 * Springfield, MO.
  	 * 
  	 * TODO: consider lat/lon distance in addition to shared
  	 * 		 CountryCodes and Admin1Codes.
  	 * 
  	 * @param allCandidates	list of lists of candidate matches for locations names
  	 * @return				list of best matches for each location name
  	 */
  	private List<ResolvedLocation> pickBestCandidates(List<List<ResolvedLocation>> allCandidates) {
  		
  		// initialize return object
  		List<ResolvedLocation> bestCandidates = disambiguationStrategy.select(this, allCandidates);
  		
  		return bestCandidates;
  	}
  	
    /**
     * Resolves the supplied list of location names into
     * {@link ResolvedLocation}s containing {@link GeoName} objects.
     * 
     * Calls {@link CustomLuceneLocationResolver#getCandidateMatches(LocationOccurrence, boolean)} on
     * each location name to find all possible matches, then uses
     * heuristics to select the best match for each by calling
     * {@link LocationResolver#pickBestCandidates(List<List<ResolvedLocation>>)}.
     * 
     * @param locations 		list of location names to be resolved
     * @param fuzzy				switch for turning on/off fuzzy matching
     * @return 					list of {@link ResolvedLocation} objects
     * @throws ParseException 
     * @throws IOException 
     **/
    public List<ResolvedLocation> resolveLocations(List<LocationOccurrence> locations, boolean fuzzy) throws IOException, ParseException {
    	
    	// forgetting something?
    	if (locations == null)
    		return new ArrayList<ResolvedLocation>();
    	
		if (maxHitDepth > 1) { // perform context-based heuristic matching
			
			// stores all possible matches for each location name
			List<List<ResolvedLocation>> allCandidates = new ArrayList<List<ResolvedLocation>>();
			
			// loop through all the location names
			for (LocationOccurrence location : locations) {
				// get all possible matches
				List<ResolvedLocation> candidates = getCandidateMatches(location, fuzzy);
				
				// if we found some possible matches, save them
				if (candidates.size() > 0)
					allCandidates.add(candidates);
			}
			
			// initialize return object
			List<ResolvedLocation> bestCandidates = new ArrayList<ResolvedLocation>();
			
			// select the best match for each location name based
			// based on heuristics
			bestCandidates.addAll(pickBestCandidates(allCandidates));
			
			return bestCandidates;
			
		} else { // use no heuristics, simply choose matching location with greatest population
			
			// initialize return object
			List<ResolvedLocation> resolvedLocations = new ArrayList<ResolvedLocation>();
			
			// stores possible matches for each location name
			List<ResolvedLocation> candidateLocations;
			
			// loop through all the location names
			for (LocationOccurrence location : locations) {
				// choose the top-sorted candidate for each individual
				// location name
				candidateLocations = getCandidateMatches(location, fuzzy);
				
				// if a match was found, add it to the return list
				if (candidateLocations.size() > 0)
					resolvedLocations.add(candidateLocations.get(0));
			}
			
			return resolvedLocations;
		}
	}

    public void logStats(){
        disambiguationStrategy.logStats();
    }
    
}