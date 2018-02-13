package edu.pennstate.science_olympiad.interceptors;

import edu.pennstate.science_olympiad.controllers.EventController;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RequestInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        super.preHandle(request, response, handler);

        if((((HandlerMethod) handler).getBean() instanceof EventController)) {
            HttpSession session = request.getSession(false);

            if(session == null || session.getAttribute("session") == null) {
                System.out.println("SESSION IS BAD OMG");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }

        System.out.println("MADE IT THROUGH RequestInterceptor - YO SESSION IS GOOD HOMIE");
        return true;
    }
}
