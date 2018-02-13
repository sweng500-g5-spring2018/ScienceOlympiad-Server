package edu.pennstate.science_olympiad.controllers;

import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.people.AUser;
import edu.pennstate.science_olympiad.people.Admin;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.UserFactory;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import edu.pennstate.science_olympiad.services.EventService;
import edu.pennstate.science_olympiad.sms.CustomPhoneNumber;
import edu.pennstate.science_olympiad.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    @RequestMapping(value="newTeamEvent",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public Object createTeamEvent() {

        //simulate mapping inserting an event for later
        Event event = new Event("FirstEvent");
        eventService.createNewEvent(event);

        //add a team to the fake event created above
        Object o = eventService.addTeamToEvent(new ArrayList<String>(),"FirstEvent");

        return o;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value="testSessionStart",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> testSessionStart(HttpServletRequest request) {
        HttpSession userSession = request.getSession(false);

        if(userSession == null) {
            userSession = request.getSession();
        }

        System.out.println("TEST SESSION START");
        userSession.setAttribute("sessionTest", "OH HELLO");
        return ResponseEntity.status(HttpStatus.OK).body("SESSION CREATED SUCCESSFUL");
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value="testSessionEnd",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> testSessionEnd(HttpServletRequest request) {
        HttpSession userSession = request.getSession(false);
        if(userSession == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("SESSION DOES NOT SEEM TO EXIST.");
        }

        userSession.invalidate();
        System.out.println("TEST SESSION END");
        return ResponseEntity.status(HttpStatus.OK).body("YAY SESSION KILLED SUCCESSFULLY");
    }
}