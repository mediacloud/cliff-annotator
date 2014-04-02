package edu.mit.civic.mediacloud.where.aboutness;

import java.util.List;
import java.util.HashMap;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Wrapper around the concept of looking at a list of places and deciding which country a
 * document is "about".  This exists so we can try and compare different strategies.
 * @author rahulb
 */
public interface AboutnessStrategy {

    public abstract List<CountryCode> selectCountries(
            List<ResolvedLocation> resolvedLocations);
    public abstract List<HashMap> selectStates(
            List<ResolvedLocation> resolvedLocations);
    public abstract List<ResolvedLocation> selectCities(
            List<ResolvedLocation> resolvedLocations);

}