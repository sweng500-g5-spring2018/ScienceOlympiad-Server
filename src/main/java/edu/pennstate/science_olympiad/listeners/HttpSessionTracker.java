package edu.pennstate.science_olympiad.listeners;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;
import java.util.Map;

@WebListener
public class HttpSessionTracker implements HttpSessionListener {

    private static int activeSessions = 0;
    private static final Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();

    /* 60 seconds * [number of minutes] */
    private static final int timeout = 60 * 20;

    /**
     * Listen for session events being created and track them in a Map.
     *
     * @param event the HttpSessionEvent that has triggered the call
     */
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        session.setMaxInactiveInterval(timeout);

        sessions.put(session.getId(), session);
        activeSessions++;

        System.out.println("(Active " + activeSessions + ") SESSION CREATED: " + session.getId());
    }

    /**
     * Listen for session events being destroyed and remove them from Map.
     *
     * @param event the HttpSessionEvent that has triggered the call
     */
    public void sessionDestroyed(HttpSessionEvent event) {

        System.out.println("(Active " + activeSessions + ") SESSION DESTROYED: " + event.getSession().getId());

        sessions.remove(event.getSession().getId());
        activeSessions--;

        //event.getSession().invalidate(); CALL THIS and this actually calls sessionDestroyed (call on logout)
    }

}
