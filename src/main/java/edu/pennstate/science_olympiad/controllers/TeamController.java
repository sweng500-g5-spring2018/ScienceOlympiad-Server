package edu.pennstate.science_olympiad.controllers;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.URIConstants;
import edu.pennstate.science_olympiad.helpers.mongo.MongoIdVerifier;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Student;
import edu.pennstate.science_olympiad.repositories.TeamRepository;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import edu.pennstate.science_olympiad.helpers.json.JsonHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This contains all of the endpoints on the web server for managing Teams and their members
 */
@RestController
public class TeamController implements URIConstants{
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;
    Log logger = LogFactory.getLog(getClass());

    @CrossOrigin(origins = "*")
    @RequestMapping(value= GET_TEAMS, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public List<Team> getTeams() {
        logger.info("Retrieving all teams");
        return teamRepository.getTeams();
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_TEAM , method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addNewTeam(@RequestBody String teamJson) {
        logger.info("Adding new team");
        try {
            Gson gson = new Gson();
            Team team = gson.fromJson(teamJson, Team.class);

            boolean added = teamRepository.addNewTeam(team);

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("Team was added.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Team already exists.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    /**
     * Adds a student to a team by id Number
     * @param studentIdJson the JSON of the student Id
     * @param teamIdJson the JSON of the team id
     * @return the success or failure of the request
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_STUDENT_TO_TEAM, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addStudentToTeam(@RequestBody String studentIdJson, @RequestBody String teamIdJson) {
        logger.info("Adding new student to team");
        try {
            Team team = teamRepository.getTeam(JsonHelper.getIdFromJson(teamIdJson));
            Student student = (Student) userRepository.getUser(JsonHelper.getIdFromJson(studentIdJson));

            short added = teamRepository.addStudentToTeam(student, team);

            switch (added) {
                case 0:
                    return ResponseEntity.status(HttpStatus.OK).body("Student was added.");
                case 1:
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Student already on this team.");
                case 2:
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Student already on another team.");
                default :
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown error.");
            }

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    /**
     * Adds a coach to a team, a team can only have one coach
     * @param coachIdJson
     * @param teamIdJson
     * @return
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_COACH_TO_TEAM, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addCoachToTeam(@RequestBody String coachIdJson, @RequestBody String teamIdJson) {
        logger.info("Adding coach to team");
        try {
            Team team = teamRepository.getTeam(JsonHelper.getIdFromJson(teamIdJson));
            Coach coach = (Coach) userRepository.getUser(JsonHelper.getIdFromJson(coachIdJson));

            boolean added = teamRepository.addCoachToTeam(coach, team);

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("Coach was added.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Team cannot be found.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    /**
     * removes a specified team from the database
     * @param teamId the team id to remove
     * @return Whether the team was removed or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= REMOVE_TEAM, method= RequestMethod.DELETE ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> removeTeam(@PathVariable("teamId") String teamId) {
        logger.info("Removing team");
        try {
            Team team = teamRepository.getTeam(teamId);

            boolean removed = teamRepository.removeTeam(team);

            if (removed)
                return ResponseEntity.status(HttpStatus.OK).body("Team was removed.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Team not removed, couldn't be found.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    /**
     * Removes a student from a specific team
     * @param studentIdJson the student ID, in JSON format
     * @param teamIdJson the teamId in JSON format
     * @return Whether the action was carried out or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= REMOVE_STUDENT_FROM_TEAM, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> removeStudentFromTeam(@RequestBody String studentIdJson, @RequestBody String teamIdJson) {
        logger.info("Removing student from team");
        try {

            Team team = teamRepository.getTeam(JsonHelper.getIdFromJson(teamIdJson));
            Student student = (Student) userRepository.getUser(JsonHelper.getIdFromJson(studentIdJson));

            boolean remove = teamRepository.removeStudentFromTeam(student, team);

            if (remove)
                return ResponseEntity.status(HttpStatus.OK).body("Student was removed.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Student not removed, could not find student or team.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    /**
     * Removes a student from a specific team
     * @param studentIdJson the student ID, in JSON format
     * @return Whether the action was carried out or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= REMOVE_STUDENT_FROM_SPECIFIC_TEAM, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> removeStudentFromTeam(@RequestBody String studentIdJson) {
        logger.info("Removing student from team");
        try {
            Student student = (Student) userRepository.getUser(JsonHelper.getIdFromJson(studentIdJson));

            boolean remove = teamRepository.removeStudentFromTeam(student);

            if (remove)
                return ResponseEntity.status(HttpStatus.OK).body("Student was removed.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Student not removed, could not find student or team.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    /**
     * Removes a specific team from the database
     * @param teamId the id of the team you want to update
     * @param teamJson the json of the info you want to update the team with
     * @return the response of the event being updated or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= UPDATE_TEAM, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateTeam(@PathVariable("teamId") String teamId, @RequestBody String teamJson) {
        try {
            if(! MongoIdVerifier.isValidMongoId(teamId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid event ID.");            }

            boolean update = teamRepository.updateTeam(teamId, teamJson);

            if (update){
                return ResponseEntity.status(HttpStatus.OK).body("Event was updated.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Event could not be updated, doesn't exist.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }
}
