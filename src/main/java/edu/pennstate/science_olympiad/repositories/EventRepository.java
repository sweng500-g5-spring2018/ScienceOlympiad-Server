package edu.pennstate.science_olympiad.repositories;

import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.many_to_many.Team_Event;
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
     * @param event
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
     */
    public void createNewEvent(Event event) {
        logger.info("Inserting a new event " + event.getName());
        mongoTemplate.insert(event);
    }

    public void saveTestCoach(Coach coach) {
        mongoTemplate.insert(coach);
    }

    public void saveTestStudent(Student student) {
        mongoTemplate.insert(student);
    }

    public void createTeam(Team team) {
        logger.info("creating the team");
        mongoTemplate.insert(team);
    }

    public Event getEvent(String eventName) {
        logger.info("retrieving the db event " +eventName);

        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(eventName));
        logger.info("Query " + query.toString());
        Event dbEvent = mongoTemplate.findOne(query, Event.class);

        logger.info("returning the db event " +dbEvent.getName());
        return dbEvent;

    }

    public void createTeamEvent(Team_Event teamEvent) {
        logger.info("inserting a team event record");
        mongoTemplate.insert(teamEvent);
    }

    public List<Event> getEvents() {
        logger.info("Retrieving events from the database");

        Query query = new Query();
        List<Event> events = mongoTemplate.findAll(Event.class);
        logger.info("found " +events.size());
        return events;
    }
}
