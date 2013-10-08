package edu.mit.civic.mediacloud.where.disambiguation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Manages a chain of passes used to disambiguate (ie. lets you try different orders and combinations of passes)  
 */
public class MultiplePassChain {

    private static final Logger logger = LoggerFactory
            .getLogger(MultiplePassChain.class);

    private List<GenericPass> passes; 
    
    private int callCount = 0;
    
    public MultiplePassChain(){
        passes = new ArrayList<GenericPass>();
    }
    
    public void add(GenericPass pass){
        passes.add(pass);
    }
    
    public List<ResolvedLocation> disambiguate(List<List<ResolvedLocation>> possibilities){
        callCount+= 1;
        List<ResolvedLocation> bestCandidates = new ArrayList<ResolvedLocation>();
        int round = 0;
        for(GenericPass pass:passes){
            logger.info("Pass "+round+": "+pass.getDescription());
            pass.execute(possibilities, bestCandidates);
            round += 1;
        }
        return bestCandidates;
    }
    
    public void logPassTriggerStats(){
        int round = 0;
        logger.info("Called "+callCount+" times:");
        for(GenericPass pass:passes){
            logger.info("  Pass "+round+": "+pass.getDescription());
            logger.info("    triggered "+pass.getTriggerCount()+" times");
            round += 1;
        }
    }
    
}
