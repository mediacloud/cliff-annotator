package edu.mit.civic.mediacloud.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.mit.civic.mediacloud.test.where.DisambiguationTest;
import edu.mit.civic.mediacloud.test.where.SpecificCaseTest;


/**
 * Runs all JUnit tests.
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
    edu.mit.civic.mediacloud.test.where.SpecificCaseTest.class,
    edu.mit.civic.mediacloud.test.where.DisambiguationTest.class,
})
public class AllTestsSuite {
}
