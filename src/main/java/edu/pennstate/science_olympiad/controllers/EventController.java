package edu.pennstate.science_olympiad.controllers;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.URIConstants;
import edu.pennstate.science_olympiad.helpers.mongo.MongoIdVerifier;
import edu.pennstate.science_olympiad.people.Judge;
import edu.pennstate.science_olympiad.repositories.EventRepository;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import edu.pennstate.science_olympiad.helpers.json.JsonHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.pennstate.science_olympiad.services.EventService;

import java.util.List;

/**
 * This contains all of the endpoints on the web server for managing events
 */
@RestController
public class EventController implements URIConstants{
    @Autowired
    private EventService eventService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    Log logger = LogFactory.getLog(getClass());

    /**
     * Return the events to display in the events view
     * @return the list of events
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= GET_EVENTS, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public Object getEvents() {

        List<Event> events =  eventService.getEvents();
        logger.info("found these events " + events.toString());
        return events;
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value= NEW_EVENT, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addNewEvent(@RequestBody String eventJson) {
        try {
            Gson gson = new Gson();
            Event event = gson.fromJson(eventJson, Event.class);

            boolean added = eventService.createNewEvent(event);

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("User was added.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value= ASSIGN_JUDGE_TO_EVENT, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> assignJudgeToEvent(@RequestBody String judgeIdJson, @RequestBody String eventIdJson) {
        try {
            Event event = eventRepository.getEvent(JsonHelper.getIdFromJson(eventIdJson));
            Judge judge = (Judge) userRepository.getUser(JsonHelper.getIdFromJson(judgeIdJson));

            boolean added = eventRepository.assignJudgeToEvent(judge, event);

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("User was added.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }



    /**
     * Before creating an event, check to see if it already exists
     * @param eventName - an event to search the db
     * @return
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= VERIFY_EVENT, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> verifyEventUnique(@PathVariable("eventName") String eventName) {
        try {


            boolean unique= eventRepository.verifyEventUnique(eventName);

            if (unique)
                return ResponseEntity.status(HttpStatus.OK).body("Verified");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Event already exists");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }
    /**
     * Removes a specific event from the database
     * @param eventId the id of the event you want to remove
     * @return the response of the event being deleted or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= REMOVE_EVENT, method= RequestMethod.DELETE ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> removeEvent(@PathVariable("eventId") String eventId) {
        try {
            if(! MongoIdVerifier.isValidMongoId(eventId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid event ID.");            }

            boolean removed = eventRepository.removeEvent(eventId);

            if (removed){
                return ResponseEntity.status(HttpStatus.OK).body("Event was removed.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Event could not be removed, doesn't exist.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }

    /**
     * Updates a specific event in the database
     * @param eventId the id of the event you want to update
     * @param eventJson the json of the info you want to update the event with
     * @return the response of the event being updated or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= UPDATE_EVENT, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") String eventId, @RequestBody String eventJson) {
        try {
            if(! MongoIdVerifier.isValidMongoId(eventId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid event ID.");            }

            boolean update = eventRepository.updateEvent(eventId, eventJson);

            if (update){
                return ResponseEntity.status(HttpStatus.OK).body("Event was updated.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Event could not be updated, doesn't exist.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");

        }
    }

}
