package cs263w16;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
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
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class ImageShowServlet extends HttpServlet{
	
	private UserService userService=UserServiceFactory.getUserService();
	private DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
	private MemcacheService syncCache=MemcacheServiceFactory.getMemcacheService();
	private ImagesService imagesService=ImagesServiceFactory.getImagesService();
	private BlobstoreService blobstoreService=BlobstoreServiceFactory.getBlobstoreService();
	
	public void doGet(HttpServletRequest req,HttpServletResponse resp) throws IOException
	{
		syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		
		if(req.getUserPrincipal()==null) resp.sendRedirect("/login");
		else
		{
			String imgKeyName=req.getParameter("img-key");
			
			resp.setContentType("text/html");
			resp.getWriter().println("<html>");
			resp.getWriter().println("<head><style>"
					+ "#header {"
					+ "background-color:black;"
					+ "color:white;"
					+ "text-align:center;"
					+ "padding:5px;"
					+ "}"
					+ "#nav1 {"
					+ "line-height:30px;"
					+ "background-color:#eeeeee;"
					+ "height:360px;"
					+ "width:200px;"
					+ "float:left;"
					+ "padding:5px; "
					+  "overflow: scroll;"
					+ "}"
					+ "#nav2 {"
					+ "line-height:30px;"
					+ "background-color:#eeeeee;"
					+ "height:360px;"
					+ "width:470px;"
					+ "float:right;"
					+ "padding:5px; "
					+  "overflow: scroll;"
					+ "}"
					+ "#section {"
					+ "width:350px;"
					//+ "img-align:center;"
					+ "float:left;"
					+ "padding:10px; "
					+ "}"
					+ "#footer {"
					+ "background-color:black;"
					+ "color:white;"
					+ "clear:both;"
					+ "text-align:center;"
					+ "padding:5px;"
					+ "}"
					+".class1 a:link, .class1 a:visited {"
					+ "background-color: #f44336;"
					+ "color: white;"
					+ "padding: 14px 25px;"
					+ "text-align: center; "
					+ "text-decoration: none;"
					+ "display: inline-block;"
					+ "}"
					
					+ ".class1 a:hover, .class1 a:active {"
					+ "background-color: red;"
					+ "}"
					+ "</style></head>");
			
			resp.getWriter().println("<body>");
			
			if(!req.getParameterNames().hasMoreElements() || imgKeyName==null)
			{
				resp.getWriter().println("<h1>Please input valid show URL!</h1>");
			}
			else
			{
				Entity ent;
				Key imgKey=KeyFactory.stringToKey(imgKeyName);
				
				if(syncCache.get(imgKeyName)!=null)
				{
					ent=(Entity) syncCache.get(imgKeyName);
				}
				else
				{
				
				Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, imgKey);
		        Query keyBasedQuery = new Query("Photo").setFilter(keyFilter);
		        List<Entity> results = datastore.prepare(keyBasedQuery).asList(FetchOptions.Builder.withDefaults());
				
		        if(results.isEmpty()) {resp.getWriter().println("Nothing found!");return;}
		        else {ent=results.get(0);syncCache.put(imgKeyName, ent);}
				}
				
		        
		        	
		        Key photoKey=ent.getKey();
				String PhotoURL=(String) ent.getProperty("imageURL");
				BlobKey blobKey=(BlobKey) ent.getProperty("blobKey");
					
				Query commentQuery = new Query("Comment")
		                .setAncestor(imgKey);
					List<Entity> commentResults = datastore.prepare(commentQuery)
		                    .asList(FetchOptions.Builder.withDefaults());
				
					Query tagQuery = new Query("Tag")
			                .setAncestor(imgKey);
						List<Entity> tagResults = datastore.prepare(tagQuery)
			                    .asList(FetchOptions.Builder.withDefaults());
						
					resp.getWriter().println("<div id=\"header\">");
					resp.getWriter().println("<h1>Hello, "+userService.getCurrentUser().getNickname()+"!</h1>");
					resp.getWriter().println("</div>");
					
					resp.getWriter().println("<div id=\"nav1\">");
					resp.getWriter().println("<a href=\"/operate?method=1&"
							+ "img-key="
							+ KeyFactory.keyToString(photoKey)
							+ "\"><h4>Vertical Flip</h4></a>");
					resp.getWriter().println("<a href=\"/operate?method=2&"
							+ "img-key="
							+ KeyFactory.keyToString(photoKey)
							+ "\"><h4>Horizontal Flip</h4></a>");
					resp.getWriter().println("<a href=\"/operate?method=3&"
							+ "img-key="
							+ KeyFactory.keyToString(photoKey)
							+ "\"><h4>Clockwise rotate</h4></a>");
					resp.getWriter().println("<a href=\"/operate?method=4&"
							+ "img-key="
							+ KeyFactory.keyToString(photoKey)
							+ "\"><h4>Counter-clockwise rotate</h4></a>");
					resp.getWriter().println("<a href=\"/operate?method=5&"
							+ "img-key="
							+ KeyFactory.keyToString(photoKey)
							+ "\"><h4>Square crop</h4></a>");
					resp.getWriter().println("<a href=\"/operate?method=6&"
							+ "img-key="
							+ KeyFactory.keyToString(photoKey)
							+ "\"><h4>My photo</h4></a>");
					resp.getWriter().println("<a href=\"/operate?method=7&"
							+ "img-key="
							+ KeyFactory.keyToString(photoKey)
							+ "\"><h4>Blur effect</h4></a>");
					resp.getWriter().println("<a href=\"/operate?method=8&"
							+ "img-key="
							+ KeyFactory.keyToString(photoKey)
							+ "\"><h4>Fit dimensions</h4></a>");
					
					
					resp.getWriter().println("<form action=\"/operate\" method=\"post\">"
							+ "<h5>Width: </h5>"+"<input type=\"text\" name=\"width\">"
							+ "<h5>Height: </h5>"+"<input type=\"text\" name=\"height\">"
							+"<input type=\"hidden\" name=\"img-key\" value="
							+ "\""
							+ KeyFactory.keyToString(photoKey)
							+ "\">"
							+"<input type=\"hidden\" name=\"method\" value=\"1\">"
							+"<h4><input type=\"submit\" value=\"Resize\"></h4>"
							+ "</form>");
					resp.getWriter().println("<form action=\"/operate\" method=\"post\">"
							+ "<h5>Downsize: </h5>"+"<input type=\"text\" name=\"downsize\">"
							+"<input type=\"hidden\" name=\"method\" value=\"2\">"
							+"<input type=\"hidden\" name=\"img-key\" value="
							+ "\""
							+ KeyFactory.keyToString(photoKey)
							+ "\">"
							+"<h4><input type=\"submit\" value=\"Resize\"></h4>"
							+ "</form>");
					resp.getWriter().println("<form action=\"/operate\" method=\"post\">"
							+ "<h5>Upsize: </h5>"+"<input type=\"text\" name=\"upsize\">"
							+"<input type=\"hidden\" name=\"method\" value=\"3\">"
							+"<input type=\"hidden\" name=\"img-key\" value="
							+ "\""
							+ KeyFactory.keyToString(photoKey)
							+ "\">"
							+"<h4><input type=\"submit\" value=\"Resize\"></h4>"
							+ "</form>");
					resp.getWriter().println("<form action=\"/operate\" method=\"post\">"
							+ "<h5>SquareCrop: </h5>"+"<input type=\"text\" name=\"squareCrop\">"
							+"<input type=\"hidden\" name=\"method\" value=\"4\">"
							+"<input type=\"hidden\" name=\"img-key\" value="
							+ "\""
							+ KeyFactory.keyToString(photoKey)
							+ "\">"
							+"<h4><input type=\"submit\" value=\"Crop\"></h4>"
							+ "</form>");
					resp.getWriter().println("<form action=\"/operate\" method=\"post\">"
							+ "<h5>RecCrop Width: </h5>"+"<input type=\"text\" name=\"width\">"
							+ "<h5>RecCrop Height: </h5>"+"<input type=\"text\" name=\"height\">"
							+"<input type=\"hidden\" name=\"method\" value=\"5\">"
							+"<input type=\"hidden\" name=\"img-key\" value="
							+ "\""
							+ KeyFactory.keyToString(photoKey)
							+ "\">"
							+"<h4><input type=\"submit\" value=\"Crop\"></h4>"
							+ "</form>");
					resp.getWriter().println("</div>");
					

					resp.getWriter().println("<div id=\"nav2\">");
					if(commentResults.isEmpty()) resp.getWriter().println("<h1>No comments!</h1>");
					else
					{
						for(Entity comment: commentResults)
						{
							resp.getWriter().println("<h2>"
						+comment.getProperty("content")
						+"</h2>");
							resp.getWriter().println("<h5>by "
									+comment.getProperty("author")
									+"  "
									+comment.getProperty("date")
									+"</h5>");
						}
					}
					
					resp.getWriter().println("<p>Enter your comment below:</p>"
							+ "<form action=\"/enqueue\" method=\"post\">"
							+ "<input type=\"text\" name=\"content\">"
							+"<input type=\"hidden\" name=\"img-key\" value="
							+"\"" 
							+KeyFactory.keyToString(photoKey)
							+"\"" 
							+ ">"
							+"<input type=\"submit\" value=\"Submit\">"
							+ "</form>");
					
					if(!tagResults.isEmpty())
					{
						for(Entity tag: tagResults)
						{
							resp.getWriter().println("<h3>"
									+"#"
						+tag.getProperty("content")
						+"</h3>");
						}
					}
					
					resp.getWriter().println("<p>Tag this photo?</p>"
							+ "<form action=\"/enqueue\" method=\"post\">"
							+ "<input type=\"text\" name=\"tag\">"
							+"<input type=\"hidden\" name=\"img-key\" value="
							+"\"" 
							+KeyFactory.keyToString(photoKey)
							+"\"" 
							+ ">"
							+"<input type=\"submit\" value=\"Submit\">"
							+ "</form>");				
					resp.getWriter().println("</div>");
		
					
					
					
					resp.getWriter().println("<div id=\"section\">"
							+ "<img src="
							+PhotoURL
							+" "
							+"style=\"width:"
							+ "640px;height:"
							+ "360px;\">"
//							+"<h1>sfgestsenhts</h1>"
						+"</div>");
					
					resp.getWriter().println("<div id=\"footer\">");
					resp.getWriter().println("<a href=\"/user\"><h1>Back to gallery</h1></a>");
					resp.getWriter().println("</div>");
					
				}
			
			resp.getWriter().println("</body></html>");
			
		}
		
	}
}
