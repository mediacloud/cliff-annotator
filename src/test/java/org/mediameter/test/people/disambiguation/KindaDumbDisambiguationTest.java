package org.mediameter.test.people.disambiguation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mediameter.extractor.PersonOccurrence;
import org.mediameter.people.ResolvedPerson;
import org.mediameter.people.disambiguation.KindaDumbDisambiguationStrategy;

public class KindaDumbDisambiguationTest {
    
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testNonDuplicates() {
        List<PersonOccurrence> occurrences = new ArrayList<PersonOccurrence>(); 
        occurrences.add( new PersonOccurrence("Obama", 0));
        occurrences.add( new PersonOccurrence("Romney", 100));
        KindaDumbDisambiguationStrategy strategy = new KindaDumbDisambiguationStrategy();
        List<ResolvedPerson> resolvedPeople = strategy.select(occurrences);
        assertEquals("Removed a non-duplicate!", 2, resolvedPeople.size());
    }

    @Test
    public void testExactDuplicate() {
        List<PersonOccurrence> occurrences = new ArrayList<PersonOccurrence>(); 
        occurrences.add( new PersonOccurrence("Obama", 0));
        occurrences.add( new PersonOccurrence("Obama", 100));
        KindaDumbDisambiguationStrategy strategy = new KindaDumbDisambiguationStrategy();
        List<ResolvedPerson> resolvedPeople = strategy.select(occurrences);
        assertEquals("Exact duplicate not removed!", 1, resolvedPeople.size());
    }
    
    @Test
    public void testCaseDuplicate() {
        List<PersonOccurrence> occurrences = new ArrayList<PersonOccurrence>(); 
        occurrences.add( new PersonOccurrence("Obama", 0));
        occurrences.add( new PersonOccurrence("obama", 100));
        KindaDumbDisambiguationStrategy strategy = new KindaDumbDisambiguationStrategy();
        List<ResolvedPerson> resolvedPeople = strategy.select(occurrences);
        assertEquals("Non-matching case duplicate not removed!", 1, resolvedPeople.size());
    }
    @Test
    public void testNotFullNameDuplicate() {
        List<PersonOccurrence> occurrences = new ArrayList<PersonOccurrence>(); 
        occurrences.add( new PersonOccurrence("Obama", 0));
        occurrences.add( new PersonOccurrence("Barack Obama", 100));
        KindaDumbDisambiguationStrategy strategy = new KindaDumbDisambiguationStrategy();
        List<ResolvedPerson> resolvedPeople = strategy.select(occurrences);
        assertEquals("Non-matching names duplicate not removed!", 1, resolvedPeople.size());
    }
    @Test
    public void testChooseLongestName() {
        List<PersonOccurrence> occurrences = new ArrayList<PersonOccurrence>(); 
        occurrences.add( new PersonOccurrence("Obama", 0));
        occurrences.add( new PersonOccurrence("Barack Obama", 100));
        KindaDumbDisambiguationStrategy strategy = new KindaDumbDisambiguationStrategy();
        List<ResolvedPerson> resolvedPeople = strategy.select(occurrences);
        ResolvedPerson person = resolvedPeople.get(0);
        assertEquals("Resolved person not choosing longest name", "Barack Obama", person.getName());
    }
}
