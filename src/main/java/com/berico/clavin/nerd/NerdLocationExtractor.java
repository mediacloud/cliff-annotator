package com.berico.clavin.nerd;

import java.util.ArrayList;
import java.util.List;

import com.berico.clavin.extractor.LocationExtractor;
import com.berico.clavin.extractor.LocationOccurrence;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Triple;

/**
 * Stanford Named Entity Recognizer implementation of the CLAVIN
 * Location Extractor.
 * 
 * @author Richard Clayton (Berico Technologies)
 */
public class NerdLocationExtractor implements LocationExtractor {

	/**
	 * This is the label/ontology used by Stanford to represent location
	 * entities.
	 */
	private static final String NER_LOCATION_ONTOLOGY = "LOCATION";
	
	private AbstractSequenceClassifier<CoreMap> namedEntityRecognizer;
		
	/**
	 * Directly inject the Stanford Classifier into the LocationExtractor.  This requires
	 * you to manually instantiate the classifier.
	 * 
	 * @param namedEntityRecognizer Stanford Named Entity Recognizer instance
	 */
	public NerdLocationExtractor(AbstractSequenceClassifier<CoreMap> namedEntityRecognizer) {
		
		this.namedEntityRecognizer = namedEntityRecognizer;
	}
	
	/**
	 * Grab the Named Entity Recognizer from the SequenceClassifierProvider.
	 * 
	 * @param sequenceClassifierProvider Provider for the Named Entity Recognizer.
	 * @throws Exception Thrown by the injected provider when we attempt to retrieve the classifier.
	 */
	public NerdLocationExtractor(SequenceClassifierProvider sequenceClassifierProvider) throws Exception {
		
		this.namedEntityRecognizer = sequenceClassifierProvider.getClassifier();
	}
	
	/**
	 * Get extracted locations from a plain-text body.
	 * @param text Text content to perform extraction on.
	 * @return List of Location Occurrences.
	 */
	public List<LocationOccurrence> extractLocationNames(String text) {
		
		List<LocationOccurrence> extractedLocations = new ArrayList<LocationOccurrence>();
		
		// The "Triple" represents: <Ontology, Start Index, Stop Index>
		List<Triple<String, Integer, Integer>> extractedResults 
	 		= this.namedEntityRecognizer.classifyToCharacterOffsets(text);
		
		// If we have results...
		if (extractedResults != null){
			
			// Iterate over each "triple"
			for (Triple<String, Integer, Integer> result : extractedResults){
				
				// Determine if the entity is a "Location"
				if (result.first.equalsIgnoreCase(NER_LOCATION_ONTOLOGY)){
					
					// Build a location occurence object
					LocationOccurrence location 
						= new LocationOccurrence(
							extractEntityAtOffsets(text, result.second(), result.third()),
							result.second());
					
					// Add it to the list of "found" locations.
					extractedLocations.add(location);
				}
			}
		}
		
		// Return the list.
		return extractedLocations;
	}
	
	/**
	 * Extract the entity from text at the specified offsets.
	 * 
	 * @param text Text containing entity.
	 * @param start Start Offset.
	 * @param stop End Offset.
	 * @return Entity at those offsets.
	 */
	static String extractEntityAtOffsets(String text, int start, int stop){
		
		return text.substring(start, stop);
	}
}
