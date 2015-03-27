package org.mediameter.cliff.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mediameter.cliff.ParseManager;
import org.mediameter.cliff.extractor.ExtractedEntities;
import org.mediameter.cliff.extractor.SentenceLocationOccurrence;
import org.mediameter.cliff.test.util.TestPlaces;

import com.bericotech.clavin.resolver.ResolvedLocation;
import com.google.gson.Gson;

public class EntityParserTest {
    
    @Test
    @SuppressWarnings("rawtypes")
    public void extractAndResolveFromSentences() throws Exception {
        String fileName = "story-sentences-278413513.json";
        File file = new File("src/test/resources/sample-sentence-docs/"+fileName); 
        String jsonText = FileUtils.readFileToString(file);
        Gson gson = new Gson();
        Map[] sentences = gson.fromJson(jsonText, Map[].class);
        ExtractedEntities entities = ParseManager.extractAndResolveFromSentences(sentences, false);
        List<ResolvedLocation> locations = entities.getResolvedLocations();
        assertEquals(locations.size(),1);
        ResolvedLocation loc = locations.get(0);
        assertEquals(loc.geoname.geonameID,TestPlaces.RIKERS_ISLAND);
        assertTrue(loc.location instanceof SentenceLocationOccurrence);
        SentenceLocationOccurrence sentenceLoc = (SentenceLocationOccurrence) loc.location;
        assertEquals(sentenceLoc.storySentenceId,"3279940188");
    }
    
}
