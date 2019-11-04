package org.mediacloud.cliff.test.places.substitutions;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mediacloud.cliff.places.substitutions.AbstractSubstitutionMap;
import org.mediacloud.cliff.places.substitutions.WikipediaDemonymMap;

public class WikipediaDemonymMapTest {

    AbstractSubstitutionMap demonyms;
    
    @Before
    public void setUp() throws Exception {
        demonyms = new WikipediaDemonymMap();
    }

    @Test
    public void testCount() {
        assertTrue("Wrong number of keys ("+demonyms.getSize()+")", demonyms.getSize()==535);
    }
    
    @Test
    public void testAmerican(){
        String result = demonyms.getSubstitution("American");
        assertTrue("Doesn't contain American",demonyms.contains("American"));
        assertTrue("American didn't map to USA ("+result+")",result.equals("United States"));
    }
    
    @Test
    public void testBritsh(){
        String result = demonyms.getSubstitution("British");
        assertTrue("Doesn't contain British",demonyms.contains("British"));
        assertTrue("British didn't map to UK ("+result+")",result.equals("United Kingdom"));
    }

    @Test
    public void testSalvadoran(){
        String result = demonyms.getSubstitution("Salvadoran");
        assertTrue("Doesn't contain Salvadoran",demonyms.contains("Salvadoran"));
        assertTrue("Salvadoran didn't map to UK ("+result+")",result.equals("El Salvador"));
    }

    @Test
    public void testEnglishman(){
        String result = demonyms.getSubstitution("Englishman");
        assertTrue("Doesn't contain Englishman",demonyms.contains("Englishman"));
        assertTrue("Englishman didn't map to UK ("+result+")",result.equals("England"));
    }


}
