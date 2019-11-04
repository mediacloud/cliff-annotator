package org.mediacloud.cliff.extractor;

import java.io.IOException;
import java.util.Map;

import org.mediacloud.cliff.CliffConfig;

/**
 * Created with IntelliJ IDEA.
 * User: aaslinger
 * Date: 7/22/15
 * Time: 1:41 PM
 * <p/>
 * Developed By OpenWhere, Inc.
 */
public interface EntityExtractor {
	
    public static final String GERMAN = "DE";
    public static final String SPANISH = "ES";
    public static final String ENGLISH = "EN";

	public static final String[] VALID_LANGUAGES = {GERMAN, SPANISH, ENGLISH};

    public ExtractedEntities extractEntities(String textToParse, boolean manuallyReplaceDemonyms, String language);

    public ExtractedEntities extractEntities(String textToParse, boolean manuallyReplaceDemonyms);

    @SuppressWarnings("rawtypes")
    public ExtractedEntities extractEntitiesFromSentences(Map[] sentences, boolean manuallyReplaceDemonyms, String language);
    @SuppressWarnings("rawtypes")
    public ExtractedEntities extractEntitiesFromSentences(Map[] sentences, boolean manuallyReplaceDemonyms);

    public void initialize(CliffConfig config) throws ClassCastException, IOException, ClassNotFoundException;

    public String getName();
}
