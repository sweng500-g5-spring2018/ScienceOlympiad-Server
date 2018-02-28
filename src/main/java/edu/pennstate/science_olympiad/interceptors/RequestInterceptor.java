package edu.pennstate.science_olympiad.interceptors;

import com.google.gson.JsonObject;
import edu.pennstate.science_olympiad.controllers.EventController;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RequestInterceptor extends HandlerInterceptorAdapter {

    public static final String ORIGIN = "Access-Control-Allow-Origin";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //RETURN if options request
        if(request.getMethod().equalsIgnoreCase("OPTIONS")) return true;

        HttpSession session = request.getSession(false);
        System.out.println("CHECKING SESSION: " + session);

        if(session == null || session.getAttribute("user") == null) {


            System.out.println("Request Interceptor says - YO SESSION IS BAD OMFG");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return false;
        }

        System.out.println("----- Session Status -----");
        System.out.println("SESSION here: " + session.getId());
        System.out.println("RequestInterceptor says - YO SESSION IS GOOD; CARRY ON");
        System.out.println("---------------------------");

        return true;
    }
}
