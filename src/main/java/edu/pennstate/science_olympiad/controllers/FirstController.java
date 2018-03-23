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
import edu.pennstate.science_olympiad.sms.TextMessage;
import edu.pennstate.science_olympiad.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Set up all the end points, RestController automatically add
 * a ResponseBody annotation in the return of each method.
 *
 * Pretty sure in order for the object to convert to json
 * there needs to be an api included - see the faster-core in pom.xml
 */
@RestController
public class FirstController {
    Log log = LogFactory.getLog(getClass());
    @Autowired
    MongoTemplate mongoTemplate;
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

//    @CrossOrigin(origins = "*")
//    @RequestMapping(value="newTeamEvent",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
//    public Object createTeamEvent() {
//
//        //simulate mapping inserting an event for later
//        Event event = new Event("FirstEvent");
//        eventService.createNewEvent(event);
//
//        //add a team to the fake event created above
//        Object o = eventService.addTeamToEvent(new ArrayList<String>(),"FirstEvent");
//
//        return o;
//    }

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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("SESSION DOES NOT EXIST.");
        }

        userSession.invalidate();
        System.out.println("TEST SESSION END");
        return ResponseEntity.status(HttpStatus.OK).body("YAY SESSION KILLED SUCCESSFULLY");
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value="testCoachOnly", method= RequestMethod.GET, produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> testCoachOnly(HttpServletRequest request) {
        HttpSession userSession = request.getSession(false);
        if(userSession == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session exists.");
        }

        AUser user = (AUser) userSession.getAttribute("user");
        if(user != null && user instanceof Coach) {
            return ResponseEntity.status(HttpStatus.OK).body("YAY, you're a coach.  You may access this content.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are NOT a coach.  You CANNOT access this content.");
        }
    }

//    @CrossOrigin(origins = "*")
//    @RequestMapping(value="returnSms", method= RequestMethod.POST, produces={MediaType.APPLICATION_JSON_VALUE})
//    public String returnSms() {
//        TextMessage.getInstance().text("+18056162550", "Text Received");
//        return "We received your message and here is your response";
//    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value="returnSms", method= RequestMethod.POST, produces={MediaType.APPLICATION_JSON_VALUE})
    public void returnSms(HttpServletRequest request, HttpServletResponse response) {
        log.info("Got to the return sms endpoint");

        TextMessage.getInstance().text("+19086165430", "Text Received, with parameters");
    }
}