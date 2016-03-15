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
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Worker extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
    private UserService userService=UserServiceFactory.getUserService();
	
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	if(userService.getCurrentUser()==null) {response.sendRedirect("/login");}
    	else
    	{
    	 String content = request.getParameter("content");
         String imgKeyName = request.getParameter("img-key");	  
         
         Key imgKey=KeyFactory.stringToKey(imgKeyName);
         
         syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	  
		
		// Do something with key.
		Entity ent=new Entity("Comment",imgKey);
		
		ent.setProperty("author",userService.getCurrentUser().getNickname());
		ent.setProperty("content",content);
		Date createdDate = new Date();
		ent.setProperty("date",createdDate);
		  
		syncCache.put(ent.getKey().getName(), ent);
		datastore.put(ent);
    	}
    }
}