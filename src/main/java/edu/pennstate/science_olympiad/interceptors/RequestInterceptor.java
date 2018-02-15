package edu.pennstate.science_olympiad.interceptors;

import edu.pennstate.science_olympiad.controllers.EventController;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RequestInterceptor extends HandlerInterceptorAdapter {

    public static final String METHODS_NAME = "Access-Control-Allow-Methods";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("REQUEST INTERCEPTOR");

        //RETURN if options request
        if(request.getMethod().equalsIgnoreCase("OPTIONS")) return true;

        //if((((HandlerMethod) handler).getBean() instanceof EventController)) {
            HttpSession session = request.getSession(false);
            System.out.println("SESSION: " + session);

            if(session == null || session.getAttribute("user") == null) {
                System.out.println("Request Interceptor says - YO SESSION IS BAD OMFG");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            System.out.println("RequestInterceptor says - YO SESSION IS GOOD HOMIE; CARRY ON");
        //}

        return true;
    }
}
