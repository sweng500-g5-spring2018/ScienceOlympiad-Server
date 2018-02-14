package edu.pennstate.science_olympiad.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import edu.pennstate.science_olympiad.interceptors.CorsInterceptor;
import edu.pennstate.science_olympiad.interceptors.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;
import sun.misc.Request;

import java.util.Arrays;

/**
 * The xml version was giving me problems for some reason so i just did the java way
 * The annotation's are what is doing everything here
 */
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"edu.pennstate.science_olympiad.services","edu.pennstate.science_olympiad.repositories",
        "edu.pennstate.science_olympiad.controllers",
        "edu.pennstate.science_olympiad.listeners"})
@EnableMongoRepositories(basePackages = "edu.pennstate.science_olympiad.repositories")
public class SpringConfig extends WebMvcConfigurerAdapter {
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }


    protected String getDatabaseName() {
        return "science_olympiad";
    }

    /**
     * MongoClient works by having a connection pool
     *
     * TODO find out how to handle exceptions
     * @return
     * @throws Exception
     */
    public @Bean Mongo mongo() throws Exception {
        MongoCredential credential = MongoCredential.createCredential("api_server",
                "science_olympiad","3rOjF9Xo2cT1ole".toCharArray());
        ServerAddress serverAddr = new ServerAddress("server.sweng500.com",27017);

        return new MongoClient(serverAddr, Arrays.asList(credential));
    }

    /**
     * MongoTemplate is what is used for all of our database operations
     * and can be autowired anywhere due to the @Bean
     * @return
     * @throws Exception
     */
    public @Bean
    MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), getDatabaseName());
    }

    @Bean
    public RequestInterceptor localInterceptor() {
        return new RequestInterceptor();
    }

//    @Bean
//    public CorsInterceptor corsInterceptor() {
//        CorsInterceptor cors = new CorsInterceptor();
//
//        cors.setAllowMethods("GET, OPTIONS, POST, PUT, DELETE");
//        cors.setAllowHeaders("Origin, X-Requested-With, Content-Type, Accept");
//        cors.setOrigin("localHost:8080, sweng500.com");
//
//        return cors;
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(corsInterceptor());
        registry.addInterceptor(localInterceptor());
        //registry.addInterceptor(new ThemeChangeInterceptor()).addPathPatterns("/**").excludePathPatterns("/login", "/emailAvailable");
    }
}
