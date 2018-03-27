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

}
