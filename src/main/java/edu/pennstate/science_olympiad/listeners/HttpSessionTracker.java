package edu.pennstate.science_olympiad.listeners;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class HttpSessionTracker implements HttpSessionListener {

    private static int activeSessions;

    //add map

    public void sessionCreated(HttpSessionEvent event) {
        // expire session
        activeSessions++;

        System.out.println("SESSION CREATED ... total: " + activeSessions);
        event.getSession().setMaxInactiveInterval(60 * 5);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        activeSessions--;
        System.out.println("SESSION DESTROYED ... total: " + activeSessions);
        //event.getSession().invalidate(); CALL THIS and this actually calls sessionDestroyed (call on logout)
    }

}
