package com.berico.clavin.nerd;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.util.CoreMap;

/**
 * Convenience interface for Dependency Injection frameworks
 * like Spring for passing a Stanford Classifier into the 
 * NerdLocationExtractor.
 * 
 * @author Richard Clayton (Berico Technologies)
 */
public interface SequenceClassifierProvider {

	AbstractSequenceClassifier<CoreMap> getClassifier() throws Exception;

}
