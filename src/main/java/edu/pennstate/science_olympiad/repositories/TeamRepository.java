package edu.pennstate.science_olympiad.repositories;

import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Student;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

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
        query.addCriteria(Criteria.where("id").is(team.id));
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
     *        3 - Team not found
     */
    public short addStudentToTeam(Student student, Team team) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("id").is(team.id));
        Team dbTeam = mongoTemplate.findOne(singleQuery, Team.class);

        if (dbTeam != null) {
            //if the student is already on the team
            if (dbTeam.getStudents().contains(student))
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

                dbTeam.getStudents().add(student);
                mongoTemplate.save(dbTeam);
                return 0;
            }
        }
        return 3;
    }

    /**
     * Adds a coach to a specified team
     * @param coach Coach to add to the team
     * @param team Team in which to add the coach
     * @return if the coach was added
     */
    public boolean addCoachToTeam(Coach coach, Team team) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("id").is(team.id));
        Team dbTeam = mongoTemplate.findOne(singleQuery, Team.class);

        if (dbTeam != null) {
            dbTeam.setCoach(coach);
            mongoTemplate.save(dbTeam);
            return true;
        }
        return false;
    }

    /**
     * Removes a team altogether
     * @param team Team to remove from the database
     * @return if the team was removed
     */
    public boolean removeTeam(Team team) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("id").is(team.id));
        Team dbTeam = mongoTemplate.findOne(singleQuery, Team.class);

        if (dbTeam != null) {
            mongoTemplate.remove(dbTeam);
            return true;
        }
        return false;
    }

    /**
     * Reoves a student from a specified team
     * @param student Student to remove from the team
     * @param team Team in which to remove the student
     * @return if the student was removed from the team
     */
    public boolean removeStudentFromTeam(Student student, Team team) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("id").is(team.id));
        Team dbTeam = mongoTemplate.findOne(singleQuery, Team.class);

        if (dbTeam != null) {
            if (dbTeam.getStudents().contains(student)) {
                dbTeam.getStudents().remove(student);
                return true;
            }
        }
        return false;
    }

//    Note there is no method for removing a coach, instead just replace them. Weird things are bound to happen
//    if a team does not have a coach
}
