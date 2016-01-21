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
	  
	  String keyName=req.getParameter("keyname");
	  String value=req.getParameter("value");
	  
	  if(!req.getParameterNames().hasMoreElements())
	  {
		  Query taskDataQuery = new Query("TaskData");
		  
		  List<Entity> results = datastore.prepare(taskDataQuery)
                                .asList(FetchOptions.Builder.withDefaults());
		  
		  int count=0;
		  
		  for(Entity result: results)
		  {
			  count++;
			  resp.getWriter().println("<h2>"+count+" "+"key:"
			  +result.getKey().getName()+" "+"value:"+result.getProperty("value")
			  +" "+"created at "+result.getProperty("date")+"</h2>");
		  }
		  
	  }
	  else if(keyName!=null && value==null)
	  {
		  Key entKey=KeyFactory.createKey("TaskData", keyName);
		  
		  Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, entKey);
          Query q = new Query("TaskData").setFilter(keyFilter);
          List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		  
		  if (results.isEmpty()) {
              resp.getWriter().println("<h2>Nothing added!</h2>"); 
          }
		  else
		  {
			    int count=0;
				for(Entity result: results)
				{
					count++;
					
					resp.getWriter().println("<h2>"+count+" "+"key:"
					+result.getKey().getName()+" "+"value:"+result.getProperty("value")
					+" "+"created at "+result.getProperty("date")+"</h2>");
				}
			  
		  }
	  }
	  else if(keyName!=null && value!=null)
	  {
		  Entity ent=new Entity("TaskData",keyName);
		  
		  ent.setProperty("value",value);
		  Date createdDate = new Date();
		  ent.setProperty("date",createdDate);
		  
		  datastore.put(ent);
		  
		  resp.getWriter().println("<h2>Store"+keyName+"and"+value+"in Datastore</h2>");
	  }
	  else
	  {
		  resp.getWriter().println("<h2>Wrong Format Input! Check again!</h2>");
	  }
	  
      resp.getWriter().println("</body></html>");
  }
}