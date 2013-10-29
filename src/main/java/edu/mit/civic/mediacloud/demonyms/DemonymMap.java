package edu.mit.civic.mediacloud.demonyms;

public interface DemonymMap {

    public abstract boolean contains(String demonymCandidate);

    public abstract String getCountry(String demonymCandidate);

    public abstract int getCount();

}