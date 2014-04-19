package edu.mit.civic.mediacloud.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.civic.mediacloud.ParseManager;

/**
 * Wrapsthe CLAVIN geoparser behind some ports so we can integrate it into other workflows.
 * 
 * @author rahulb
 */
public class ParseTextServlet extends HttpServlet{
	
	private static final Logger logger = LoggerFactory.getLogger(ParseTextServlet.class);
	
	public ParseTextServlet() {
	}	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

        logger.info("Text Parse Request");
        response.setContentType("application/json;charse=UTF=8");
        response.setCharacterEncoding("UTF-8");

        String results = null;
        try {
            String text = request.getParameter("q");
            results = ParseManager.parseFromText(text);
            logger.info(results);
        } catch(Exception e){   // try to give the user something useful
            logger.error(e.toString());
            results = ParseManager.getErrorText(e.toString());
        }
        
        response.getWriter().write(results);

	}
	
}
