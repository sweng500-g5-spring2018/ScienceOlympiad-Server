package edu.pennstate.science_olympiad.controllers;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import edu.pennstate.science_olympiad.User;

/**
 * Set up all the end points, RestController automatically add
 * a ResponseBody annotation in the return of each method.
 *
 * Pretty sure in order for the object to convert to json
 * there needs to be an api included - see the faster-core in pom.xml
 */
@RestController
public class FirstController {

    @CrossOrigin(origins = "*")
    @RequestMapping(value="users",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public User getUser() {
        return new User();
    }
}