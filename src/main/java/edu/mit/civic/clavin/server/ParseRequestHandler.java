package edu.mit.civic.clavin.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Let the user try out parsing some text via the web browser (returning JSON results)
 * @author rahulb
 */
public class ParseRequestHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(ParseRequestHandler.class);

    /**
     * TODO: handle PUT instead of get
     */
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("GET")) {

            logger.info("Parse Request");
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/json");
            responseHeaders.set("charset", "utf-8");
            exchange.sendResponseHeaders(200, 0);

            URI uri = exchange.getRequestURI();
            String results = "";
            try {
                results = ParseManager.locate(uri.getQuery());
                logger.info(results);
            } catch(Exception e){   // try to give the user something useful
                logger.error(e.toString());
                results = ParseManager.getErrorText(e.toString());
            }

            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(results.getBytes());
            responseBody.close();
        }
    }
}
