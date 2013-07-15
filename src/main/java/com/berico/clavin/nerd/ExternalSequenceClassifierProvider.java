package com.berico.clavin.nerd;

import java.util.Properties;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.util.CoreMap;

/**
 * Initializes the extractor from an external training set (fileToLoad)
 * provided by Stanford (all.3class, conll.4class, muc.7class) and 
 * a Property set.  If the property set is not provided, the System properties
 * are used instead (this will cause Stanford to barf on System.out about
 * properties it doesn't recognize, but who cares?).
 * 
 * @author Richard Clayton (Berico Technologies)
 */
public class ExternalSequenceClassifierProvider implements SequenceClassifierProvider {

	/**
	 * Training set.
	 */
	private final String fileToLoad;
	
	/**
	 * Configuration properties.
	 */
	private Properties properties = System.getProperties();

	/**
	 * Initialize the provider with the training set.
	 * @param fileToLoad Location of the training set on the file system.
	 */
	public ExternalSequenceClassifierProvider(String fileToLoad){
		this.fileToLoad = fileToLoad;
	}

	/**
	 * Initialize the provider with a training set and a custom property bag.
	 * @param fileToLoad Location of the training set on the file system.
	 * @param properties Custom Property Bag.
	 */
	public ExternalSequenceClassifierProvider(String fileToLoad, Properties properties){
		this.fileToLoad = fileToLoad;
		this.properties = properties;
	}

	/**
	 * Get an initialized Classifier.
	 */
	@SuppressWarnings("unchecked")
	public AbstractSequenceClassifier<CoreMap> getClassifier() throws Exception {

		return (AbstractSequenceClassifier<CoreMap>)
				CRFClassifier.getClassifier(fileToLoad, properties);
	}
}
