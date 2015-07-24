package org.mediameter.cliff.extractor;

import org.mediameter.cliff.CliffConfig;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: aaslinger
 * Date: 7/22/15
 * Time: 1:41 PM
 * <p/>
 * Developed By OpenWhere, Inc.
 */
public interface EntityExtractor {


    public ExtractedEntities extractEntities(String textToParse, boolean manuallyReplaceDemonyms);

    @SuppressWarnings("rawtypes")
    public ExtractedEntities extractEntitiesFromSentences(Map[] sentences, boolean manuallyReplaceDemonyms);

    public void initialize(CliffConfig config) throws ClassCastException, IOException, ClassNotFoundException;

    public String getName();
}
