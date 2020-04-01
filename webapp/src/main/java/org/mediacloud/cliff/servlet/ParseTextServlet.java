package org.mediacloud.cliff.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mediacloud.cliff.ParseManager;
import org.mediacloud.cliff.extractor.EntityExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Wrapsthe CLAVIN geoparser behind some ports so we can integrate it into other workflows.
 * 
 * @author rahulb
 */
public class ParseTextServlet extends HttpServlet{
	
	private static final Logger logger = LoggerFactory.getLogger(ParseTextServlet.class);
	
    private static Gson gson = new Gson();
    
	public ParseTextServlet() {
	}


	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
	    doGet(request,response);
	}	
	
	@Override
    @SuppressWarnings("rawtypes")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

        logger.info("Text Parse Request from "+request.getRemoteAddr());
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        HashMap results = null;
        String text = request.getParameter("q");
        String replaceAllDemonymsStr = request.getParameter("replaceAllDemonyms");
        boolean manuallyReplaceDemonyms = (replaceAllDemonymsStr==null) ? false : Boolean.parseBoolean(replaceAllDemonymsStr);
        String language = request.getParameter("language");
        if (language == null) {
        	language = EntityExtractor.ENGLISH;
        }
        // check for invalid language argument
        if (!Arrays.asList(EntityExtractor.VALID_LANGUAGES).contains(language)) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        	return;
        }
        logger.debug("q="+text);
        logger.debug("replaceAllDemonyms="+manuallyReplaceDemonyms);
        
        if(text==null){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                results = ParseManager.parseFromText(text,manuallyReplaceDemonyms, language);
            } catch(Exception e){   // try to give the user something useful
                logger.error(e.toString(), e);
            	StringWriter sw = new StringWriter();
            	PrintWriter pw = new PrintWriter(sw);
            	e.printStackTrace(pw);
            	results = ParseManager.getErrorText(sw.toString());
            }
            String jsonResults = gson.toJson(results);
            logger.info(jsonResults);
            response.getWriter().write(jsonResults);
        }
	}
	
}
