package edu.mit.civic.clavin.resolver;

import java.util.ArrayList;
import java.util.List;

import com.berico.clavin.gazetteer.Place;
import com.berico.clavin.resolver.ResolvedLocation;

/**
 * A grouping of ResolvedLocations in one document that all have the same Place
 * @author rahulb
 */
public class ResolvedLocationGroup {

    List<ResolvedLocation> resolvedLocations;
    
    public ResolvedLocationGroup(){
        resolvedLocations = new ArrayList<ResolvedLocation>();
    }
    
    public boolean add(ResolvedLocation resolvedLocation){
        boolean worked = false;
        if( (resolvedLocations.size()==0) || (resolvedLocation.getPlace().getId()==this.getPlace().getId())){
            resolvedLocations.add(resolvedLocation);
            worked = true;
        }
        return worked;
    }
    
    public Place getPlace(){
        return resolvedLocations.get(0).getPlace();
    }
    
    /**
     * I'm not sure this means anything useful right now
     * @return
     */
    public float getAverageConfidence(){
        float sum = 0;
        for (ResolvedLocation loc: resolvedLocations){
            sum += loc.getConfidence();
        }
        return sum / this.getOccurrenceCount();
    }
    
    public int getOccurrenceCount(){
        return resolvedLocations.size();
    }
    
}
