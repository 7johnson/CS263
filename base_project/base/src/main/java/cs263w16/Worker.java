// The Worker servlet should be mapped to the "/worker" URL.
package cs263w16;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;
import java.util.logging.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.Query.Filter;

import com.google.appengine.api.memcache.*;

public class Worker extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyName = request.getParameter("keyname");
        String value = request.getParameter("value");

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	  
	    syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	  
		
		// Do something with key.
		Entity ent=new Entity("TaskData",keyName);
		
		ent.setProperty("value",value);
		Date createdDate = new Date();
		ent.setProperty("date",createdDate);
		  
		syncCache.put(ent.getKey().getName(), ent);
		datastore.put(ent);
		
    }
}