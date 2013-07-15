package edu.mit.civic.clavin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;

/**
 * Wrap sthe CLAVIN geoparser behind some ports so we can integrate it into other workflows.
 *
 * TODO: put the port in a config file
 * 
 * @author rahulb
 */
public class GeoServer {
	
	private static final Logger logger = LoggerFactory.getLogger(GeoServer.class);

	static final int WEB_PORT = 8080;
	private static final int SOCKET_PORT = 4000;
		
	public HttpServer webServer;
	protected MultiClientSocketServer socketServer;
	
	public GeoServer() {
	    createWebServer();
	    createSocketServer();
	}
	
	private void createSocketServer(){
        socketServer = new MultiClientSocketServer(SOCKET_PORT);
	}
	
	private void createWebServer(){
	    InetSocketAddress addr = new InetSocketAddress(WEB_PORT);
	    try {
            webServer = HttpServer.create(addr, 0);
            webServer.createContext("/parse", new ParseRequestHandler());
            webServer.setExecutor(Executors.newCachedThreadPool());
            webServer.createContext("/status", new StatusRequestHandler(this));
            webServer.setExecutor(Executors.newCachedThreadPool());
        } catch (IOException e) {
            logger.error("Unable to start listening for web requests on port "+WEB_PORT);
            logger.error(e.toString()); 
            e.printStackTrace();
        }
	}
	
	public void start() {
        if(webServer!=null){
            webServer.start();
            logger.info("Web server is listening on port "+WEB_PORT );      
        }
        if(socketServer!=null){
            socketServer.run();
        }
	}
	
	public static void main(String[] args){
        GeoServer myServer = new GeoServer();
        myServer.start();
	}
	
}
