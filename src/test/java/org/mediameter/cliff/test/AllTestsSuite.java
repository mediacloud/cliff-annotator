package org.mediameter.cliff.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Runs all JUnit tests.
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
    org.mediameter.cliff.test.places.SpecificCaseTest.class,
    org.mediameter.cliff.test.places.HandCodedDisambiguationTest.class,
    org.mediameter.cliff.test.places.substitutions.WikipediaDemonymMapTest.class,
    org.mediameter.cliff.test.MuckUtilsTest.class,
    org.mediameter.cliff.test.people.disambiguation.KindaDumbDisambiguationTest.class
})
public class AllTestsSuite {
}
