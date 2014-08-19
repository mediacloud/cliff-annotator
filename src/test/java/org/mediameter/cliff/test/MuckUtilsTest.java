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
    public void testEverything() throws IOException {
        String fileName = "nlp-test-1.json";
        File file = new File("src/test/resources/sample-muck-json/"+fileName); 
        String json = FileUtils.readFileToString(file);
        
        ExtractedEntities entities  = MuckUtils.entitiesFromJsonString(json);
        assertEquals("Wrong number of location occurrences", 5, entities.getLocations().size());
        assertEquals("Wrong number of people occurrences", 13, entities.getPeople().size());
        assertEquals("Wrong number of organization occurrences", 3, entities.getOrganizations().size());
    }
    
}
