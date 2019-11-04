package org.mediacloud.cliff.test.places.disambiguation;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mediacloud.cliff.ParseManager;
import org.mediacloud.cliff.extractor.ExtractedEntities;
import org.mediacloud.cliff.test.util.TestPlaces;
import org.mediacloud.cliff.util.MuckUtils;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class FranceNLPPlaceTest {
    
    @Test
    public void testFranceFromNlp() throws Exception{
        String fileName = "story307385477.json";
        File file = new File("src/test/resources/sample-muck-json/"+fileName); 
        String json = FileUtils.readFileToString(file);
        ExtractedEntities entities  = MuckUtils.entitiesFromNlpJsonString(json);
        assertEquals("Wrong number of location occurrences", 5, entities.getLocations().size());
        assertEquals("Wrong number of people occurrences", 18, entities.getPeople().size());
        assertEquals("Wrong number of organization occurrences", 8, entities.getOrganizations().size());
        entities = ParseManager.getParserInstance().resolve(entities);
        List<ResolvedLocation> results = entities.getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 5!",5,results.size());
        assertEquals(TestPlaces.COUNTRY_IRELAND,results.get(0).getGeoname().getGeonameID());
        assertEquals(TestPlaces.COUNTRY_FRANCE,results.get(1).getGeoname().getGeonameID());
    }

}
