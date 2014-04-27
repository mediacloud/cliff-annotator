package org.mediameter.cliff.test.gdelt;

public class GdeltEvent {

    private GdeltActor actor1;
    private GdeltActor actor2;
    private String sourceUrl;
    
    public GdeltEvent(GdeltActor a1, GdeltActor a2, String url){
        this.actor1 = a1;
        this.actor2 = a2;
        this.sourceUrl = url;
    }

    public GdeltActor getActor1() {
        return actor1;
    }

    public GdeltActor getActor2() {
        return actor2;
    }
    
    public String getSourceUrl() {
        return sourceUrl;
    }
    
}
