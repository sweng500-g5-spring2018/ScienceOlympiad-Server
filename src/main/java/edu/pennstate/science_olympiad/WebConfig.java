package edu.pennstate.science_olympiad;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;


import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * Takes the place of the typical web.xml used in web applications.
 * Either web.xml or a class like this is needed to run applications on TOMCAT.
 */
public class WebConfig implements WebApplicationInitializer {

    /**
     * Overriden
     * @param container
     */
    public void onStartup(ServletContext container) {
        AnnotationConfigWebApplicationContext context
                = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation("edu.pennstate.science_olympiad.SpringConfig");

        //container.addListener(new ContextLoaderListener(context));

        ServletRegistration.Dynamic dispatcher =
                container.addServlet("sweng500", new DispatcherServlet(context));

        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}