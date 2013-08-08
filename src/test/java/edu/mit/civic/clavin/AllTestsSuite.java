package edu.mit.civic.clavin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Runs all JUnit tests.
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
    edu.mit.civic.clavin.NewsHeuristicsStrategyTest.class,
})
public class AllTestsSuite {
    // THIS CLASS INTENTIONALLY LEFT BLANK
}
