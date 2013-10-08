package edu.mit.civic.mediacloud;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Let the user try out parsing some text via the web browser (returning JSON results)
 * @author rahulb
 */
public class StatusRequestHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(StatusRequestHandler.class);

    private static Gson gson = new Gson();
    
    private ParseServer parent; 
    
    public StatusRequestHandler(ParseServer geoServer) {
        parent = geoServer;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("GET")) {
            logger.info("Status Request");

            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/json");
            responseHeaders.set("charset", "utf-8");
            exchange.sendResponseHeaders(200, 0);

            OutputStream responseBody = exchange.getResponseBody();
            HashMap status = new HashMap();
            status.put("status","ok");
            status.put("totalSocketRequests",parent.socketServer.getTotalRequests());
            status.put("socketClients", parent.socketServer.getClientRequestInfo());
            status.put("socketServerPort",parent.socketServer.getPort());
            status.put("webServerPort",ParseServer.WEB_PORT);
            
            responseBody.write(gson.toJson(status).getBytes());
            responseBody.close();
        }
    }
}
