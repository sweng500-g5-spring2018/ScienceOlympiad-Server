package edu.pennstate.science_olympiad.repositories;

import com.mongodb.WriteResult;
import edu.pennstate.science_olympiad.helpers.request.LoginJsonHelper;
import edu.pennstate.science_olympiad.School;
import edu.pennstate.science_olympiad.people.AUser;
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
     * @param user the user to store in the database
     * @return Whether the user was added or not, will not add the user if it already exists
     */
    public boolean addUser(AUser user) {
        //just testing some operations

        logger.info("Checking if user is already here");
        Query checking = new Query();
        checking.addCriteria(Criteria.where("emailAddress").is(user.getEmailAddress()));
        boolean exists = mongoTemplate.exists(checking,"ausers");

        if (!exists) {
            logger.info("Adding user: " + user.getName());
            mongoTemplate.insert(user, "ausers");
            return true;
        }
        return false;
    }

    public AUser getUser(LoginJsonHelper loginHelper) {
        logger.info("Looking for user with email " + loginHelper.getEmailAddress());
        Query lookup = new Query();
        lookup.addCriteria(Criteria.where("emailAddress").is(loginHelper.getEmailAddress()));
        AUser user = mongoTemplate.findOne(lookup, AUser.class, "ausers");
        //if (user.isPasswordEqual(loginHelper.getPassword()))
            return user;
        //else
            //return null;
    }

    public void removeAllUsers() {
        logger.info("Attempting to remove all users");
        List<AUser> users = mongoTemplate.findAll(AUser.class);
        for (AUser user : users)
            mongoTemplate.remove(user);
    }

    public boolean emailUsed(String emailAddress) {
        logger.info("Checking to see if email address is in use");
        Query query = new Query();
        query.addCriteria(Criteria.where("emailAddress").is(emailAddress));
        return ( mongoTemplate.find(query, AUser.class).size() > 0 );
    }

    public boolean addCoachToStudent(Coach coach, Student student) {
        logger.info("Attempting to add coach to student");
        Query query = new Query();
        query.addCriteria(Criteria.where("emailAddress").is(student.getEmailAddress()));
        Student dbStudent = mongoTemplate.findOne(query, Student.class);

        if (dbStudent != null) {
            dbStudent.setCoach(coach);
            mongoTemplate.save(dbStudent);
            return true;
        }
        return false;
    }

    public boolean addSchoolToCoach(School school, Coach coach) {
        logger.info("Attempting to add school to coach");
        Query query = new Query();
        query.addCriteria(Criteria.where("emailAddress").is(coach.getEmailAddress()));
        Coach dbCoach = mongoTemplate.findOne(query, Coach.class);

        if (dbCoach != null) {
            dbCoach.setSchool(school);
            mongoTemplate.save(dbCoach);
            return true;
        }
        return false;
    }

}
