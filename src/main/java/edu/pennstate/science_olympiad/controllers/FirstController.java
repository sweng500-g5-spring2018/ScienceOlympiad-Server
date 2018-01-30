package edu.pennstate.science_olympiad.controllers;

import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.people.AUser;
import edu.pennstate.science_olympiad.people.Admin;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import edu.pennstate.science_olympiad.services.EventService;
import edu.pennstate.science_olympiad.sms.CustomPhoneNumber;
import edu.pennstate.science_olympiad.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

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
    @Autowired
    EventService eventService;
    public UserRepository getUserRepository(){
        return userRepository;
    }
    public EventService getEventService() {return eventService;}
    public void setUserRepository(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @CrossOrigin(origins = "*")
    @RequestMapping(value="createTestUser",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public Object addUser() {
       // log.info(">>addUser()");
        Admin admin = new Admin();
        admin.setFirstName("Kyle");
        admin.setLastName("H");
        admin.setEmailAddress("test@email");
        admin.setPhoneNumber(new CustomPhoneNumber("+12345678910"));
        userRepository.addUser(admin);

        Coach coach = admin.createCoach();
        coach.setFirstName("Coach");
        coach.setLastName("Nixon");
        userRepository.addUser(coach);
       // log.info("<<addUser()");
        Pair response = new Pair("success","true");
        return response;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value="users",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public AUser getUser() {
        Admin admin = new Admin();
        admin.setFirstName("Brandon");
        admin.setLastName("Hessler");
        admin.setEmailAddress("PennState@brandonhessler.com");
        return admin;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value="newTeamEvent",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public Object createTeamEvent() {

        //simulate mapping inserting an event for later
        Event event = new Event("FirstEvent");
        eventService.createNewEvent(event);

        //add a team to the fake event created above
        Object o = eventService.addTeamToEvent(new ArrayList<String>(),"FirstEvent");

        return o;
    }
}