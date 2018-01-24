package edu.pennstate.science_olympiad.repository;

import com.mongodb.WriteResult;
import edu.pennstate.science_olympiad.people.AUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Class to perform CRUD operations on the different types of users.
 * It Registers this bean to be used elsewhere
 */
@Repository("userRepository")
public class UserRepository {
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
     * Currently stores all users in the same collection (ausers)
     * @param user
     */
    public void addUser(AUser user) {
        //just testing some operations

        logger.info("trying to delete");
        Query delete = new Query();
        delete.addCriteria(Criteria.where("firstName").is("Kyle"));
        WriteResult res = mongoTemplate.remove(delete,"ausers");
        logger.info("Removed -- " + res.getN());

        logger.info("Trying to insert into DB " +user.getFirstName());
        mongoTemplate.insert(user);


        Query query = new Query();
        query.addCriteria(Criteria.where("firstName").is("Kyle"));
        List<AUser> kyle = mongoTemplate.find(query,AUser.class);
        logger.info("found kyle " + kyle.size());

    }
}
