package edu.pennstate.science_olympiad.repositories;

import com.google.gson.Gson;
import com.mongodb.WriteResult;
import edu.pennstate.science_olympiad.helpers.request.LoginJsonHelper;
import edu.pennstate.science_olympiad.School;
import edu.pennstate.science_olympiad.people.*;
import edu.pennstate.science_olympiad.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.swing.*;
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
            logger.info("Hashing user's password");
            user.hashPassword();
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
        if (user != null && user.isPasswordEqual(loginHelper.getPassword()))
            return user;
        else
            return null;
    }

    public AUser getUser(String userId) {
        logger.info("Looking for user");
        Query lookup = new Query();
        lookup.addCriteria(Criteria.where("_id").is(userId));
        return mongoTemplate.findOne(lookup, AUser.class, "ausers");
    }

    public boolean removeUser(String userId) {
        logger.info("Removing user");
        AUser user = getUser(userId);

        if (user != null) {
            mongoTemplate.remove(user);
            return true;
        }
        return false;
    }

    public void removeAllUsers() {
        logger.info("Attempting to remove all users");
        List<AUser> users = mongoTemplate.findAll(AUser.class);
        for (AUser user : users)
            mongoTemplate.remove(user);
    }

    public boolean updateUser(String userType, String userId, String updatedUserJson) {
        logger.info("Updating user");
        Gson gson = new Gson();
        AUser userToUpdate = null;
        AUser infoToUse;
        if (userType.equalsIgnoreCase(IUserTypes.ADMIN)){
            userToUpdate = getUser(userId);
            infoToUse = gson.fromJson(updatedUserJson, Admin.class);
        } else if (userType.equalsIgnoreCase(IUserTypes.COACH)){
            userToUpdate = getUser(userId);
            infoToUse = gson.fromJson(updatedUserJson, Coach.class);
        } else if (userType.equalsIgnoreCase(IUserTypes.JUDGE)){
            userToUpdate = getUser(userId);
            infoToUse = gson.fromJson(updatedUserJson, Judge.class);
        } else if (userType.equalsIgnoreCase(IUserTypes.STUDENT)){
            userToUpdate = getUser(userId);
            infoToUse = gson.fromJson(updatedUserJson, Student.class);
        } else {
            infoToUse = null;
        }

        if (userToUpdate != null) {
            userToUpdate.copyInfo(infoToUse);
            mongoTemplate.save(userToUpdate);
            return true;
        }
        return false;
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

        if (coach != null) {
            coach.setSchool(school);
            mongoTemplate.save(coach);
            return true;
        }
        return false;
    }

    public boolean resetPassword(String userId) {
        AUser user = mongoTemplate.findById(userId, AUser.class);
        //password cleared
        user.setPassword("");
        mongoTemplate.save(user);
        //send and email to the user telling them to change their password
        //the link from the email should put them on a separate page that lets them set a new password
        JOptionPane.showMessageDialog(null, "Password as been reset and email has been sent to the window licker that forgot their password");
        return true;
    }

    public boolean changePassword(String userId, String newPassword) {
        AUser user = mongoTemplate.findById(userId, AUser.class);

        if(user!=null) {
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }

    public boolean validate(String userId, String password) {
        AUser user = mongoTemplate.findById(userId, AUser.class);

        return user != null && user.isPasswordEqual(password);
    }

    public Pair getPw(AUser user, String password) {

        String oldPw = user.getPassword();
        String newPw = user.get_SHA_512_SecurePassword(password);

        return new Pair(oldPw, newPw);
    }

    /**
     * Returns the judges from the db, right now just show coaches for testing
     * @return
     * @throws Exception
     */
    public List<Judge> getAllJudges() throws Exception{
        Query query = new Query();
        query.addCriteria(Criteria.where("_class").is("edu.pennstate.science_olympiad.people.Judge"));
        //findall doesnt seem to work ?
        List<Judge> u = mongoTemplate.find(query,Judge.class);
        logger.info("found some judge");
        return u;
    }

    /**
     * Get a judge from the db
     * @param jid
     * @return
     */
    public Judge getJudge(String jid) {
        Query lookup = new Query();
        lookup.addCriteria(Criteria.where("_id").is(jid).and("_class").is("edu.pennstate.science_olympiad.people.Judge"));
        Judge u = mongoTemplate.findOne(lookup, Judge.class);
        if(u== null) {
            logger.info(jid + "    THe getJudge user is null!!!!");
        }
        logger.info("in the use rrepository found " + u.toString());
        return u;
    }
}
