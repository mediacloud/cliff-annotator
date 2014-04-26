package org.mediameter.cliff.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mediameter.cliff.ParseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapsthe CLAVIN geoparser behind some ports so we can integrate it into other workflows.
 * 
 * @author rahulb
 */
public class ParseNlpJsonServlet extends HttpServlet{
	
	private static final Logger logger = LoggerFactory.getLogger(ParseNlpJsonServlet.class);
	
	public ParseNlpJsonServlet() {
	}	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

        logger.info("Text Parse Request");
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF=8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String results = null;
        String text = request.getParameter("q");
        if(text==null){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                results = ParseManager.parseFromNlpJson(text);
                logger.info(results);
            } catch(Exception e){   // try to give the user something useful
                logger.error(e.toString());
                results = ParseManager.getErrorText(e.toString());
            }
            response.getWriter().write(results);
        }
	}
	
}
