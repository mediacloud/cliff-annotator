package edu.mit.civic.clavin.resolver.impl.lucene;

import java.util.List;

import org.apache.lucene.queryparser.analyzing.AnalyzingQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berico.clavin.Options;
import com.berico.clavin.extractor.LocationOccurrence;
import com.berico.clavin.resolver.ResolvedLocation;
import com.berico.clavin.resolver.impl.LocationNameIndex;
import com.berico.clavin.resolver.impl.lucene.FieldConstants;
import com.berico.clavin.resolver.impl.lucene.LuceneComponents;
import com.berico.clavin.resolver.impl.lucene.LuceneUtils;

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
 * LuceneLocationNameIndex.java
 * 
 *###################################################################*/

/**
 * Location Name Index backed by Lucene index.
 * 
 * This implementation utilizes two strategies:
 * 1.  Attempt an exact match on the name provided.
 * 2.  (if set and exact match returns without results), perform
 *     a fuzzy match against the index on the name provided.
 * 
 * By default, results are sorted first by population, and then by
 * field score.  This works well for location names that are subsets
 * of a index's normalized name ("New York" in "City of New York"), but
 * will definitely exhibit a population bias for results like Boston, MA
 * and Boston, Philippines.
 */
public class TweakedLuceneLocationNameIndex implements LocationNameIndex {

	private static final Logger logger = 
			LoggerFactory.getLogger(TweakedLuceneLocationNameIndex.class);
	
	/**
	 * Default maximum number of results to return (by Lucene).
	 */
	public static final int DEFAULT_LIMIT = 10;
	public static final String KEY_DEFAULT_LIMIT = "location.index.limit";
	
	/**
	 * Whether fuzzy matching should be used by default.
	 */
	public static final boolean DEFAULT_USE_FUZZY = false;
	public static final String KEY_DEFAULT_USE_FUZZY = "location.index.useFuzzy";
	
	/**
	 * Default sorting mechanism (Population, then Field Score).
	 * It's important to note that this mechanism favors population size of
	 * results.  The assumption is that "Boston" will resolve to "City of Boston"
	 * before "Boston" (exact term match for location in Philippines) or something
	 * more specific like "Boston Heights".
	 */
	public static Sort DEFAULT_SORTER = 
		new Sort(
			new SortField(FieldConstants.POPULATION, SortField.Type.LONG, true),
			SortField.FIELD_SCORE);
	
	LuceneComponents lucene;
	AnalyzingQueryParser queryParser;
	
	/**
	 * Instantiate the Index with the appropriate LuceneComponents.
	 * @param lucene Configured LuceneComponents.
	 */
	public TweakedLuceneLocationNameIndex(LuceneComponents lucene){
		
		this.lucene = lucene;
		this.queryParser = new AnalyzingQueryParser(
			Version.LUCENE_43, FieldConstants.NAME, lucene.getIndexAnalyzer());
	}

	/**
	 * Return a list of Resolved Locations that best match the Location Occurrence
	 * found in a document.
	 * @param occurrence The Location Occurrence.
	 * @param options Options for the index.
	 * @return List of Resolved Locations matching the occurrence.
	 */
	public List<ResolvedLocation> search(
			LocationOccurrence occurrence, Options options) throws Exception {
		
		options = (options == null)? new Options() : options;
		
		// Get the max number of records to return.
		int limit = options.getInt(KEY_DEFAULT_LIMIT, DEFAULT_LIMIT);
		
		// Get whether fuzzy matching is enabled.
		boolean useFuzzy = options.getBoolean(KEY_DEFAULT_USE_FUZZY, DEFAULT_USE_FUZZY);
		
		IndexSearcher searcher = lucene.getSearcherManager().acquire();
		
		boolean usedFuzzy = false;
		
		// We need to sanitize the name so it doesn't have unescaped Lucene syntax that
		// would throw off the search index.
		String escapedName = 
				QueryParserBase.escape(occurrence.getText().toLowerCase());
		
		// Try an exact query
		Query query = getExactQuery(escapedName);
		
		// Gather the results.
		TopDocs results = searcher.search(query, null, limit, DEFAULT_SORTER);
		
		// If there are no results, and a fuzzy query was requested
		if (results.scoreDocs.length == 0 && useFuzzy) {
			
			usedFuzzy = true;
			
			// Attempt a fuzzy query
			query = getFuzzyQuery(escapedName);
			
			// Gather the results
			results = searcher.search(query, null, limit, DEFAULT_SORTER);
		}
		
		if (results.scoreDocs.length == 0)
			logger.info("Found no results for {}.", escapedName);
		
		return LuceneUtils.convertToLocations(occurrence, searcher, results, usedFuzzy);
	}
	
	/**
	 * Construct an exact query for the provided location name.
	 * @param locationName Name to search for.
	 * @return Exact Query
	 * @throws ParseException
	 */
	protected Query getExactQuery(String locationName) throws ParseException {
	
		// We want to attempt to force an 'exact match', but using quotes in the 
		// search string.  We also want to search in lower case to avoid
		// unnormalized names or gramatical errors.
		String searchExpression = String.format("\"%s\"", locationName.toLowerCase());
		
		// Parse the Lucene query
		return queryParser.parse(searchExpression);
	}
	
	/**
	 * Construct a fuzzy query for the provided location name.
	 * @param locationName Name to search for.
	 * @return Fuzzy Query
	 * @throws ParseException
	 */
	protected Query getFuzzyQuery(String locationName) throws ParseException{

		// Adding a tilde at the end of the query will instruct Lucene to perform
		// a fuzzy query.
		String searchExpression = String.format("%s~", locationName.toLowerCase());
		
		// Parse the Lucene query
		return queryParser.parse(searchExpression);
	}
	
	/**
	 * Allow configuration of static DEFAULT_SORTER field via DI framework
	 * like Spring.
	 * @param sorter Lucene Sorter to use.
	 */
	public void setDefaultSorter(Sort sorter){
		DEFAULT_SORTER = sorter;
	}

	/**
	 * Set the max number of results to return from the index.
	 * @param options Options to set on
	 * @param limit Max number of results.
	 */
	public static void configureLimit(Options options, int limit){
		
		options.put(KEY_DEFAULT_LIMIT, Integer.toString(limit));
	}
	
	/**
	 * Set whether fuzzy matching should be used.
	 * @param options Options to set on
	 * @param useFuzzy true if fuzzy matching should be used.
	 */
	public static void configureUseFuzzy(Options options, boolean useFuzzy){
		
		options.put(KEY_DEFAULT_USE_FUZZY, Boolean.toString(useFuzzy));
	}
}
