package edu.pennstate.science_olympiad.repositories;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.helpers.request.LoginJsonHelper;
import edu.pennstate.science_olympiad.School;
import edu.pennstate.science_olympiad.people.*;
import edu.pennstate.science_olympiad.sms.EmailSender;
import edu.pennstate.science_olympiad.util.RandomString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
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
    @Autowired
    EmailSender emailSender;

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

    public boolean removeUser(AUser user) {
        logger.info("Removing user");

        mongoTemplate.remove(user);
        return true;
    }

    public void removeAllUsers() {
        logger.info("Attempting to remove all users");
        List<AUser> users = mongoTemplate.findAll(AUser.class);
        for (AUser user : users)
            mongoTemplate.remove(user);
    }

    public boolean updateUser(AUser user, String updatedUserJson) {
        logger.info("Updating user");
        Gson gson = new Gson();
        AUser infoToUse;
        if (user instanceof Admin){
            infoToUse = gson.fromJson(updatedUserJson, Admin.class);
        } else if (user instanceof Coach){
            infoToUse = gson.fromJson(updatedUserJson, Coach.class);
        } else if (user instanceof Judge){
            infoToUse = gson.fromJson(updatedUserJson, Judge.class);
        } else if (user instanceof Student){
            infoToUse = gson.fromJson(updatedUserJson, Student.class);
        } else {
            infoToUse = null;
        }
        logger.info(infoToUse);

        if (user != null) {
            user.copyInfo(infoToUse);
            logger.info(user);
            mongoTemplate.save(user);
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

//    public boolean addCoachToStudent(Coach coach, Student student) {
//        logger.info("Attempting to add coach to student");
//        Query query = new Query();
//        query.addCriteria(Criteria.where("emailAddress").is(student.getEmailAddress()));
//        Student dbStudent = mongoTemplate.findOne(query, Student.class);
//
//        if (dbStudent != null) {
//            dbStudent.setCoach(coach);
//            mongoTemplate.save(dbStudent);
//            return true;
//        }
//        return false;
//    }

    public boolean addSchoolToUser(School school, AUser user) {
        logger.info("Attempting to add school to coach or student");

        if(user instanceof Coach) {
            ((Coach) user).setSchool(school);
            mongoTemplate.save(user);
            return true;
        } else if(user instanceof Student) {
            ((Student) user).setSchool(school);
            mongoTemplate.save(user);
            return true;
        }

        return false;
    }

    public boolean resetPassword(String emailAddress) {
        logger.info("Resetting password for: " + emailAddress);
        Query query = new Query();
        query.addCriteria(Criteria.where("emailAddress").is(emailAddress));
        AUser user = mongoTemplate.findOne(query, AUser.class);
        if (user != null) {
            logger.info("User " + user.getFirstName() + " found");
            //password cleared
            String random = new RandomString(20).nextString();
            user.setPassword(random);
            mongoTemplate.save(user);
            //send and email to the user telling them to change their password
            boolean sent = emailSender.sendMail(user.getEmailAddress(), "Password Reset",
                    "Dear " + user.getFirstName() + ",\n\nYour password has successfully been reset on " +
                            "https://www.sweng500.com . If you feel you have been hacked feel free to give us a call " +
                            "between the hours of 10pm and 4am (EST) at 610-752-5349.\n\nYour temporary password is: " +
                            random + "\n\n\nThank you for choosing Science Olympiad!\n\n\n::cough::cough::A+::cough::cough::");

            logger.info("Password as been reset and email has been sent to the window licker that forgot their password");
            return sent;
        } else
            return false;
    }

    public void signUpEmail(String emailAddress, String firstName, String userType, String password) {
        emailSender.sendMail(emailAddress, "Welcome to Science Olympiad",  "Welcome " + firstName +
            ",\n\nYou have been signed up for Science Olympiad on https://www.sweng500.com as a " + userType +
            ".\n\n\nYour password for this site is: " + password + ".\n\n\nThank you for participating!\n\nGroup 5");
    }

    public boolean changePassword(AUser user, String newPassword) {
        if(user!=null) {
            user.setPassword(newPassword);
            mongoTemplate.save(user);
            return true;
        }
        return false;
    }

    public boolean validate(String userId, String password) {
        AUser user = mongoTemplate.findById(userId, AUser.class);

        return user != null && user.isPasswordEqual(password);
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
     * Returns the coaches from the db
     * @return
     * @throws Exception
     */
    public List<Coach> getAllCoaches() throws Exception{
        Query query = new Query();
        query.addCriteria(Criteria.where("_class").is("edu.pennstate.science_olympiad.people.Coach"));
        //findall doesnt seem to work ?
        List<Coach> coaches = mongoTemplate.find(query, Coach.class);
        logger.info("found some coaches");
        return coaches;
    }

    /**
     * Returns the students that attend the specific school
     *
     * @param schoolId the mongoID of the school
     * @return a list of Students
     * @throws Exception
     */
    public List<Student> getStudentsFromSchool(String schoolId) throws Exception{
        Query query = new Query();

        query.addCriteria( new Criteria().andOperator(
                Criteria.where("_class").is("edu.pennstate.science_olympiad.people.Student"),
                Criteria.where("school.$id").is(new ObjectId(schoolId))
        ));

        List<Student> students = mongoTemplate.find(query, Student.class);

        return students;
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
