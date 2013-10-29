package edu.mit.civic.mediacloud.test.demonyms;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.mit.civic.mediacloud.demonyms.DemonymMap;
import edu.mit.civic.mediacloud.demonyms.WikipediaDemonymMap;

public class WikipediaDemonymMapTest {

    DemonymMap demonyms;
    
    @Before
    public void setUp() throws Exception {
        demonyms = new WikipediaDemonymMap();
    }

    @Test
    public void testCount() {
        assertTrue("Wrong number of keys ("+demonyms.getCount()+")", demonyms.getCount()==532);
    }
    
    @Test
    public void testAmerican(){
        String result = demonyms.getCountry("American");
        assertTrue("Doesn't contain American",demonyms.contains("American"));
        assertTrue("American didn't map to USA ("+result+")",result.equals("united states"));
    }
    
    @Test
    public void testBritsh(){
        String result = demonyms.getCountry("British");
        assertTrue("Doesn't contain British",demonyms.contains("British"));
        assertTrue("British didn't map to UK ("+result+")",result.equals("united kingdom"));
    }
    

}
