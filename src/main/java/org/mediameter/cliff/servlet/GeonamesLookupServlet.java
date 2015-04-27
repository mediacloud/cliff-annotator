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
 * Return basic GeoNames Data 
 * 
 * @author rahulb
 */
public class GeonamesLookupServlet extends HttpServlet{
	
	private static final Logger logger = LoggerFactory.getLogger(GeonamesLookupServlet.class);
	
    private static Gson gson = new Gson();
    
	public GeonamesLookupServlet() {
	}


	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
	    doGet(request,response);
	}	
	
	@Override
    @SuppressWarnings("rawtypes")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

        logger.info("GeoNames Lookup Request from "+request.getRemoteAddr());
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF=8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        HashMap results = null;
        String id = request.getParameter("id");
        logger.debug("id="+id);
        
        if(id==null){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                int geonamesId = Integer.parseInt(id);
                results = ParseManager.getGeoNameInfo(geonamesId);
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
