package edu.pennstate.science_olympiad.controllers;
import edu.pennstate.science_olympiad.people.AUser;
import edu.pennstate.science_olympiad.people.Admin;
import edu.pennstate.science_olympiad.people.UserFactory;
import edu.pennstate.science_olympiad.repository.UserRepository;
import edu.pennstate.science_olympiad.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;

/**
 * Set up all the end points, RestController automatically add
 * a ResponseBody annotation in the return of each method.
 *
 * Pretty sure in order for the object to convert to json
 * there needs to be an api included - see the faster-core in pom.xml
 */
@RestController
public class FirstController {
    //Log log = LogFactory.getLog(getClass());
    @Autowired
    UserRepository userRepository;

    public UserRepository getUserRepository(){
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @CrossOrigin(origins = "*")
    @RequestMapping(value="createTestUser",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public Object addUser() {
       // log.info(">>addUser()");
        AUser user = UserFactory.getInstance().createUser("admin");
        user.setFirstName("Kyle");
        user.setLastName("h");
        user.setEmailAddress("test@email");
        userRepository.addUser(user);

        AUser coach = UserFactory.getInstance().createUser("coach");
        coach.setFirstName("coach1");
        coach.setLastName("co");
        userRepository.addUser(coach);
       // log.info("<<addUser()");
        Pair response = new Pair("success","true");
        return response;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value="users",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public AUser getUser() {
        Admin admin = (Admin)UserFactory.getInstance().createUser("admin");
        admin.setFirstName("Brandon");
        admin.setLastName("Hessler");
        admin.setEmailAddress("PennState@brandonhessler.com");
        return admin;
    }
}