package org.mediameter.cliff.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mediameter.cliff.extractor.ExtractedEntities;
import org.mediameter.cliff.util.MuckUtils;

public class MuckUtilsTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void testStory1() throws IOException {
        String fileName = "nlp-test-1.json";
        File file = new File("src/test/resources/sample-muck-json/"+fileName); 
        String json = FileUtils.readFileToString(file);
        
        ExtractedEntities entities  = MuckUtils.entitiesFromNlpJsonString(json);
        assertEquals("Wrong number of location occurrences", 19, entities.getLocations().size());
        assertEquals("Wrong number of people occurrences", 15, entities.getPeople().size());
        assertEquals("Wrong number of organization occurrences", 4, entities.getOrganizations().size());
    }
    
}
