package edu.mit.civic.clavin.test;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berico.clavin.gazetteer.CountryCode;
import com.berico.clavin.resolver.ResolvedLocation;

import edu.mit.civic.clavin.resolver.FrequencyOfMentionAboutnessStrategy;
import edu.mit.civic.clavin.server.ParseManager;

/**
 * Print out the accuracy of our Aboutness algorithm against the hand-coded bake-off data.
 * Not a unit test, because we don't have a threshold at which this is "correct"
 * @author rahulb
 *
 */
public class AboutnessCheck {

    private static final Logger logger = LoggerFactory.getLogger(DisambiguationTest.class);

    private static double getAboutnessAccuracy(String filePath) throws Exception{
        int correct = 0;
        List<CodedArticle> articles = TestUtils.loadExamplesFromFile(filePath);
        for(CodedArticle article: articles){
            logger.info("Testing article "+article.mediacloudId+" (looking for "+article.handCodedPlaceName+" / "+article.handCodedCountryCode+")");
            List<ResolvedLocation> resolvedLocations = ParseManager.locateRaw(article.text);
            List<CountryCode> primaryCountries = FrequencyOfMentionAboutnessStrategy.select(resolvedLocations);
            if(article.isAboutHandCodedCountry(primaryCountries)) correct++;
        }
        return (double)correct/(double)articles.size();
    }

    public static void main(String [] args) throws Exception {
        double nytPct = getAboutnessAccuracy(TestUtils.NYT_JSON_PATH);
        double huffpoPct = getAboutnessAccuracy(TestUtils.HUFFPO_JSON_PATH);
        double bbcPct = getAboutnessAccuracy(TestUtils.BBC_JSON_PATH);
        System.out.println("Aboutness gets NYT "+nytPct+" correct");
        System.out.println("Aboutness gets Huff Po "+huffpoPct+" correct");
        System.out.println("Aboutness gets BBC "+bbcPct+" correct");
    }

}
