package edu.pennstate.science_olympiad.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import edu.pennstate.science_olympiad.Building;
import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.helpers.json.JsonHelper;
import edu.pennstate.science_olympiad.helpers.request.NewJudgeHelper;
import edu.pennstate.science_olympiad.many_to_many.Judge_Event;
import edu.pennstate.science_olympiad.many_to_many.Team_Event;
import edu.pennstate.science_olympiad.people.*;
import edu.pennstate.science_olympiad.repositories.BuildingRepository;
import edu.pennstate.science_olympiad.repositories.EventRepository;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import edu.pennstate.science_olympiad.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean to control the execution flow of certain business actions related to events
 */
@Service
public class EventService {
    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BuildingRepository buildingRepository;

    Log logger = LogFactory.getLog(getClass());

    public EventRepository getEventRepository() {
        return eventRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public BuildingRepository getBuildingRepository(){
        return buildingRepository;
    }

    /**
     * @param studentIds - The students selected to add to the team - just testing
     * @param eventName  -The event we are adding a team for, used to lookup event
     * @return Success no matter what
     */
    public Object addTeamToEvent(List<String> studentIds, String eventName) {
        //The students and coaches objects should have already been entered into the database so mock them here

        //The coach would be grabbed from the database based on a session of whos logged in
        Coach coach = new Coach();
        eventRepository.saveTestCoach(coach);
        //make db request to get the students with these id's entered in form
        Student student = new Student();
        student.setFirstName("Kyle");
        student.setLastName("Hughes");
        eventRepository.saveTestStudent(student);
        //Create the team based on the gathered coach and the students found from the ids
        Team team = new Team(coach);
        eventRepository.createTeam(team);

        //based on the event name, get the Event already saved in the database ("FirstEvent")
        Event event = eventRepository.getEvent(eventName);

        //now finally create a new team event object
        Team_Event teamEvent = new Team_Event(team, event);

        eventRepository.createTeamEvent(teamEvent);
        return new Pair<String, String>("Success", "added a new team to the event");
    }

    /**
     * This will have happened before any teams are added -- just adding one to test
     *
     * @param event nobody knows
     * @return whether the event was created or not
     */
    public boolean createNewEvent(Event event) {
        //this will also create a new Team_Event Object in the DB
        return eventRepository.createNewEvent(event);
    }

    public List<Event> getEvents() {
        return eventRepository.getEvents();
    }

    /**
     * Add an event created from the events page
     * Get the building id and existing judges from the db
     * Create any new judges added in the form
     *
     * @param eventJson The json passed into the request containing event,existing judge ids, new judge info
     * @return true if event is added
     * @throws Exception back to be handled by controller JsonErrors
     */
    public boolean addEvent(String eventJson) throws Exception {
        //store all existing judge ids and new judge ids for mapping
        List<String> addedJudges = new ArrayList<String>();
        //logger.info("got into add event -- " + eventJson);

        Gson gson = new Gson();
        String tempevent = JsonHelper.getJsonObject(eventJson, "eventJson");

        //this returns the building element
        String buildingString = JsonHelper.getJsonPrimitive(tempevent,"building");
        String actualEvent = JsonHelper.removeAndGetElement(eventJson,"building","eventJson");
        logger.info("got the building!!! " + buildingString);

        Building eventBuilding = buildingRepository.getBuilding(buildingString);
        Event event = gson.fromJson(actualEvent, Event.class);
        event.setLocation(eventBuilding);
        eventRepository.addEvent(event);
        // logger.info("created the event ok "+ event.getId());

        //get any existing judges selected in the json request
        JsonArray currentJudges = JsonHelper.getJsonList(eventJson, "existingJudgeValues");
        for (int i = 0; i < currentJudges.size(); i++) {
            logger.info("getting currentJudges" + currentJudges.get(i));
            //strip off the quotes for some reason the json comes with in the json list
            addedJudges.add(currentJudges.get(i).toString().replace("\"",""));
        }

        //create any new judges found
        JsonArray newJudges = JsonHelper.getJsonList(eventJson, "newJudgeValues");
        for (int i = 0; i < newJudges.size(); i++) {
            NewJudgeHelper judge = gson.fromJson(newJudges.get(i), NewJudgeHelper.class);
           // logger.info("got a new jduge !!! " + judge.getEmail());
            AUser newJudge = new Judge();
            ((Judge) newJudge).copyInfoFromJson(judge);
            newJudge.setPassword("default123");
            boolean added = userRepository.addUser(newJudge);
            //send email eventually
            if (added)
                addedJudges.add(newJudge.getId());
            else {
                logger.info("an error occured creating a new judge " + newJudge.getEmailAddress());
            }

        }
        //map all of the added/existing judges ids to the event id
        for (String aJudge : addedJudges) {
            Judge_Event je = new Judge_Event(event.getId(), aJudge);
            eventRepository.mapJudgeToEvent(je);
            logger.info("mapping this judge " + aJudge);
        }

        return true;

    }

    public List<Judge> getEventJudges(String eventId) {
        Event theEvent = eventRepository.getEvent(eventId);
        List<Judge> judges = new ArrayList<Judge>();
        //query the many to many
        List<String> judgeIds = eventRepository.getEventJudges(theEvent.getId());
        //grab the actual judge object
        logger.info("found some judgeIds for this event --" + judgeIds.size());
        for(String jid : judgeIds) {
            Judge jud = (Judge) userRepository.getJudge(jid);
            logger.info("Getting the event judges " + jud.getFirstName());
            judges.add(jud);
        }
        return judges;
    }

    /**
     * Remove an event and all of its event_judges (eventually remove teams)
     * @param eventId
     * @return
     */
    public boolean removeEvent(String eventId) {
        boolean eventRemove = eventRepository.removeEvent(eventId);
        if(eventRemove) {
            boolean judgesRemoved = eventRepository.removeEventJudges(eventId);
            return judgesRemoved;
        }
        return false;
    }
}
