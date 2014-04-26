package org.mediameter.cliff.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mediameter.cliff.extractor.ExtractedEntities;
import org.mediameter.cliff.muck.MuckUtils;

public class MuckUtilsTest {
    
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testOnlyLocations() throws IOException {
        ExtractedEntities entities = loadTestFile("nlp-test-locations.json");
        assertEquals("Wrong number of location occurrences", 3, entities.getLocations().size());
    }
    
    @Test
    public void testNoEntities() throws IOException {
        ExtractedEntities entities = loadTestFile("nlp-test-nothing.json");
        assertEquals("Wrong number of location occurrences", 0, entities.getLocations().size());
        assertEquals("Wrong number of people occurrences", 0, entities.getPeople().size());
    }
    
    @Test
    public void testOnlyPeople() throws IOException {
        ExtractedEntities entities = loadTestFile("nlp-test-people.json");
        assertEquals("Wrong number of people occurrences", 1, entities.getPeople().size());
    }
    
    @Test
    public void testEverything() throws IOException {
        ExtractedEntities entities = loadTestFile("nlp-test-everything.json");
        assertEquals("Wrong number of location occurrences", 1, entities.getLocations().size());
        assertEquals("Wrong number of people occurrences", 1, entities.getPeople().size());
    }
    
    private ExtractedEntities loadTestFile(String fileName) throws IOException{
        String json = FileUtils.readFileToString(new File("src/test/resources/sample-muck-json/"+fileName));
        return MuckUtils.entitiesFromJsonString(json);
    }

}
