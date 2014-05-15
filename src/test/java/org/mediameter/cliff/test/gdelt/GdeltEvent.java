package org.mediameter.cliff.test.gdelt;

import java.net.URL;

public class GdeltEvent {

    private String id;
    private GdeltActor actor1;
    private GdeltActor actor2;
    private URL sourceUrl;
    
    public GdeltEvent(String id, GdeltActor a1, GdeltActor a2, URL url){
        this.id = id;
        this.actor1 = a1;
        this.actor2 = a2;
        this.sourceUrl = url;
    }

    public String getId(){
        return id;
    }
    
    public GdeltActor getActor1() {
        return actor1;
    }

    public GdeltActor getActor2() {
        return actor2;
    }
    
    public URL getSourceUrl() {
        return sourceUrl;
    }
    
    @Override
    public String toString(){
        return id+": "+actor1+" & "+actor2+" ("+sourceUrl+")";
    }
    
    
}
