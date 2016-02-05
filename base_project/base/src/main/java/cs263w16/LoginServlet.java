package cs263w16;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	
    	try {
    	String twitter_userid   = (String)req.getParameter("twitter_userid");
    	String twitter_password = (String)req.getParameter("twitter_password");
    	
    	Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(twitter_userid,twitter_password);
        
        RequestToken requestToken;
		
		requestToken = twitter.getOAuthRequestToken();
		
        String token = requestToken.getToken();
        String tokenSecret = requestToken.getTokenSecret();
         
        HttpSession session = req.getSession();
        session.setAttribute("token", token);
        session.setAttribute("tokenSecret", tokenSecret);
         
        String authUrl = requestToken.getAuthorizationURL();
         
        req.setAttribute("authUrl", authUrl);
        RequestDispatcher rd = req.getRequestDispatcher("Login.jsp");
        rd.forward(req, res);
    	} 
    	catch (TwitterException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
       
}
