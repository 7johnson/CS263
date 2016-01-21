// The Worker servlet should be mapped to the "/worker" URL.
package cs263w16;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.Query.Filter;

import com.google.appengine.api.memcache.*;

public class Worker extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String key = request.getParameter("keyname");
        // Do something with key.
    }
}