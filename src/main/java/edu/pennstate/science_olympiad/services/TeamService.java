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
import edu.pennstate.science_olympiad.people.AUser;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Judge;
import edu.pennstate.science_olympiad.people.Student;
import edu.pennstate.science_olympiad.repositories.BuildingRepository;
import edu.pennstate.science_olympiad.repositories.EventRepository;
import edu.pennstate.science_olympiad.repositories.TeamRepository;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import edu.pennstate.science_olympiad.sms.EmailSender;
import edu.pennstate.science_olympiad.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bean to control the execution flow of certain business actions related to events
 */
@Service
public class TeamService {
    @Autowired
    TeamRepository teamRepository;

    @Autowired
    UserRepository userRepository;

    Log logger = LogFactory.getLog(getClass());

    public TeamRepository getTeamRepository() {
        return teamRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    /**
     * @param studentsToTeamJson - The JSON string containing an eventID and a list of studentIDs
     * @return Success no matter what
     */
    public Team addStudentsToTeam(String studentsToTeamJson) {
        try{
            Gson gson = new Gson();
            String teamId = JsonHelper.getIdFromJson(studentsToTeamJson);

            Team team = gson.fromJson(studentsToTeamJson, Team.class);

            boolean updated = teamRepository.updateTeam(teamId, studentsToTeamJson);

            if(updated) {
                return teamRepository.getTeam(teamId);
            } else {
                return null;
            }
        }catch(Exception e) {
            logger.info("Failed to add students to a team.");
            return null;
        }
    }

    public List<Student> filterStudentsOnTeam(List<Student> students, String schoolId) {
        try {
            List<Student> toExclude = new ArrayList<>();


            //Check to make sure the student is not already on another team very inefficiently ;)
            List<Team> teamsForSchool = teamRepository.getTeamsFromSchool(schoolId);

            for(Student student : students) {
                for(Team team: teamsForSchool) {
                    for(Student st : team.getStudents()) {
                        if(st.getId().equals(student.getId())) {
                            toExclude.add(student);
                            break;
                        }
                    }
                }
            }

            students.removeAll(toExclude);

            return students;
        } catch(Exception e) {
            logger.info("Failed to filter students on team by school");
            return null;
        }

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
//    public boolean addEvent(String eventJson) throws Exception {
//        //store all existing judge ids and new judge ids for mapping
//        List<String> addedJudges = new ArrayList<String>();
//        //logger.info("got into add event -- " + eventJson);
//
//        Gson gson = new Gson();
//        String tempevent = JsonHelper.getJsonObject(eventJson, "eventJson");
//
//        //this returns the building element
//        String buildingString = JsonHelper.getJsonPrimitive(tempevent,"building");
//        String actualEvent = JsonHelper.removeAndGetElement(eventJson,"building","eventJson");
//        logger.info("got the building!!! " + buildingString);
//
//        Building eventBuilding = buildingRepository.getBuilding(buildingString);
//        Event event = gson.fromJson(actualEvent, Event.class);
//        event.setLocation(eventBuilding);
//        eventRepository.addEvent(event);
//        // logger.info("created the event ok "+ event.getId());
//
//        //get any existing judges selected in the json request
//        JsonArray currentJudges = JsonHelper.getJsonList(eventJson, "existingJudgeValues");
//        for (int i = 0; i < currentJudges.size(); i++) {
//            logger.info("getting currentJudges" + currentJudges.get(i));
//            //strip off the quotes for some reason the json comes with in the json list
//            addedJudges.add(currentJudges.get(i).toString().replace("\"",""));
//        }
//
//        //create any new judges found
//        JsonArray newJudges = JsonHelper.getJsonList(eventJson, "newJudgeValues");
//        for (int i = 0; i < newJudges.size(); i++) {
//            NewJudgeHelper judge = gson.fromJson(newJudges.get(i), NewJudgeHelper.class);
//            AUser newJudge = new Judge();
//            ((Judge) newJudge).copyInfoFromJson(judge);
//            //password is hashed in addUser method
//            String defaultPass = "default123";
//            newJudge.setPasswordPlainText(defaultPass);
//            boolean added = userRepository.addUser(newJudge);
//            //password will now be hashed if the user doesnt already exist
//            if (added) {
//                String emailToSend = newJudge.getEmailAddress();
//                String firstName = newJudge.getFirstName();
//                String emailText="Dear, " +firstName+"\n\n"+
//                        " Please Login to the Science Olympiad System to complete your profile" +
//                        "\n\n Password : " +defaultPass;
//                    //send the email to the user
//                    emailSender.sendMail(emailToSend,"Account Creation",emailText);
//                    logger.info("account creation email has been sent");
//
//                addedJudges.add(newJudge.getId());
//            } else {
//                logger.info("an error occured creating a new judge " + newJudge.getEmailAddress());
//            }
//
//        }
//        //map all of the added/existing judges ids to the event id
//        for (String aJudge : addedJudges) {
//            Judge_Event je = new Judge_Event(event.getId(), aJudge);
//            eventRepository.mapJudgeToEvent(je);
//            logger.info("mapping this judge " + aJudge);
//        }
//
//        return true;
//
//    }

}
