package edu.pennstate.science_olympiad;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * The xml version was giving me problems for some reason so i just did the java way
 * The annotation's are what is doing everything here
 */
@EnableWebMvc
@ComponentScan(basePackages = "edu.pennstate.science_olympiad.controllers")
public class SpringConfig extends WebMvcConfigurerAdapter{
    Log log = LogFactory.getLog(getClass());
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        log.info("Setting servlet handling");
        configurer.enable();
    }
}
