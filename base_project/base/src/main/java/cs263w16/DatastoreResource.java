package cs263w16;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.*;
import javax.ws.rs.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

//Map this class to /ds route
@Path("/ds")
public class DatastoreResource {
  // Allows to insert contextual objects into the class,
  // e.g. ServletContext, Request, Response, UriInfo
  @Context
  UriInfo uriInfo;
  @Context
  Request request;

  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();

  
  // Return the list of entities to the user in the browser
  @GET
  @Produces(MediaType.TEXT_XML)
  public List<TaskData> getEntitiesBrowser() {
    //datastore dump -- only do this if there are a small # of entities
    List<TaskData> list = new ArrayList<TaskData>();
    Query taskDataQuery = new Query("TaskData");
    List<Entity> results = datastore.prepare(taskDataQuery).asList(FetchOptions.Builder.withDefaults());
    for(Entity ent:results){
            list.add(new TaskData(ent.getKey().getName(),
			(String)(ent.getProperty("value")),
			(Date)ent.getProperty("date")));
    }
    return list;
  }

  // Return the list of entities to applications
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public List<TaskData> getEntities() {
    //datastore dump -- only do this if there are a small # of entities
    //same code as above method
      return getEntitiesBrowser();
  }

  //Add a new entity to the datastore
  @POST
  @Produces(MediaType.TEXT_HTML)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public void newTaskData(@FormParam("keyname") String keyname,
      @FormParam("value") String value,
      @Context HttpServletResponse servletResponse) throws IOException {
    Date date = new Date();
    Entity ent = new Entity("TaskData",keyname);
    ent.setProperty("value", value);
    ent.setProperty("date",date);
    
    datastore.put(ent);
    syncCache.put(keyname, ent);
    
    System.out.println("Posting new TaskData: " +keyname+" val: "+value+" ts: "+ date );
  }

  //The @PathParam annotation says that keyname can be inserted as parameter after this class's route /ds
  @Path("{keyname}")
  public TaskDataResource getEntity(@PathParam("keyname") String keyname) {
    System.out.println("GETting TaskData for " +keyname);
    return new TaskDataResource(uriInfo, request, keyname);
  }
}