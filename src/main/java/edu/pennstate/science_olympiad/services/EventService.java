package edu.pennstate.science_olympiad.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import edu.pennstate.science_olympiad.Building;
import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.Room;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.helpers.json.JsonHelper;
import edu.pennstate.science_olympiad.helpers.request.NewJudgeHelper;
import edu.pennstate.science_olympiad.many_to_many.Judge_Event;
import edu.pennstate.science_olympiad.many_to_many.Team_Event;
import edu.pennstate.science_olympiad.people.*;
import edu.pennstate.science_olympiad.repositories.*;
import edu.pennstate.science_olympiad.sms.EmailSender;
import edu.pennstate.science_olympiad.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
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
    RoomRepository roomRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    EmailSender emailSender;

    Log logger = LogFactory.getLog(getClass());

    public EmailSender getEmailSender() {
        return emailSender;
    }
    public EventRepository getEventRepository() {
        return eventRepository;
    }
    public UserRepository getUserRepository() {
        return userRepository;
    }
    public RoomRepository getBuildingRepository(){
        return roomRepository;
    }
    public TeamRepository getTeamRepository(){
        return teamRepository;
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
     * Create any new judges added in the form and send them emails
     *
     * @param eventJson The json passed into the request containing event,existing judge ids, new judge info
     * @return true if event is added
     * @throws Exception back to be handled by controller JsonErrors
     */
    public boolean addEvent(String eventJson) throws Exception {
        //store all existing judge ids and new judge ids for mapping
        List<String> addedJudges = new ArrayList<String>();

        Gson gson = new Gson();

        //this returns the building element
        String roomString = getBuildingFromJson(eventJson);
        String actualEvent = JsonHelper.removeAndGetElement(eventJson,"room","eventJson");

        logger.info("got the room string");
        Room eventRoom = roomRepository.getRoom(roomString);
        logger.info("found the room " + eventRoom.getRoomName());
        Event event = gson.fromJson(actualEvent, Event.class);
        event.setLocation(eventRoom);
        eventRepository.addEvent(event);
        // logger.info("created the event ok "+ event.getId());

        //create any new judges found
        addedJudges = getOldNewJudges(eventJson);
        mapJudgesToEvent(event.getId(),addedJudges);

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
     * Remove an event and all of its event_judges and event_teams
     * @param eventId
     * @return
     */
    public boolean removeEvent(String eventId) {
        boolean eventRemove = eventRepository.removeEvent(eventId);
        if(eventRemove) {
            boolean judgesRemoved = eventRepository.removeEventJudges(eventId);
            boolean teamsRemoved = eventRepository.removeEventTeams(eventId);
            return (judgesRemoved&&teamsRemoved);
        }
        return false;
    }

    public boolean updateEvent(String eventId, String eventJson) {

        List<String> addedJudges = new ArrayList<String>();

        Gson gson = new Gson();


        String roomString = getBuildingFromJson(eventJson);
        //remove the building and return the rest of the json which will be the full event
        String actualEvent = JsonHelper.removeAndGetElement(eventJson,"room","eventJson");

       // save the building with the event
        Room eventRoom = roomRepository.getRoom(roomString);
        Event event = gson.fromJson(actualEvent, Event.class);
        event.setLocation(eventRoom);
        eventRepository.updateEvent(eventId,event);
        // logger.info("created the event ok "+ event.getId());

        //remove all judge_Events
        eventRepository.removeEventJudges(eventId);
        //insert the new judges
        addedJudges = getOldNewJudges(eventJson);
        mapJudgesToEvent(eventId,addedJudges);

        return true;
    }

    /**
     * Extract the building from the json request
     * @param eventJson
     * @return
     */
    private String getBuildingFromJson(String eventJson) {
        String tempevent = JsonHelper.getJsonObject(eventJson, "eventJson");

        //this returns the building element
        String roomString = JsonHelper.getJsonPrimitive(tempevent, "room");
        return roomString;
    }

    /**
     * Extract the old judges and new judges from the json request
     * @param eventJson
     * @return
     */
    private List<String> getOldNewJudges(String eventJson) {
        List<String> addedJudges = new ArrayList<String> ();
        //create any new judges found
        Gson gson = new Gson();
        //get any existing judges selected in the json request
        JsonArray currentJudges = JsonHelper.getJsonList(eventJson, "existingJudgeValues");
        for (int i = 0; i < currentJudges.size(); i++) {
            logger.info("getting currentJudges" + currentJudges.get(i));
            //strip off the quotes for some reason the json comes with in the json list
            addedJudges.add(currentJudges.get(i).toString().replace("\"",""));
        }
        //get any new judges
        JsonArray newJudges = JsonHelper.getJsonList(eventJson, "newJudgeValues");
        for (int i = 0; i < newJudges.size(); i++) {
            NewJudgeHelper judge = gson.fromJson(newJudges.get(i), NewJudgeHelper.class);
            AUser newJudge = new Judge();
            ((Judge) newJudge).copyInfoFromJson(judge);
            //password is hashed in addUser method
            String defaultPass = "Password1";
            newJudge.setPasswordPlainText(defaultPass);
            boolean added = userRepository.addUser(newJudge);
            //password will now be hashed if the user doesnt already exist
            if (added) {
                addedJudges.add(newJudge.getId());
                String emailToSend = newJudge.getEmailAddress();
                String firstName = newJudge.getFirstName();
                String emailText="Dear, " +firstName+"\n\n"+
                        " Please Login to the Science Olympiad System to complete your profile" +
                        "\n\n Password : " +defaultPass;
                //send the email to the user
                emailSender.sendMail(emailToSend,"Account Creation",emailText);
                logger.info("account creation email has been sent");
            } else {
                logger.info("an error occured creating a new judge " + newJudge.getEmailAddress());
            }

        }

        return addedJudges;
    }

    /**
     * Create the many to many mapping of judges to events
     * @param eventId
     * @param judges
     */
    private void mapJudgesToEvent(String eventId,List<String> judges) {
        //map all of the added/existing judges ids to the event id
        for (String aJudge : judges) {
            Judge_Event je = new Judge_Event(eventId, aJudge);
            eventRepository.mapJudgeToEvent(je);
            logger.info("mapping this judge " + aJudge);
        }
    }

    /**
     * Grab the teams registered to this event to display on the details
     * @param eventId
     * @return
     */
    public List<Team> getEventTeams(String eventId) {
        Event theEvent = eventRepository.getEvent(eventId);
        List<Team> teams = new ArrayList<Team>();
        //query the many to many
        List<String> teamIds = eventRepository.getTeamIds(theEvent.getId());
        //grab the actual judge object
        logger.info("found some judgeIds for this event --" + teamIds.size());
        for(String tid : teamIds) {
            Team team = teamRepository.getTeam(tid);
            logger.info("Getting the teams for this event " + team.getName());
            teams.add(team);
        }
        return teams;
    }
}
