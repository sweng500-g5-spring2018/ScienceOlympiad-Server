package edu.pennstate.science_olympiad.repositories;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Student;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("teamRepository")
public class TeamRepository {
    Log logger = LogFactory.getLog(getClass());

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * Gets all of the teams in the database
     * @return All of the teams
     */
    public List<Team> getTeams() {
        return mongoTemplate.findAll(Team.class);
    }

    /**
     * Adds a new Team
     * @param team team to add
     * @return Team successfully added
     */
    public boolean addNewTeam(Team team) {

        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(team.getName()));
        boolean exists = mongoTemplate.exists(query, Team.class);

        if (!exists) {
            logger.info("Adding a new team");
            mongoTemplate.insert(team);
            return true;
        }
        logger.info("Team already exists");
        return false;
    }

    /**
     * Adds student to a team
     * @param student student to add to the team
     * @param team team to add the student to
     * @return whether the student was added to the team or not
     *        0 - Student was added
     *        1 - Student already on this team
     *        2 - Student already on another team
     */
    public short addStudentToTeam(Student student, Team team) {
        if (team.getStudents().contains(student))
            return 1;
        else {
            //Check to make sure the student is not already on another team
            List<Team> allTeams = mongoTemplate.findAll(Team.class);
            allTeams.remove(team);
            boolean onAnotherTeam = false;
            for (Team aTeam : allTeams) {
                if (aTeam.getStudents().contains(student))
                    onAnotherTeam = true;
            }

            if (onAnotherTeam)
                return 2;

            team.getStudents().add(student);
            mongoTemplate.save(team);
            return 0;

        }
    }

    /** @DEPRECATED probably
     *
     * Adds student to a team
     * @param students students to add to the team
     * @param team team to add the student to
     * @return whether the student was added to the team or not
     *        0 - Students were added
     *        1 - A student is already on this team
     *        2 - A Student is already on another team
     */
//    public short addStudentsToTeam(List<String> students, Team team) {
//        List<Student> common = new ArrayList<Student>(team.getStudents());
//        common.retainAll(students);
//
//        if (common.size() > 0)
//            return 1;
//        else {
//            //Check to make sure the student is not already on another team
//            List<Team> allTeams = mongoTemplate.findAll(Team.class);
//            allTeams.remove(team);
//
//            for (Team aTeam : allTeams) {
//                common = new ArrayList<Student>(team.getStudents());
//                common.retainAll(students);
//                if (common.size() > 0)
//                    return 2;
//            }
//
//            team.addStudents(students);
//            mongoTemplate.save(team);
//            return 0;
//        }
//    }


    /**
     * Adds a coach to a specified team
     * @param coach Coach to add to the team
     * @param team Team in which to add the coach
     * @return if the coach was added
     */
    public boolean addCoachToTeam(Coach coach, Team team) {
       team.setCoach(coach);
       mongoTemplate.save(team);
       Team dbTeam = getTeam(team.id);
       if (dbTeam.getCoach() == coach)
           return true;
       return false;
    }

    /**
     * Removes a team altogether
     * @param team Team to remove from the database
     * @return if the team was removed
     */
    public boolean removeTeam(Team team) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("_id").is(team.id));
        Team dbTeam = mongoTemplate.findOne(singleQuery, Team.class);

        if (dbTeam != null) {
            mongoTemplate.remove(dbTeam);
            return true;
        }
        return false;
    }

    /**
     * Removes a student from a specified team
     * @param student Student to remove from the team
     * @param team Team in which to remove the student
     * @return if the student was removed from the team
     */
    public boolean removeStudentFromTeam(Student student, Team team) {
        if(student != null && team != null) {
            boolean isDeleted = team.getStudents().remove(student);

            if(isDeleted) {
                mongoTemplate.save(team);
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    /**
     * Removes a student from a specified team
     * @param student Student to remove from the team
     * @return if the student was removed from the team
     */
    public boolean removeStudentFromTeam(Student student) {

        List<Team> teamsForSchool = getTeamsFromSchool(student.getSchool().getId());

        for (Team team : teamsForSchool) {

            Student studToRemove = null;
            for(Student stud : team.getStudents()) {
                if(stud.getId().equals(student.getId())) {
                    studToRemove = stud;
                }
            }

            if(studToRemove != null) {
                return removeStudentFromTeam(studToRemove, team);
            }
        }

        return true;
    }

    /**
     * Gets a specific team from the database
     * @param teamId the id number of the team to get
     * @return the team from the database
     */
    public Team getTeam(String teamId) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("_id").is(teamId));
        return mongoTemplate.findOne(singleQuery, Team.class);
    }

    /**
     * Returns the teams that belong to the specific school
     *
     * @param schoolId the mongoID of the school
     * @return a list of Teams
     * @throws Exception
     */
    public List<Team> getTeamsFromSchool(String schoolId) {
        try {
            Query query = new Query();

            query.addCriteria(Criteria.where("school.$id").is(new ObjectId(schoolId)));

            List<Team> teams = mongoTemplate.find(query, Team.class);

            return teams;
        } catch (Exception e) {
            return new ArrayList<Team>();
        }

    }

    /**
     * updates a team in the database
     * @param teamId The team to update
     * @param teamJson the info to update the team with
     * @return whether the team was updated or not
     */
    public boolean updateTeam (String teamId, String teamJson) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("_id").is(teamId));
        Team dbTeam = mongoTemplate.findOne(singleQuery, Team.class);

        Gson gson = new Gson();
        Team tempTeam = gson.fromJson(teamJson, Team.class);
        if (dbTeam != null) {
            dbTeam.copyInfo(tempTeam);
            mongoTemplate.save(dbTeam);
            return true;
        }
        return false;
    }

//    Note there is no method for removing a coach, instead just replace them. Weird things are bound to happen
//    if a team does not have a coach
}
