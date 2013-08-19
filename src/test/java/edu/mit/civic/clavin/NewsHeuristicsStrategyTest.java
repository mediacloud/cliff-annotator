package edu.mit.civic.clavin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.berico.clavin.resolver.ResolvedLocation;
import com.berico.clavin.util.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.mit.civic.clavin.server.ParseManager;

/**
 * These out the Strategy we've created for figuring out what country a news story is in.
 */
public class NewsHeuristicsStrategyTest {
 
    private static Gson gson = new Gson();

    private static final int STATUE_OF_MINNESOTA = 5037779;
    private static final int CITY_OF_BEIJING = 1816670;
    private static final int COUNTRY_OF_CHINA = 1814991;
    private static final int COUNTRY_OF_INDIA = 1269750;
    private static final int COUNTRY_OF_AUSTRALIA = 2077456;

    @Test
    public void testMinnesotaExample() throws Exception {
        File inputFile = new File("src/test/resources/sample-docs/minnesota.txt");
        String inputString = TextUtils.fileToString(inputFile);
        List<ResolvedLocation> results = ParseManager.locateRaw(inputString);
        assertTrue(resultsContainsPlaceId(results, STATUE_OF_MINNESOTA));
    }

    @Test
    public void testUsExample() throws Exception {
        File inputFile = new File("src/test/resources/sample-docs/speech.txt");
        String inputString = TextUtils.fileToString(inputFile);
        List<ResolvedLocation> results = ParseManager.locateRaw(inputString);
        assertTrue(resultsContainsPlaceId(results, STATUE_OF_MINNESOTA));
        assertTrue(resultsContainsPlaceId(results, CITY_OF_BEIJING));
    }

    @Test
    public void testCountryExample() throws Exception {
        File inputFile = new File("src/test/resources/sample-docs/multi-country.txt");
        String inputString = TextUtils.fileToString(inputFile);
        List<ResolvedLocation> results = ParseManager.locateRaw(inputString);
        assertTrue(resultsContainsPlaceId(results, COUNTRY_OF_CHINA));
        assertTrue(resultsContainsPlaceId(results, COUNTRY_OF_INDIA));
        assertTrue(resultsContainsPlaceId(results, COUNTRY_OF_AUSTRALIA));
    }

    @Test
    public void testBBCExamples() throws Exception {
        List<CodedArticle> articles = loadExamplesFromFile("src/test/resources/sample-docs/bbc_annotated.json");
        assertEquals(24, articles.size());
        for(CodedArticle article: articles){
            assertTrue("Didn't find "+article.handCodedPlaceName+" ("+article.primaryPlaceId+") in article "+article.mediacloudId,
                    article.primaryPlaceIsParsed());           
        }
    }

    @Test
    public void testNewYorkTimesExamples() throws Exception {
        List<CodedArticle> articles = loadExamplesFromFile("src/test/resources/sample-docs/nyt_annotated.json");
        assertEquals(25, articles.size());
        for(CodedArticle article: articles){
            assertTrue("Didn't find "+article.handCodedPlaceName+" ("+article.primaryPlaceId+") in article "+article.mediacloudId,
                    article.primaryPlaceIsParsed());           
        }
    }
    
    @Test
    public void testHuffingtonPostExamples() throws Exception {
        List<CodedArticle> articles = loadExamplesFromFile("src/test/resources/sample-docs/huffington_post_annotated.json");
        assertEquals(23, articles.size());
        for(CodedArticle article: articles){
            assertTrue("Didn't find "+article.handCodedPlaceName+" ("+article.primaryPlaceId+") in article "+article.mediacloudId,
                    article.primaryPlaceIsParsed());           
        }
    }

    private List<CodedArticle> loadExamplesFromFile(String filename) throws Exception {
        Type listType = new TypeToken<List<CodedArticle>>() {}.getType();
        String json = FileUtils.readFileToString(new File(filename));
        List<CodedArticle> articles = gson.fromJson(json, listType);
        return articles;
    }
    
    private class CodedArticle{
        public int mediacloudId;
        public String text;
        public String handCodedPlaceName;
        public int primaryPlaceId;
        
        public boolean primaryPlaceIsParsed() throws Exception{
            List<ResolvedLocation> results = ParseManager.locateRaw(text);
            if(primaryPlaceId==0){  // no places mentioned in article!
                return results.size()==0;
            } else {
                return resultsContainsPlaceId(results, primaryPlaceId);
            }
        }
    }
    
    public static boolean resultsContainsPlaceId(List<ResolvedLocation> results, int placeId){
        for(ResolvedLocation location: results){
            if(location.geoname.geonameID==placeId){
                return true;
            }
        }
        return false;
    }
    
}