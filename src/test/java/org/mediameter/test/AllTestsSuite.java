package org.mediameter.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Runs all JUnit tests.
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
    org.mediameter.test.places.SpecificCaseTest.class,
    org.mediameter.test.places.HandCodedDisambiguationTest.class,
    org.mediameter.test.places.substitutions.WikipediaDemonymMapTest.class,
    org.mediameter.test.MuckUtilsTest.class,
    org.mediameter.test.people.disambiguation.KindaDumbDisambiguationTest.class
})
public class AllTestsSuite {
}
