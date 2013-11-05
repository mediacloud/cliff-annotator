package edu.mit.civic.mediacloud.test.where.substitutions;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.mit.civic.mediacloud.where.substitutions.AbstractSubstitutionMap;
import edu.mit.civic.mediacloud.where.substitutions.WikipediaDemonymMap;

public class WikipediaDemonymMapTest {

    AbstractSubstitutionMap demonyms;
    
    @Before
    public void setUp() throws Exception {
        demonyms = new WikipediaDemonymMap();
    }

    @Test
    public void testCount() {
        assertTrue("Wrong number of keys ("+demonyms.getSize()+")", demonyms.getSize()==532);
    }
    
    @Test
    public void testAmerican(){
        String result = demonyms.getSubstitution("American");
        assertTrue("Doesn't contain American",demonyms.contains("American"));
        assertTrue("American didn't map to USA ("+result+")",result.equals("united states"));
    }
    
    @Test
    public void testBritsh(){
        String result = demonyms.getSubstitution("British");
        assertTrue("Doesn't contain British",demonyms.contains("British"));
        assertTrue("British didn't map to UK ("+result+")",result.equals("united kingdom"));
    }
    

}
