package edu.pennstate.science_olympiad.interceptors;

import com.google.gson.JsonObject;
import edu.pennstate.science_olympiad.controllers.EventController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RequestInterceptor extends HandlerInterceptorAdapter {
    Log log = LogFactory.getLog(getClass());

    private static final String AC_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String AC_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //RETURN if options request
        if(request.getMethod().equalsIgnoreCase("OPTIONS")) return true;

        HttpSession session = request.getSession(false);
        log.info(request.getHeader("SmsSid"));
        if(session == null || session.getAttribute("user") == null) {
            System.out.println("Request Interceptor says - YO SESSION IS BAD HOMIE GET OUTTA HERE");

            //Build valid response that CORS can handle before returning
            String referer = request.getHeader("referer");
            if(referer.charAt(referer.length() - 1) == '/') {
                referer = referer.substring(0, referer.length() - 1);
            }

            response.setHeader(AC_ALLOW_ORIGIN, referer);
            response.setHeader(AC_ALLOW_CREDENTIALS, "true");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: The request has not been applied because it lacks valid authentication credentials.");
            return false;
        }

        System.out.println("----- Session Status -----");
        System.out.println("SESSION ID here: " + session.getId());
        System.out.println("RequestInterceptor says - YO SESSION IS GOOD; CARRY ON");
        System.out.println("---------------------------");

        return true;
    }
}
