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
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String content = request.getParameter("content");
        String tag = request.getParameter("tag");
        String imgKey = request.getParameter("img-key");

        response.getWriter().println(content);
        response.getWriter().println(imgKey);
        
        // Add the task to the default queue.
        if(content!=null){
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/worker")
		.param("content", content)
		.param("img-key", imgKey));
        }
        
        if(content!=null) response.sendRedirect("/comment?content="+content+"&"+"img-key="+imgKey);
        else if(tag!=null) response.sendRedirect("/tag?tag="+tag+"&"+"img-key="+imgKey);
        //response.sendRedirect("/image?img-key="+imgKey);
    }
}