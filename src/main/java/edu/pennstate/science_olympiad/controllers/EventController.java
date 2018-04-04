package edu.pennstate.science_olympiad.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.deploy.panel.JHighDPITable;
import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.URIConstants;
import edu.pennstate.science_olympiad.helpers.mongo.MongoIdVerifier;
import edu.pennstate.science_olympiad.helpers.request.NewJudgeHelper;
import edu.pennstate.science_olympiad.many_to_many.Team_Event;
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
            boolean added = eventService.addEvent(eventJson);

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("Event was created");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("An error occured while creating event");
        } catch(Exception e) {
            logger.info("exception " + e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }
/**
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
    */



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
        logger.info("got to delete event controller ");
        try {
            if(! MongoIdVerifier.isValidMongoId(eventId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid event ID.");            }

            boolean removed = eventService.removeEvent(eventId);

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

            boolean update = eventService.updateEvent(eventId, eventJson);

            if (update){
                return ResponseEntity.status(HttpStatus.OK).body("Event was updated.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Event could not be updated, doesn't exist.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");

        }
    }

    /**
     * Grabs a specific event to view
     * @param eventId the id of the event you want to update
     * @return the Event object c
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= GET_AN_EVENT, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAEvent(@PathVariable("eventId") String eventId) {
            Event event = eventRepository.getEvent(eventId);
            logger.info("Got the event " + event.getName());
            if (event != null) {
                return ResponseEntity.status(HttpStatus.OK).body(event);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Event could not be found");
            }

    }

    /**
     * Get this events judges, this is in the works and not working currently
     * @param eventId the id of the event you want to update
     * @return the Event object c
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= GET_EVENT_JUDGES, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getEventJudges(@PathVariable("eventId") String eventId) {
        List<Judge> judges = eventService.getEventJudges(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(judges);
    }

    /**
     * This is an endpoint that allows for a judge to add a score to a team_event object
     * @param score the score that a team earned, this will be a double although it is passed as a string
     * @param teamIdJson the teamId of the specified team, in json format
     * @param eventIdJson the eventId of the specified event, in json format
     * @return Whether the score was added or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_SCORE, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addScoreToTeamEvent(@PathVariable("score") String score,
                                                 @RequestBody String teamIdJson, @RequestBody String eventIdJson) {
        String teamId = JsonHelper.getIdFromJson(teamIdJson);
        String eventId = JsonHelper.getIdFromJson(eventIdJson);
        boolean added = eventRepository.addScoreToTeamEvent(Double.parseDouble(score), teamId, eventId);
        if (added)
            return ResponseEntity.status(HttpStatus.OK).body(added);
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body(added);
    }

    /**
     * This will grab all of the scores for all of the teams_event objects.
     *
     * This is useful for determining who the winner will be at the end.
     * @return the list of all of the team_event objects in the database
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= GET_SCORES, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAllTeamEventScores() {
        List<Team_Event> team_events = eventRepository.getAllScores();
        if (team_events != null)
            return ResponseEntity.status(HttpStatus.OK).body(team_events);
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Unable to query for team_events");
    }

    /**
     * This will grab all of the team_event objects for a specified team.
     *
     * This is useful for if we want to show a team their scores thus far in the competition
     * @param teamId the ID if the team that we would like the team_event objects for
     * @return team_event objects for a specified team
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= GET_SCORES_FOR_TEAM, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getTeamScores(@PathVariable("teamId") String teamId) {
        List<Team_Event> team_events = eventRepository.getAllScoresForTeam(teamId);
        if (team_events != null)
            return ResponseEntity.status(HttpStatus.OK).body(team_events);
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Unable to query for team_events");
    }

    /**
     * This will grab all of the team_event objects for a specified event.
     *
     * This is useful for the sheet that will allow the judge to see all of the events for the event that they are currently judging
     * @param eventId the ID if the event that we would like the team_event objects for
     * @return team_event objects for a specified event
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= GET_SCORES_FOR_EVENT, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getEventScores(@PathVariable("eventId") String eventId) {
        List<Team_Event> team_events = eventRepository.getAllScoresForEvent(eventId);
        if (team_events != null)
            return ResponseEntity.status(HttpStatus.OK).body(team_events);
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Unable to query for team_events");
    }

}
