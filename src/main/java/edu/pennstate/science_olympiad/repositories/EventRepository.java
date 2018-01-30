package edu.pennstate.science_olympiad.repositories;

import edu.pennstate.science_olympiad.Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

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

//    public List<Event> getEvents() {
//        logger.info("Retrieving events from the database");
//
//        Query query = new Query();
//        query.addCriteria(Criteria.where("name").)
//    }
}
