package edu.mit.civic.mediacloud.who.disambiguation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.civic.mediacloud.extractor.PersonOccurrence;
import edu.mit.civic.mediacloud.who.ResolvedPerson;

/**
 * Employ a variety of heuristics for picking the best people from a list.
 */
public class KindaDumbDisambiguationStrategy implements PersonDisambiguationStrategy {

    private static final Logger logger = LoggerFactory
            .getLogger(KindaDumbDisambiguationStrategy.class);

    public KindaDumbDisambiguationStrategy() {
    }

    @Override
    public List<ResolvedPerson> select(List<PersonOccurrence> allPossibilities) {
        ArrayList<ResolvedPerson> bestCandidates = new ArrayList<ResolvedPerson>();
        for(PersonOccurrence occurrence: allPossibilities){
            bestCandidates.add(new ResolvedPerson(occurrence));
        }
        return bestCandidates;
    }

    @Override
    public void logStats() {
        return;        
    }
}