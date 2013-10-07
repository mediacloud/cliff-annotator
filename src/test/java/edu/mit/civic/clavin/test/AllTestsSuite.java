package edu.mit.civic.clavin.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Runs all JUnit tests.
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
    edu.mit.civic.clavin.test.SpecificCaseTest.class,
    edu.mit.civic.clavin.test.DisambiguationTest.class,
})
public class AllTestsSuite {
}
