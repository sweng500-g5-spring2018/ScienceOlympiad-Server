package edu.pennstate.science_olympiad.controllers;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.URIConstants;
import edu.pennstate.science_olympiad.helpers.mongo.MongoIdVerifier;
import edu.pennstate.science_olympiad.people.AUser;
import edu.pennstate.science_olympiad.people.Admin;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Student;
import edu.pennstate.science_olympiad.repositories.TeamRepository;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import edu.pennstate.science_olympiad.helpers.json.JsonHelper;
import edu.pennstate.science_olympiad.services.TeamService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * This contains all of the endpoints on the web server for managing Teams and their members
 */
@RestController
public class TeamController implements URIConstants{
    @Autowired
    private TeamService teamService;
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
    @RequestMapping(value= GET_TEAMS_BY_USER, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getTeamsByUser(HttpServletRequest request) {
        logger.info("Retrieving all teams by this user");
        try {
            HttpSession userSession = request.getSession(false);
            if(userSession == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session exists.");
            }

            AUser user = (AUser) userSession.getAttribute("user");
            if(user instanceof Coach) {
                logger.info("Coach instance");
               return  ResponseEntity.status(HttpStatus.OK).body(teamRepository.getTeamByCoach((Coach) user));
            } else if(user instanceof Admin) {
                return  ResponseEntity.status(HttpStatus.OK).body(teamRepository.getTeams());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User should not be able to see any teams.");
            }
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
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
     * Adds the students to the team by updating the team using the provided JSON
     * @param studentsToTeamJson the JSON string containing a team object with updated students JSON
     * @return the success or failure of the request
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= UPDATE_STUDENTS_IN_TEAM, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addStudentToTeam(@RequestBody String studentsToTeamJson) {
        logger.info("Adding students to team");
        try {
            Team updatedTeam = teamService.updateTeamWithNewStudents(studentsToTeamJson);

            if(updatedTeam != null) {
                return ResponseEntity.status(HttpStatus.OK).body(updatedTeam);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update team.");
            }
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }



    /**
     * Adds a coach to a team, a team can only have one coach
     * @param coachIdJson the mongoID of the coach to add to the team
     * @param teamIdJson the mongoID of the team to add the coach to
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
            if(teamService.isTeamInEvent(teamId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Team is set to participate in an event.  Cannot delete.");
            }

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
}
