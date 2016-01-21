package cs263w16;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.Query.Filter;

import com.google.appengine.api.memcache.*;

@SuppressWarnings("serial")
public class DatastoreServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      
	  resp.setContentType("text/html");
      resp.getWriter().println("<html><body>");
      //resp.getWriter().println("<h2>Hello World</h2>"); //remove this line

	  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	  MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	  
	  syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	  
	  String keyName=req.getParameter("keyname");
	  String value=req.getParameter("value");
	  
	  if(!req.getParameterNames().hasMoreElements())
	  {
		  Query taskDataQuery = new Query("TaskData");
		  
		  List<Entity> results = datastore.prepare(taskDataQuery)
                                .asList(FetchOptions.Builder.withDefaults());
		  Map<String,Integer> map=new HashMap<String,Integer>();
		  
		  resp.getWriter().println("<h1>Datastore Data: </h1>");
		  int count=0;
		  
		  for(Entity result: results)
		  {
			  count++;
			  resp.getWriter().println("<h2>"+count+". "+"key:"
			  +result.getKey().getName()+","+"value:"+result.getProperty("value")
			  +","+"created at "+result.getProperty("date")+"</h2>");
			  
			  if(!map.containsKey(result.getKey().getName()))
				  map.put(result.getKey().getName(),0);
		  }
		  
		  
		  resp.getWriter().println("<h1></h1>");
		  resp.getWriter().println("<h1>MemCache Data: </h1>");
		  
		  count=0;
		  for(String s:map.keySet())
		  {
			  if(syncCache.get(s)!=null)
			  {
				  count++;
				  Entity memResult=(Entity)syncCache.get(s);
				  resp.getWriter().println("<h2>"+count+". "+" "+"key:"
				  +memResult.getKey().getName()+","+"value:"+memResult.getProperty("value")
				  +","+"created at "+memResult.getProperty("date")+"</h2>");
			  }
		  }
	  }
	  else if(keyName!=null && value==null)
	  {
		  boolean notInMem=false;
		  
		  if(syncCache.get(keyName)==null) notInMem=true;
		  
		  Key entKey=KeyFactory.createKey("TaskData", keyName);
		  
		  Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, entKey);
          Query keyBasedQuery = new Query("TaskData").setFilter(keyFilter);
          List<Entity> results = datastore.prepare(keyBasedQuery).asList(FetchOptions.Builder.withDefaults());
		 
		  if (results.isEmpty()) {
			  
			  if(!notInMem)  
			  {
				  datastore.put((Entity)syncCache.get(keyName));
				  resp.getWriter().println("<h2>Memory only!</h2>");
			  }				  
		      else resp.getWriter().println("<h2>Neither!</h2>"); 
          }
		  else
		  {
			    if(notInMem) resp.getWriter().println("<h2>Datastore Only!</h2>");
				else resp.getWriter().println("<h2>Both!</h2>");				
            
			  
		        resp.getWriter().println("<h1>Selected Data: </h1>");
		  
			    for(Entity result: results)
				{		
					if(notInMem) syncCache.put(keyName,result);
					
					resp.getWriter().println("<h2>"+"key:"
					+result.getKey().getName()+","+"value:"+result.getProperty("value")
					+","+"created at "+result.getProperty("date")+"</h2>");
				}
			  
		  }
	  }
	  else if(keyName!=null && value!=null)
	  {
		  Entity ent=new Entity("TaskData",keyName);
		  
		  ent.setProperty("value",value);
		  Date createdDate = new Date();
		  ent.setProperty("date",createdDate);
		  
		  syncCache.put(ent.getKey().getName(), ent);
		  datastore.put(ent);
		  
		  resp.getWriter().println("<h2>Stored "+keyName+" and "+value+" in Datastore!</h2>");
		  resp.getWriter().println("<h2>Stored "+keyName+" and "+value+" in MemCache!</h2>");

	  }
	  else
	  {
		  resp.getWriter().println("<h2>Wrong Format Input!Check again!</h2>");
	  }
	  
      resp.getWriter().println("</body></html>");
  }
}