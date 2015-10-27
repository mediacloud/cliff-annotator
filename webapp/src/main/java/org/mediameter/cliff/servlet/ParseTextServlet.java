package org.mediameter.cliff.servlet;

import java.io.BufferedReader;
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
 * Wrapsthe CLAVIN geoparser behind some ports so we can integrate it into other workflows.
 * 
 * @author rahulb
 */
public class ParseTextServlet extends HttpServlet{
	
	private static final Logger logger = LoggerFactory.getLogger(ParseTextServlet.class);
	
    private static Gson gson = new Gson();
    
	public ParseTextServlet() {
	}

    private void parseTextResults(String text, HttpServletRequest request, HttpServletResponse response)  throws IOException{

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        HashMap results = null;
        String replaceAllDemonymsStr = request.getParameter("replaceAllDemonyms");
        boolean manuallyReplaceDemonyms = (replaceAllDemonymsStr==null) ? false : Boolean.parseBoolean(replaceAllDemonymsStr);
        logger.debug("q="+text);
        logger.debug("replaceAllDemonyms="+manuallyReplaceDemonyms);

        if(text==null){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                results = ParseManager.parseFromText(text,manuallyReplaceDemonyms);
            } catch(Exception e){   // try to give the user something useful
                logger.error(e.toString());
                results = ParseManager.getErrorText(e.toString());
            }
            handleResults(results, response);
        }
    }

    private void handleResults(HashMap results, HttpServletResponse response ) throws IOException{
        String jsonResults = gson.toJson(results);
        logger.info(jsonResults);
        response.getWriter().write(jsonResults);
    }

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        if(request.getParameter("q") != null){
            doGet(request,response);
        }
        else{

            StringBuilder jb = new StringBuilder();
            String line = null;
            try(BufferedReader reader = request.getReader()) {
                request.setCharacterEncoding(StandardCharsets.UTF_8.name());
                while ((line = reader.readLine()) != null)
                    jb.append(line);
            } catch (Exception e) {
                logger.error(e.toString());
                HashMap results = ParseManager.getErrorText(e.toString());
                handleResults(results, response);
                return;
            }

            parseTextResults(jb.toString(), request, response);
        }

	}	
	
	@Override
    @SuppressWarnings("rawtypes")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

        logger.info("Text Parse Request from "+request.getRemoteAddr());

        String text = request.getParameter("q");
        parseTextResults(text, request, response);
	}
	
}
