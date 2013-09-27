package edu.mit.civic.clavin.test;

import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.berico.clavin.resolver.ResolvedLocation;


/**
 * Runs all JUnit tests.
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
    edu.mit.civic.clavin.test.SpecificCaseTest.class,
})
public class AllTestsSuite {
    // THIS CLASS INTENTIONALLY LEFT BLANK

    public static boolean resultsContainsPlaceId(List<ResolvedLocation> results, int placeId){
        for(ResolvedLocation location: results){
            if(location.geoname.geonameID==placeId){
                return true;
            }
        }
        return false;
    }
}
