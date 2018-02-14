package edu.pennstate.science_olympiad.controllers;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.URIConstants;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Student;
import edu.pennstate.science_olympiad.repositories.TeamRepository;
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

    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_STUDENT_TO_TEAM, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addStudentToTeam(@RequestBody String studentJson, @RequestBody String teamJson) {
        logger.info("Adding new student to team");
        try {
            Gson gson = new Gson();
            Team team = gson.fromJson(teamJson, Team.class);
            Student student = gson.fromJson(studentJson, Student.class);

            short added = teamRepository.addStudentToTeam(student, team);

            switch (added) {
                case 0:
                    return ResponseEntity.status(HttpStatus.OK).body("Student was added.");
                case 1:
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Student already on this team.");
                case 2:
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Student already on another team.");
                case 3:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Team does not exist.");
                default :
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown error.");
            }

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_COACH_TO_TEAM, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addCoachToTeam(@RequestBody String coachJson, @RequestBody String teamJson) {
        logger.info("Adding coach to team");
        try {
            Gson gson = new Gson();
            Team team = gson.fromJson(teamJson, Team.class);
            Coach coach = gson.fromJson(coachJson, Coach.class);

            boolean added = teamRepository.addCoachToTeam(coach, team);

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("Coach was added.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Team cannot be found.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value= REMOVE_TEAM, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> removeTeam(@RequestBody String teamJson) {
        logger.info("Removing team");
        try {
            Gson gson = new Gson();
            Team team = gson.fromJson(teamJson, Team.class);

            boolean added = teamRepository.removeTeam(team);

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("Team was removed.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Team not removed, couldn't be found.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value= REMOVE_STUDENT_FROM_TEAM, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> removeStudentFromTeam(@RequestBody String studentJson, @RequestBody String teamJson) {
        logger.info("Removing student from team");
        try {
            Gson gson = new Gson();
            Team team = gson.fromJson(teamJson, Team.class);
            Student student = gson.fromJson(studentJson, Student.class);

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
