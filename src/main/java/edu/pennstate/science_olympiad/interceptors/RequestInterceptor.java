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

        if(session == null || session.getAttribute("user") == null) {
            System.out.println("Request Interceptor says - YO SESSION IS BAD OMFG");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setHeader(ORIGIN, request.getHeader("referer"));

            //WRITE RESPONSE
//            response.getWriter().write("UNAUTHORIZED: A valid session is required.");
//            response.getWriter().flush();
//            response.getWriter().close();
            return false;
        }

        System.out.println("RequestInterceptor says - YO SESSION IS GOOD HOMIE; CARRY ON");

        return true;
    }
}
