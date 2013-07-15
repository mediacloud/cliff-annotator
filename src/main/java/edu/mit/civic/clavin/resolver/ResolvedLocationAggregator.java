package edu.mit.civic.clavin.resolver;

import java.util.Collection;
import java.util.HashMap;

import com.berico.clavin.resolver.ResolvedLocation;

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