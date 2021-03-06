package cs263w16;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.appidentity.AppIdentityServicePb.SigningService.Method;
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
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.images.Transform;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class ImageOperateServlet extends HttpServlet{
	private UserService userService=UserServiceFactory.getUserService();
	private DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
	private MemcacheService syncCache=MemcacheServiceFactory.getMemcacheService();
	private ImagesService imagesService=ImagesServiceFactory.getImagesService();
	private BlobstoreService blobstoreService=BlobstoreServiceFactory.getBlobstoreService();
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(req, resp);
		
		if(userService.getCurrentUser()==null) 
		{
			resp.sendRedirect("/login");
			return;
		}
		
		String method=req.getParameter("method");
		String imgKeyName=req.getParameter("img-key");
		
		if(method==null || imgKeyName==null) 
		{
			resp.getWriter().println("Not a valid image operation URL!");
			return;
		}
		else
		{
			byte[] newImageData = null;
			
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
	        
			BlobKey blobKey=(BlobKey) ent.getProperty("blobKey");
			
			ServingUrlOptions options=ServingUrlOptions.Builder.withBlobKey(blobKey);
			String originalImageURL=imagesService.getServingUrl(options);
			String imageURL=imagesService.getServingUrl(options);
			imageURL+="=s640";
						
	        if(method.equals("1")) imageURL+="-fv";
	        else if(method.equals("2")) imageURL+="-fh"; 
	        else if(method.equals("3")) imageURL+="-r90";
	        else if(method.equals("4")) imageURL+="-r270";
	        else if(method.equals("5")) imageURL=originalImageURL+"=s200-p";
	        else if(method.equals("6")) imageURL=originalImageURL+"=s200-cc";				
	        else if(method.equals("7")) imageURL=originalImageURL+"=s200-cc-fSoften=1,5,0";				
	        else if(method.equals("8")) imageURL=originalImageURL+"=s";				
	        else {
	        	resp.getWriter().println("Wrong method input!");
	        	return;
			}   	
	        
	        resp.getWriter().printf("<img src="+imageURL+">");
	        	   
		}
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doPost(req, resp);
		if(userService.getCurrentUser()==null) 
		{
			resp.sendRedirect("/login");
			return;
		}
		
		String method=req.getParameter("method");
		String imgKeyName=req.getParameter("img-key");
		String width=req.getParameter("width");
		String height=req.getParameter("height");
		String downsize=req.getParameter("downsize");
		String upsize=req.getParameter("upsize");
		String squareCrop=req.getParameter("squareCrop");
		
		int widthNum,heightNum,downsizeNum,upsizeNum,squareCropNum;
	
		if(method==null || imgKeyName==null) 
		{
			resp.getWriter().println("Not a valid image operation URL!");
			return;
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
	        
BlobKey blobKey=(BlobKey) ent.getProperty("blobKey");
			
			ServingUrlOptions options=ServingUrlOptions.Builder.withBlobKey(blobKey);
			String originalImageURL=imagesService.getServingUrl(options);
			String imageURL=imagesService.getServingUrl(options);
						
	        if(method.equals("1")) 
	        	{
	        	
	        	widthNum=Integer.parseInt(width);
	    		heightNum = Integer.parseInt(height);
	    		
	        	imageURL+="=w"+widthNum+"-h"+heightNum;
	        	}
	        else if(method.equals("2")) 
	        	{
	        	downsizeNum = Integer.parseInt(downsize);
	    		
	        	imageURL+="=w"+640/downsizeNum+"-h"+360/downsizeNum; 
	        	}
	        else if(method.equals("3")) 
	        	{
	        	
	        	upsizeNum = Integer.parseInt(upsize);
	    		
	        	imageURL+="=w"+640*upsizeNum+"-h"+360*upsizeNum+"-nu";
	        	}
	        else if(method.equals("4"))
	        	{
	        	squareCropNum=Integer.parseInt(squareCrop);
	    		
	        	
	        	imageURL+="=s"+squareCropNum+"-ci";
	        	}
	        else if(method.equals("5")) 
	        	{
	        	widthNum=Integer.parseInt(width);
	    		heightNum = Integer.parseInt(height);
	    		
	        	imageURL+="=w"+widthNum+"-h"+heightNum+"-c";
	        	}
	        else {
	        	resp.getWriter().println("Wrong method input!");
	        	return;
			}   	
	        
	        resp.getWriter().printf("<img src="+imageURL+">");
	        	   
		}		
	}

}
