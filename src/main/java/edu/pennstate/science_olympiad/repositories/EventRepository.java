package edu.pennstate.science_olympiad.repositories;

import com.google.gson.Gson;
import com.mongodb.WriteResult;
import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.many_to_many.Judge_Event;
import edu.pennstate.science_olympiad.many_to_many.Team_Event;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Judge;
import edu.pennstate.science_olympiad.people.Student;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.awt.event.TextEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * This will be used to perform CRUD operations on {@link edu.pennstate.science_olympiad.Event}s
 * This is then registered to be used elsewhere.
 */
@Repository("eventRepository")
public class EventRepository {
    Log logger = LogFactory.getLog(getClass());

    @Autowired
    MongoTemplate mongoTemplate;

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate=mongoTemplate;
    }

    /**
     * Stores event in the database
     * @param event adds a new event
     */
    public void addEvent(Event event) {
        logger.info("Trying to insert into DB " + event.getName());
        mongoTemplate.insert(event);

        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(event.getName()));
        Event dbEvent = mongoTemplate.findOne(query, Event.class);
        if (dbEvent != null)
            logger.info("Insert Completed");
        else
            logger.info("Insert Failed");
    }

    /**************************************
     *
     * Just added all of these methods to easily see whats happening
     * @param event adds a new event rto the database
     * @return whether the event was created or not
     */
    public boolean createNewEvent(Event event) {

        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(event.getName()));
        boolean exists = mongoTemplate.exists(query, Event.class);

        if (!exists) {
            logger.info("Inserting a new event " + event.getName());
            mongoTemplate.insert(event);
            return true;
        }

        logger.info("Event already exists");
        return false;
    }

    /**
     * For testing
     * @param coach saves a test coach to the db
     */
    public void saveTestCoach(Coach coach) {
        mongoTemplate.insert(coach);
    }

    /**
     * For testing
     * @param student saves a fake student to the db
     */
    public void saveTestStudent(Student student) {
        mongoTemplate.insert(student);
    }

    /**
     * creates a team
     * @param team saves a team
     */
    public void createTeam(Team team) {
        logger.info("creating the team");
        mongoTemplate.insert(team);
    }

    /**
     * Gets a specified event from the database
     * @param eventId the eventId to get from the database
     * @return The event requested
     */
    public Event getEvent(String eventId) {
        logger.info("Retrieving the db event " + eventId);

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(eventId));
        logger.info("Query " + query.toString());
        Event dbEvent = mongoTemplate.findOne(query, Event.class);

        logger.info("returning the db event " +dbEvent.getName());
        return dbEvent;
    }

    /**
     * Creates a Team_Event
     * @param teamEvent the Team_event to create
     */
    public void createTeamEvent(Team_Event teamEvent) {
        logger.info("inserting a team event record");
        mongoTemplate.insert(teamEvent);
    }

    /**
     * Gets all events from the database
     * @return all of the events
     */
    public List<Event> getEvents() {
        logger.info("Retrieving events from the database");

        Query query = new Query();
        List<Event> events = mongoTemplate.findAll(Event.class);
        logger.info("found " +events.size());
        return events;
    }

    /**
     * Assigns a judge to an event
     * @param judge The judge to be assigned
     * @param event The event in which to assign the judge
     * @return Whether the assignment was a sucess or not

    public boolean assignJudgeToEvent(Judge judge, Event event) {
        logger.info("Adding judge to event");

        if (event != null) {
            event.addJudgeToEvent(judge);
            mongoTemplate.save(event);

            logger.info("Event saved in database");
            return true;
        }
        return false;
    }
     */
    /**
     * Verify an event already exists
     * @param eventName an event attempting to be verified
     * @return If the event exists or not
     */
    public boolean verifyEventUnique(String eventName) {
        logger.info("verifying the event is unique " + eventName);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(eventName));
        logger.info("Query " + query.toString());
        return mongoTemplate.findOne(query, Event.class) == null;
    }

    /**
     * Removes an event from the database
     * @param eventId The event to remove
     * @return whether the event was removed or not
     */
    public boolean removeEvent (String eventId) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("_id").is(eventId));
        Event dbEvent = mongoTemplate.findOne(singleQuery, Event.class);

        if (dbEvent != null) {
            logger.info("trying to remove event ");
            mongoTemplate.remove(dbEvent);
            return true;
        }
        logger.info("event does not exist");
        return false;
    }

    /**
     * updates an event in the database
     * @param eventId The event to update
     * @return whether the event was updated or not
     */
    public boolean updateEvent (String eventId, Event event) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("_id").is(eventId));
        Event dbEvent = mongoTemplate.findOne(singleQuery, Event.class);

        if (dbEvent != null) {
            dbEvent.copyInfo(event);
            mongoTemplate.save(dbEvent);
            return true;
        }
        return false;
    }

    /**
     * Created the many to many mapping of events to judges
     */
    public void mapJudgeToEvent(Judge_Event je) {
        mongoTemplate.insert(je);
    }

    /**
     * Get the judges for this event
     * @param id
     * @return
     */
    public List<String> getEventJudges(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("eventId").is(id));
        List<Judge_Event> judgeE = mongoTemplate.find(query, Judge_Event.class);
        List<String> judgeIds = new ArrayList<String>();
        for(Judge_Event je : judgeE) {
            judgeIds.add(je.getJudge());
        }

        return judgeIds;
    }

    /**
     * Remove the judges assigned to an event
     * @param eventId
     * @return
     */
    public boolean removeEventJudges(String eventId) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("eventId").is(eventId));
        List<Judge_Event> judges = mongoTemplate.find(singleQuery, Judge_Event.class);
        logger.info("Found some judges --- " + judges.size());
        if (judges != null && judges.size() >0 ) {
            logger.info("trying to remove the judges ");
            for(Judge_Event je : judges) {
                mongoTemplate.remove(je);
            }
        }
        return true;
    }

    /**
     * Adds a score for a team for a specific event
     * @param score the score the team earned
     * @param teamId the team id that earned the score
     * @param eventId the event that the team earned the score participating in
     * @return Whether the score was added or not
     */
    public boolean addScoreToTeamEvent(Double score, String teamId, String eventId) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("teamId").is(teamId)).addCriteria(Criteria.where("eventId").is(eventId));
            logger.info("Query " + query.toString());
            Team_Event dbTeamEvent = mongoTemplate.findOne(query, Team_Event.class);
            if (dbTeamEvent != null) {
                dbTeamEvent.setScore(score);
                mongoTemplate.save(dbTeamEvent);
                return true;
            }
            return false;
        } catch(Exception e) {
            return false;
        }
    }

    /**
     * Gets all of the Team_Event objects with includes the team, the event, and the scores
     * @return List<Team_Event>
     */
    public List<Team_Event> getAllScores() {
        try {
            List<Team_Event> team_events = mongoTemplate.findAll(Team_Event.class);
            if (team_events != null)
                return team_events;
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Gets all of the Team_Event objects for a specific team.
     * @param teamId the ID for the team that you want the event and score information for
     * @return The event and score information for the team specified
     */
    public List<Team_Event> getAllScoresForTeam(String teamId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("teamId").is(teamId));
        logger.info("Query " + query.toString());
        List<Team_Event> dbTeamEvents = mongoTemplate.find(query, Team_Event.class);
        if (dbTeamEvents != null) {
            return dbTeamEvents;
        }
        return null;
    }

    /**
     * Gets all of the Team_Event objects for a specific event.
     * @param eventId the ID for the event that you want the teams who participated and the score information for
     * @return The team and score information for the specified event
     */
    public List<Team_Event> getAllScoresForEvent(String eventId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("eventId").is(eventId));
        logger.info("Query " + query.toString());
        List<Team_Event> dbTeamEvents = mongoTemplate.find(query, Team_Event.class);
        if (dbTeamEvents != null) {
            return dbTeamEvents;
        }
        return null;
    }


    /**
     * Register a team for an event unless it is already registered
     * @param teamId
     * @param eventId
     * @return
     */
    public boolean registerTeamToEvent(String teamId, String eventId) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("eventId").is(eventId));
        singleQuery.addCriteria(Criteria.where("teamId").is(teamId));
        List<Team_Event> existing = mongoTemplate.find(singleQuery,Team_Event.class,"team_events");
        logger.info("existing -- " + existing.size());
        if(existing.size() > 0) {
            return false;
        }
        Team_Event te = new Team_Event(teamId,eventId);
        mongoTemplate.insert(te);
        return true;
    }

    /**
     * Get all team ids registered for this event id
     * @param id
     * @return
     */
    public List<String> getTeamIds(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("eventId").is(id));
        List<Team_Event> teamE = mongoTemplate.find(query, Team_Event.class);
        List<String> teamIds = new ArrayList<String>();
        for(Team_Event je : teamE) {
            teamIds.add(je.getTeamId());
        }

        return teamIds;
    }

    /**
     * Remove the teams registered for an event on event delete
     * @param eventId
     * @return
     */
    public boolean removeEventTeams(String eventId) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("eventId").is(eventId));
        List<Team_Event> teams = mongoTemplate.find(singleQuery, Team_Event.class);
        if (teams != null && teams.size() >0 ) {
            for(Team_Event te : teams) {
                mongoTemplate.remove(te);
            }
        }
        return true;
    }

    public boolean removeTeamFromEvent(String eventId, String teamId) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("eventId").is(eventId)).addCriteria(Criteria.where("teamId").is(teamId));
        WriteResult team = mongoTemplate.remove(singleQuery, Team_Event.class);
        logger.info("removed the team event " + team.isUpdateOfExisting() + " -- " + team.getN());
        return true;
    }

    public boolean removeJudgeFromEvent(String eventId, String judgeId) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("eventId").is(eventId)).addCriteria(Criteria.where("judgeId").is(judgeId));
        WriteResult judge = mongoTemplate.remove(singleQuery, Judge_Event.class);

        return true;
    }
}
