package edu.mit.civic.mediacloud.test.who.disambiguation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.mit.civic.mediacloud.extractor.PersonOccurrence;
import edu.mit.civic.mediacloud.who.ResolvedPerson;
import edu.mit.civic.mediacloud.who.disambiguation.KindaDumbDisambiguationStrategy;

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

}
