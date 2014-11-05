package org.mediameter.cliff.test.places.disambiguation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.mediameter.cliff.ParseManager;
import org.mediameter.cliff.test.places.CodedArticle;
import org.mediameter.cliff.test.util.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Tests that verify against a small hand-coded corpus of articles.  These check that the
 * list of extracted places contains the hand-coded one. 
 */
public class HandCodedDisambiguationTest {
 
    private static final Logger logger = LoggerFactory.getLogger(HandCodedDisambiguationTest.class);

    @Test
    public void testHuffPoHandCodedArticles() throws Exception {
        List<CodedArticle> articles = TestUtils.loadExamplesFromFile(TestUtils.HUFFPO_JSON_PATH);
        assertEquals(22, articles.size());
        verifyArticlesMentionHandCodedCountry(articles,"huff");
    }

    @Test
    public void testBbcHandCodedArticles() throws Exception{
        List<CodedArticle> articles = TestUtils.loadExamplesFromFile(TestUtils.BBC_JSON_PATH);
        assertEquals(25, articles.size());
    }
    
    @Test
    public void testNytHandCodedArticles() throws Exception{
        List<CodedArticle> articles = TestUtils.loadExamplesFromFile(TestUtils.NYT_JSON_PATH);
        assertEquals(23, articles.size());
    }

    private void verifyArticlesMentionHandCodedCountry(List<CodedArticle> articles, String source) throws Exception{
        for(CodedArticle article: articles){
            logger.info("Testing article "+article.mediacloudId+" (looking for "+article.handCodedPlaceName+" / "+article.handCodedCountryCode+")");
            List<ResolvedLocation> resolvedLocations = ParseManager.extractAndResolve(article.text).getResolvedLocations();
            String resolvedCountryCodes = "";
            for(ResolvedLocation loc: resolvedLocations){
                resolvedCountryCodes += loc.getGeoname().getPrimaryCountryCode()+" ";
            }
            assertTrue("Didn't find "+source+" "+article.handCodedPlaceName+" ("+article.handCodedCountryCode+") "
                    + "in article "+article.mediacloudId+ "( found "+resolvedCountryCodes+")",
                    article.mentionsHandCodedCountry(resolvedLocations));
        }
    }

}