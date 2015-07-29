package org.mediameter.cliff.places.focus;

import java.util.List;

import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Wrapper around the concept of looking at a list of places and deciding which country a
 * document is "about".  This exists so we can try and compare different strategies.
 * @author rahulb
 */
public interface FocusStrategy {

    public abstract List<FocusLocation> selectCountries(
            List<ResolvedLocation> resolvedLocations);
    public abstract List<FocusLocation> selectStates(
            List<ResolvedLocation> resolvedLocations);
    public abstract List<FocusLocation> selectCities(
            List<ResolvedLocation> resolvedLocations);

}