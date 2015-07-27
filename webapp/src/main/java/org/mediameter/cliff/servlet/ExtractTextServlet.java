package org.mediameter.cliff.servlet;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mediameter.cliff.ParseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;

public class ExtractTextServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ExtractTextServlet.class);
    private static Gson gson = new Gson();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        logger.info("Text Parse Request from "+request.getRemoteAddr());
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        long startTime = System.currentTimeMillis();
        HashMap results = new HashMap();
        String urlString = request.getParameter("url");
        logger.info("Request to parse "+urlString);
        if(urlString==null){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                URL url = new URL(urlString);
                final HTMLDocument htmlDoc = HTMLFetcher.fetch(url);
                final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
                String title = doc.getTitle();
                String text = ArticleExtractor.INSTANCE.getText(doc);
                
                logger.info("  Title: \""+title+"\"");
                logger.debug(text);
                logger.info("done");

                HashMap info = new HashMap();
                info.put("url",urlString);
                info.put("title", title);
                info.put("text", text);
                
                results = ParseManager.getResponseMap(info);
            } catch (Exception e) {
                results = ParseManager.getErrorText(e.getMessage());
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedMillis = endTime - startTime;
        results.put("milliseconds", elapsedMillis);
        
        String jsonResults = gson.toJson(results);
        logger.debug(jsonResults);
        response.getWriter().write(jsonResults);
    }
    
}
