package org.mediameter.cliff.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mediameter.cliff.ParseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Wraps the CLAVIN geoparser behind some ports so we can integrate it into other workflows.
 * 
 * @author rahulb
 */
public class ParseSentencesServlet extends HttpServlet{
	
	private static final Logger logger = LoggerFactory.getLogger(ParseSentencesServlet.class);
	
    private static Gson gson = new Gson();
    
	public ParseSentencesServlet() {
	}

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
	    doGet(request,response);
	}	
	
	@Override
    @SuppressWarnings("rawtypes")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

        logger.info("Sentences Parse Request from "+request.getRemoteAddr());
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        HashMap results = null;
        String text = request.getParameter("q");
        String replaceAllDemonymsStr = request.getParameter("replaceAllDemonyms");
        boolean manuallyReplaceDemonyms = (replaceAllDemonymsStr==null) ? false : Boolean.parseBoolean(replaceAllDemonymsStr);
        logger.debug("q="+text);
        logger.debug("replaceAllDemonyms="+manuallyReplaceDemonyms);
        
        if(text==null){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                results = ParseManager.parseFromSentences(text,manuallyReplaceDemonyms);
            } catch(Exception e){   // try to give the user something useful
                logger.error(e.toString());
                results = ParseManager.getErrorText(e.toString());
            }
            String jsonResults = gson.toJson(results);
            logger.info(jsonResults);
            response.getWriter().write(jsonResults);
        }
	}
	
}
