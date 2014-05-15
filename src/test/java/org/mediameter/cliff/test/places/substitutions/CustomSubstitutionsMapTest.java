package org.mediameter.cliff.test.places.substitutions;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mediameter.cliff.extractor.StanfordNamedEntityExtractor;
import org.mediameter.cliff.places.substitutions.AbstractSubstitutionMap;
import org.mediameter.cliff.places.substitutions.CustomSubstitutionMap;

public class CustomSubstitutionsMapTest {

    AbstractSubstitutionMap substitutions;
    
    @Before
    public void setUp() throws Exception {
        substitutions = new CustomSubstitutionMap(StanfordNamedEntityExtractor.CUSTOM_SUBSTITUTION_FILE);
    }

    @Test
    public void testShanghaiDisneyland(){
        // not built yet, but referenced in stories
        String result = substitutions.getSubstitution("Shanghai Disneyland");
        assertTrue("Doesn't contain Shanghai Disneyland",substitutions.contains("Shanghai Disneyland"));
        assertTrue("Shanghai Disneyland didn't map to Shanghai ("+result+")",result.equalsIgnoreCase("Shanghai"));
    }

}
