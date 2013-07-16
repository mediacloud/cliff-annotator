package edu.mit.civic.clavin.server;

import java.io.IOException;
import java.io.OutputStream;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Let the user try out parsing some text via the web browser (returning JSON results)
 * @author rahulb
 */
public class StatusRequestHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(StatusRequestHandler.class);

    private GeoServer parent; 
    
    public StatusRequestHandler(GeoServer geoServer) {
        parent = geoServer;
    }

    @SuppressWarnings("unchecked")
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("GET")) {
            logger.info("Status Request");

            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/json");
            responseHeaders.set("charset", "utf-8");
            exchange.sendResponseHeaders(200, 0);

            OutputStream responseBody = exchange.getResponseBody();
            JSONObject status = new JSONObject();
            status.put("status","ok");
            status.put("activeSocketClientCount",parent.socketServer.getClientCount());
            status.put("socketServerPort",parent.socketServer.getPort());
            status.put("webPort",GeoServer.WEB_PORT);
            
            responseBody.write(status.toString().getBytes());
            responseBody.close();
        }
    }
}
