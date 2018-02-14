package edu.pennstate.science_olympiad.interceptors;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsInterceptor extends HandlerInterceptorAdapter {

    private static final String ORIGIN = "Origin";
    private static final String AC_REQUEST_METHOD = "Access-Control-Request-Method";
    private static final String AC_REQUEST_HEADERS = "Access-Control-Request-Headers";

    private static final String AC_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String AC_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String AC_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String AC_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    private CorsData corsData;

    private String origin;
    private String allowMethods;
    private String allowHeaders;
    private String allowCredentials;

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setAllowMethods(String allowMethods) {
        this.allowMethods = allowMethods;
    }

    public void setAllowHeaders(String allowHeaders) {
        this.allowHeaders = allowHeaders;
    }

    public void setAllowCredentials(String allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        this.corsData = new CorsData(request);

        if(this.corsData.isPreflighted()) {
            response.setHeader(AC_ALLOW_ORIGIN, corsData.getOrigin());
            response.setHeader(AC_ALLOW_METHODS, allowMethods);
            response.setHeader(AC_ALLOW_HEADERS, allowHeaders);
            response.setHeader(AC_ALLOW_CREDENTIALS, allowCredentials);
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if(this.corsData.isSimple()) {
            response.setHeader(AC_ALLOW_ORIGIN, this.corsData.getOrigin());
        }
    }

    class CorsData {

        private String origin;
        private String requestMethods;
        private String requestHeaders;

        CorsData(HttpServletRequest request) {
            this.origin = request.getHeader(ORIGIN);
            this.requestMethods= request.getHeader(AC_REQUEST_METHOD);
            this.requestHeaders = request.getHeader(AC_REQUEST_HEADERS);
        }

        public boolean hasOrigin(){
            return origin != null && origin.length() > 0;
        }

        public boolean hasRequestMethods(){
            return requestMethods != null && requestMethods.length() > 0;
        }

        public boolean hasRequestHeaders(){
            return requestHeaders != null && requestHeaders.length() > 0;
        }

        public String getOrigin() {
            return origin;
        }

        public String getRequestMethods() {
            return requestMethods;
        }

        public String getRequestHeaders() {
            return requestHeaders;
        }

        public boolean isPreflighted() {
            return hasOrigin() && hasRequestHeaders() && hasRequestMethods();
        }

        public boolean isSimple() {
            return hasOrigin() && !hasRequestHeaders();
        }
    }
}