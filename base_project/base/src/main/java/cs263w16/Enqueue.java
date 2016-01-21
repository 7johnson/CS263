// The Enqueue servlet should be mapped to the "/enqueue" URL.
package cs263w16;
import java.io.IOException;
import java.util.*;
import java.util.logging.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;


public class Enqueue extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyName = request.getParameter("keyname");
		String value = request.getParameter("value");


        // Add the task to the default queue.
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/rest/ds")
		.param("keyname", keyName)
		.param("value", value)
		.method(Method.POST));

        //response.sendRedirect("/done.html");
    }
}