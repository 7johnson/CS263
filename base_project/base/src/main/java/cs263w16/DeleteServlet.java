package cs263w16;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
@SuppressWarnings("serial")
public class DeleteServlet extends HttpServlet {
	
	private UserService userService=UserServiceFactory.getUserService();
	private DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
	private MemcacheService syncCache=MemcacheServiceFactory.getMemcacheService();
	
	@Override
	public void doGet(HttpServletRequest req,HttpServletResponse resp) throws IOException
	{
		
		syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		
		if(userService.getCurrentUser()==null) resp.sendRedirect("/login");
		else
		{
			String imgKeyName=req.getParameter("img-key");
			
			resp.setContentType("text/html");
			resp.getWriter().println("<html><body>");
			
			if(!req.getParameterNames().hasMoreElements() || imgKeyName==null)
			{
				resp.getWriter().println("<h1>Please input valid delete URL!</h1>");
			}
			else
			{
				
				Key imgKey=KeyFactory.stringToKey(imgKeyName);
			
				Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, imgKey);
		        Query keyBasedQuery = new Query("Photo").setFilter(keyFilter);
		        List<Entity> results = datastore.prepare(keyBasedQuery).asList(FetchOptions.Builder.withDefaults());
		        
		        if(results.isEmpty()) resp.getWriter().println("Empty!");
				
				datastore.delete(imgKey);
				
				if(syncCache.get(imgKeyName)!=null)
					syncCache.delete(new Entity("Photo",imgKey));
				
				resp.sendRedirect("/user");
			}
			
			resp.getWriter().println("</body></html>");
			
		}
		
	}
}
