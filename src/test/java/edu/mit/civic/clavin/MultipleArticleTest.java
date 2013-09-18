package edu.mit.civic.clavin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berico.clavin.resolver.ResolvedLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.mit.civic.clavin.server.ParseManager;

/**
 * Tests that verify against a small hand-coded corpus of articles.  These check that the
 * list of extracted places contains the hand-coded one. 
 */
public class MultipleArticleTest {
 
    private static final Logger logger = LoggerFactory.getLogger(MultipleArticleTest.class);

    private static Gson gson = new Gson();

    @Test
    public void testNewYorkTimesExamples() throws Exception {
        List<CodedArticle> articles = loadExamplesFromFile("src/test/resources/sample-docs/nyt_annotated.json");
        assertEquals(25, articles.size());
        verifyArticles(articles);
    }

    @Test
    public void testHuffingtonPostExamples() throws Exception {
        List<CodedArticle> articles = loadExamplesFromFile("src/test/resources/sample-docs/huffington_post_annotated.json");
        assertEquals(21, articles.size());
        verifyArticles(articles);
    }

    @Test
    public void testBBCExamples() throws Exception {
        List<CodedArticle> articles = loadExamplesFromFile("src/test/resources/sample-docs/bbc_annotated.json");
        assertEquals(24, articles.size());
        verifyArticles(articles);
    }

    private void verifyArticles(List<CodedArticle> articles) throws Exception{
        for(CodedArticle article: articles){
            logger.info("Testing article "+article.mediacloudId+" (looking for "+article.handCodedPlaceName+" / "+article.handCodedCountryCode+")");
            assertTrue("Didn't find "+article.handCodedPlaceName+" ("+article.handCodedCountryCode+") in article "+article.mediacloudId,
                    article.isHandCodedCountryInResults());           
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
        public String handCodedCountryCode;
        
        public boolean isHandCodedCountryInResults() throws Exception{
            List<ResolvedLocation> results = ParseManager.locateRaw(text);
            if(handCodedCountryCode.length()==0){  // no places mentioned in article!
                return true;
            } else {
                return TestUtils.isCountryCodeInResolvedLocations(results, handCodedCountryCode);
            }
        }
    }
    
}