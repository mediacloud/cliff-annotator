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

import com.berico.clavin.gazetteer.CountryCode;
import com.berico.clavin.resolver.ResolvedLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.mit.civic.clavin.resolver.FrequencyOfMentionAboutnessStrategy;
import edu.mit.civic.clavin.server.ParseManager;

/**
 * Tests that verify against a small hand-coded corpus of articles.  These check that the
 * list of extracted places contains the hand-coded one. 
 */
public class MultipleArticleTest {
 
    private static final Logger logger = LoggerFactory.getLogger(MultipleArticleTest.class);

    private static Gson gson = new Gson();

    @Test
    public void testMentionsHandCodedCountry() throws Exception {
        List<CodedArticle> articles;
        articles = loadExamplesFromFile("src/test/resources/sample-docs/nyt_annotated.json");
        assertEquals(25, articles.size());
        verifyArticlesMentionHandCodedCountry(articles);
        articles = loadExamplesFromFile("src/test/resources/sample-docs/huffington_post_annotated.json");
        assertEquals(21, articles.size());
        verifyArticlesMentionHandCodedCountry(articles);
        articles = loadExamplesFromFile("src/test/resources/sample-docs/bbc_annotated.json");
        assertEquals(24, articles.size());
        verifyArticlesMentionHandCodedCountry(articles);
    }

    @Test
    public void testIsAboutHandCodedCountry() throws Exception {
        verifyAccuracyOfAboutness("src/test/resources/sample-docs/nyt_annotated.json");
        verifyAccuracyOfAboutness("src/test/resources/sample-docs/huffington_post_annotated.json");
        verifyAccuracyOfAboutness("src/test/resources/sample-docs/bbc_annotated.json");
    }
    
    private void verifyAccuracyOfAboutness(String filePath) throws Exception{
        int correct = 0;
        List<CodedArticle> articles = loadExamplesFromFile(filePath);
        for(CodedArticle article: articles){
            logger.info("Testing article "+article.mediacloudId+" (looking for "+article.handCodedPlaceName+" / "+article.handCodedCountryCode+")");
            List<ResolvedLocation> resolvedLocations = ParseManager.locateRaw(article.text);
            List<CountryCode> primaryCountries = FrequencyOfMentionAboutnessStrategy.select(resolvedLocations);
            if(article.isAboutHandCodedCountry(primaryCountries)) correct++;
        }
        double correctPct = (double)correct/(double)articles.size();
        logger.info("Accuracy of "+filePath+" at "+correctPct);
        assertTrue("Only "+correctPct+" correct", correctPct > 0.9);
    }
    
    private void verifyArticlesMentionHandCodedCountry(List<CodedArticle> articles) throws Exception{
        for(CodedArticle article: articles){
            logger.info("Testing article "+article.mediacloudId+" (looking for "+article.handCodedPlaceName+" / "+article.handCodedCountryCode+")");
            List<ResolvedLocation> resolvedLocations = ParseManager.locateRaw(article.text);
            assertTrue("Didn't find "+article.handCodedPlaceName+" ("+article.handCodedCountryCode+") in article "+article.mediacloudId,
                    article.mentionsHandCodedCountry(resolvedLocations));
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
        
        public boolean isAboutHandCodedCountry(List<CountryCode> primaryCountries){
            if(handCodedCountryCode.length()==0){  // no places mentioned in article!
                return true;
            } else {
                return TestUtils.isCountryCodeInList(handCodedCountryCode, primaryCountries);
            }
        }
        
        public boolean mentionsHandCodedCountry(List<ResolvedLocation> resolvedLocations){
            if(handCodedCountryCode.length()==0){  // no places mentioned in article!
                return true;
            } else {
                return TestUtils.isCountryCodeInResolvedLocations(handCodedCountryCode, resolvedLocations);
            }
        }
    }
    
}