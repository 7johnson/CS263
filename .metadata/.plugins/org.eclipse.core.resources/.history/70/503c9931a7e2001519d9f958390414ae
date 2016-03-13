
package cs263w16;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class TodoTest {

  public static void main(String[] args) {
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);

    WebTarget target = client.target(getBaseURI());
    // Get XML
    String xmlResponse = target.path("rest").path("todo").request()
        .accept(MediaType.TEXT_XML).get(String.class);
    // Get XML for application
    String xmlAppResponse =target.path("rest").path("todo").request()
        .accept(MediaType.APPLICATION_XML).get(String.class);

    // For JSON response also add the Jackson libraries to your webapplication
        // In this case you would also change the client registration to
        // ClientConfig config = new ClientConfig().register(JacksonFeature.class);
        // Get JSON for application
        // System.out.println(target.path("rest").path("todo").request()
        // .accept(MediaType.APPLICATION_JSON).get(String.class));
    
    System.out.println(xmlResponse);
    System.out.println(xmlAppResponse);
  }

  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://localhost:8080/_com.vogella.jersey.jaxb").build();
  }

} 







