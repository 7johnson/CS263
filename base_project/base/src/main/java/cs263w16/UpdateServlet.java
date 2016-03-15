package cs263w16;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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
public class UpdateServlet extends HttpServlet {

	private UserService userService=UserServiceFactory.getUserService();
	private DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
	private MemcacheService syncCache=MemcacheServiceFactory.getMemcacheService();
	private BlobstoreService blobstoreService=BlobstoreServiceFactory.getBlobstoreService();
	private ImagesService imagesService=ImagesServiceFactory.getImagesService();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) 
    		throws ServletException, IOException {
    	if(userService.getCurrentUser()==null)
		{
			resp.sendRedirect("/login");
		}
    	resp.getWriter().write("SUCCESS");
    	
    }
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

    	syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
    	
    	// Handle post requests
    	if(userService.getCurrentUser()==null)
		{
			resp.sendRedirect("/login");
		}
		else
		{
			resp.sendRedirect("/user");
			
			String qcmd = req.getParameter("qcmd");        
	        if ("success".equals(qcmd)) {
	            resp.getWriter().write("SUCCESS");
	            return;
	        }
	        
			User curUser=userService.getCurrentUser();	
			Key userKey=KeyFactory.createKey("User", curUser.getUserId());
			
			Entity user=new Entity("User",curUser.getUserId());
			datastore.put(user);
			syncCache.put(curUser.getUserId(), user);
			
			
	        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
	        List<BlobKey> blobKeys = blobs.get("myFile");
	           
	        String id=req.getParameter("id");
	        if(id!=null)
	        {
	        	BlobKey oldBlobKey=new BlobKey(id);
	        	blobstoreService.delete(oldBlobKey);
	        }
	     
	        
	        if (blobKeys == null || blobKeys.isEmpty()) {
	            resp.sendRedirect("/");
	        }
	        else
	        {
	            String photoURL="/serve?blob-key=" + blobKeys.get(0).getKeyString();
	        	
	            Entity photo = new Entity("Photo", userKey);
	        	
	            photo.setProperty("imageURL",photoURL);
	        	photo.setProperty("Date", new Date());
	            
	        	Image myImage=ImagesServiceFactory.makeImageFromBlob(blobKeys.get(0));
	        	photo.setProperty("imgWidth", myImage.getWidth());
	            photo.setProperty("imgHeight", myImage.getHeight());          
	            
	            photo.setProperty("blobKey", blobKeys.get(0).toString());
	            
	        	datastore.put(photo);   
	        	
	            resp.sendRedirect(photoURL);
	    	    
	        }
		}
    	
    }
       
}
