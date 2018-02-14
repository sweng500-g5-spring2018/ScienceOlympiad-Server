package edu.pennstate.science_olympiad.repositories;

import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.Team;
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
     * @param eventName teh event to get from the database
     * @return The event requested
     */
    public Event getEvent(String eventName) {
        logger.info("retrieving the db event " +eventName);

        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(eventName));
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
     */
    public boolean assignJudgeToEvent(Judge judge, Event event) {
        logger.info("Adding judge to event");

        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(event.getName()));
        Event dbEvent = mongoTemplate.findOne(query, Event.class);

        if (dbEvent != null) {
            dbEvent.addJudgeToEvent(judge);
            mongoTemplate.save(dbEvent);

            logger.info("Event saved in database");
            return true;
        }
        return false;
    }

}
