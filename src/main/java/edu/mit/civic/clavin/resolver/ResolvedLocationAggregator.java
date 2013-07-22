package edu.mit.civic.clavin.resolver;

import java.util.Collection;
import java.util.HashMap;

import com.berico.clavin.resolver.ResolvedLocation;

/**
 * Manage a map of PlaceIds to sets of resolved locations that share that place
 * @author rahulb
 */
public class ResolvedLocationAggregator {

    protected HashMap<Integer, ResolvedLocationGroup> resolvedLocationGroupById;
    
    public ResolvedLocationAggregator(){
        resolvedLocationGroupById = new HashMap<Integer, ResolvedLocationGroup>();
    }
    
    public void add(ResolvedLocation resolvedLocation){
        Integer id = new Integer(resolvedLocation.getPlace().getId());
        ResolvedLocationGroup group;
        if (!resolvedLocationGroupById.containsKey(id)){
            group = new ResolvedLocationGroup();
            resolvedLocationGroupById.put( id, group);
        } else {
            group = resolvedLocationGroupById.get(id); 
        }
        group.add(resolvedLocation);
    }

    public Collection<ResolvedLocationGroup> getAllResolvedLocationGroups(){
        return resolvedLocationGroupById.values();
    }
}