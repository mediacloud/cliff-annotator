package edu.mit.civic.mediacloud.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Runs all JUnit tests.
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
    edu.mit.civic.mediacloud.test.where.SpecificCaseTest.class,
    edu.mit.civic.mediacloud.test.where.HandCodedDisambiguationTest.class,
    edu.mit.civic.mediacloud.test.where.substitutions.WikipediaDemonymMapTest.class
})
public class AllTestsSuite {
}
