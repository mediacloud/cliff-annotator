package edu.mit.civic.mediacloud.where.aboutness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * This doesn't seem to help much (compared the the FrequencyofMention strategy), even when I
 * tried different values for the threshold.
 * @author rahulb
 */
@Deprecated
public class PercentageOfMentionsAboutnessStrategy implements AboutnessStrategy {

    private static final double THRESHOLD = 0.4; 
    
    private static final Logger logger = LoggerFactory.getLogger(PercentageOfMentionsAboutnessStrategy.class);

    @Override
    public List<CountryCode> select(List<ResolvedLocation> resolvedLocations){
        HashMap<CountryCode,Integer> countryCounts = AboutnessUtils.getCountryCounts(resolvedLocations);
        
        List<CountryCode> countries = new ArrayList<CountryCode>();       
        for(CountryCode countryCode: countryCounts.keySet()){
            double pct = ((double) countryCounts.get(countryCode).intValue()) / ((double) countryCounts.size());
            if( pct > THRESHOLD ){
                countries.add(countryCode);
            }
        }
        return countries;
    }
    
}
