package edu.pennstate.science_olympiad.controllers;


import com.google.gson.Gson;
import edu.pennstate.science_olympiad.School;
import edu.pennstate.science_olympiad.URIConstants;
import edu.pennstate.science_olympiad.helpers.json.JsonHelper;
import edu.pennstate.science_olympiad.helpers.mongo.MongoIdVerifier;
import edu.pennstate.science_olympiad.people.*;
import edu.pennstate.science_olympiad.repositories.SchoolRepository;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import edu.pennstate.science_olympiad.services.TeamService;
import edu.pennstate.science_olympiad.util.Pair;
import edu.pennstate.science_olympiad.util.RandomString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * This class contains all of the endpoints that can be called to retrieve and manipulate users in the system
 */
@RestController
public class UsersController implements URIConstants{
    Log logger = LogFactory.getLog(getClass());

    @Autowired
    UserRepository userRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    SchoolRepository schoolRepository;
    @Autowired
    TeamService teamService;

    /**
     * Returns a user from their email address
     * URI is /sweng500/getUser
     * @return the user in JSON form
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= GET_USER_PROFILE, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getUser(HttpServletRequest request) {
        try {
            HttpSession userSession = request.getSession(false);
            if(userSession == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session exists.");
            }

            AUser user = (AUser) userSession.getAttribute("user");
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }

    /**
     * Returns a list of judges in the system
     * URI is /sweng500/getJudges
     * @return all of the judges in JSON
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= GET_JUDGES, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAllJudges() {
        try {
            //eventually change to judge
            List<Judge> judges = userRepository.getAllJudges();

            return ResponseEntity.status(HttpStatus.OK).body(judges);

        }catch(Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
            }
    }

    /**
     * Returns a list of coaches in the system
     * URI is /sweng500/getCoaches
     * @return all of the coaches in JSON
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= GET_COACHES, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAllCoaches() {
        try {
            //eventually change to judge
            List<Coach> coaches = userRepository.getAllCoaches();

            return ResponseEntity.status(HttpStatus.OK).body(coaches);

        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }

    /**
     * Returns a list of students that attend a specified school
     * URI is /sweng500/getStudentsFromSchool
     *
     * @param schoolId the mongoID of the school
     * @return a list of the students attending the specified school
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value=GET_STUDENTS_FROM_SCHOOL, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getStudentsFromSchool(@RequestParam(name="schoolId") String schoolId) {
        try {
            //Get students in school district and filter any that already belong to team
            List<Student> students = userRepository.getStudentsFromSchool(schoolId);
            students = teamService.filterStudentsOnTeam(students, schoolId);

            return ResponseEntity.status(HttpStatus.OK).body(students);

        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }

    /**
     * Removes a specific user from the database
     * @param userId the id of the user you want to remove
     * @return the response of the user beign deleted or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= REMOVE_USER, method= RequestMethod.DELETE ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> removeUser(@PathVariable("userId") String userId) {
        try {
            if(! MongoIdVerifier.isValidMongoId(userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid user ID.");
            }

            boolean removed = userRepository.removeUser(userId);

            if (removed){
                return ResponseEntity.status(HttpStatus.OK).body("User was removed.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User could not be removed, doesn't exist.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }



    /**
     * Removes a specific user from the database
     * @param studentId the id of the user you want to remove
     * @return the response of the user beign deleted or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= DELETE_STUDENT, method= RequestMethod.DELETE ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> deleteStudent(@PathVariable("studentId") String studentId) {
        try {
            if(! MongoIdVerifier.isValidMongoId(studentId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid user ID.");
            }

            Student student = (Student) userRepository.getUser(studentId);

            if(student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student does not exist.");
            }

            boolean studentNotReferenced = teamService.removeStudentFromAnyTeam(student);

            if( ! studentNotReferenced) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("The student is referenced and this could not be resolved.");
            }

            boolean deleteResult = userRepository.removeUser(student);

            if (deleteResult){
                return ResponseEntity.status(HttpStatus.OK).body("User was removed.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User could not be removed, doesn't exist.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }

    /**
     * Updates a specific user to the database
     * @return the response of the user being updated or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= UPDATE_USER, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateUser(@RequestBody String updatedUserJson, HttpServletRequest request) {
        try {

            HttpSession userSession = request.getSession(false);
            AUser user = (AUser) userSession.getAttribute("user");

            boolean update = userRepository.updateUser(user, updatedUserJson);

            if (update){
                return ResponseEntity.status(HttpStatus.OK).body("User was updated.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User could not be updated, doesn't exist.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }

    /**
     * Changes a user's password
     * @param passwordJson the new password
     * @param request the session credentials which includes the user object
     * @return the response of the user being updated or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= CHANGE_PASSWORD, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> changePassword(@RequestBody String passwordJson, HttpServletRequest request) {

        logger.info("Changing password");
        try {
            String passwordString = JsonHelper.getJsonString(passwordJson, "password");

            HttpSession userSession = request.getSession(false);
            AUser user = (AUser) userSession.getAttribute("user");

            boolean update = userRepository.changePassword(user, passwordString);

            if (update){
                return ResponseEntity.status(HttpStatus.OK).body("User was updated.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User could not be updated, doesn't exist.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }


    /**
     * Removes all of the users from the database
     * URI is /sweng500/removeUsers
     * @return true if the code is executed
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= REMOVE_USERS, method= RequestMethod.DELETE ,produces={MediaType.APPLICATION_JSON_VALUE})
    public boolean removeAllUsers() {
        userRepository.removeAllUsers();
        return true;
    }

    /**
     * The POST request for checking if email username is already in the system
     * URI is /sweng500/emailAvailable
     * @param emailAddressJson the email address to be checked in the system
     * @return STATUS 200 if email address is AVAILABLE, STATUS 409 if it is TAKEN, STATUS 400 if bad JSON provided
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= EMAIL_AVAILABLE, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> emailAvailable(@RequestBody String emailAddressJson) {
        try {
            String email = JsonHelper.getJsonString(emailAddressJson, "emailAddress");

            if (!userRepository.emailUsed(email)) {
                return ResponseEntity.status(HttpStatus.OK).body("Email Address is Available.");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email Address is Not Available.");
            }
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data.");
        }
    }

    /**
     * The POST request for adding a user to the system
     * URI is /sweng500/addUser
     * @param userType the string of the type of user to create, matches that from {@link edu.pennstate.science_olympiad.people.IUserTypes}
     * @param schoolID (not required) the string of the schoolID for the School to add to the User if they are of type {@link edu.pennstate.science_olympiad.people.Coach} or {@link edu.pennstate.science_olympiad.people.Student}
     * @param userJson the JSON of all of the user's data
     * @return STATUS 200 if user is successfully added, STATUS 207 if user created but could not be added to school,
     *                      STATUS 409 if user was not created, STATUS 400 if bad JSON provided
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_USER, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addUser(@RequestParam(name="userType") String userType, @RequestParam(name="schoolID",required = false) String schoolID, @RequestBody String userJson) {
        try {
            AUser userToAdd;
            School foundSchool = null;
            String userTypeString = "";

            try {
                Gson gson = new Gson();

                if (userType.equalsIgnoreCase(IUserTypes.ADMIN)){
                    userToAdd = gson.fromJson(userJson, Admin.class);
                    userTypeString = "Admin";
                } else if (userType.equalsIgnoreCase(IUserTypes.COACH)){
                    userToAdd = gson.fromJson(userJson, Coach.class);
                    userTypeString = "Coach";
                } else if (userType.equalsIgnoreCase(IUserTypes.JUDGE)){
                    userToAdd = gson.fromJson(userJson, Judge.class);
                    userTypeString = "Judge";
                } else if (userType.equalsIgnoreCase(IUserTypes.STUDENT)){
                    userToAdd = gson.fromJson(userJson, Student.class);
                    userTypeString = "Student";
                } else {
                    userToAdd = null;
                }
            } catch(Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
            }

            if(userToAdd == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, invalid user type.");
            }

            if(userToAdd instanceof Coach || userToAdd instanceof Student) {

                foundSchool = schoolRepository.getSchool(schoolID);

                if (foundSchool == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid school ID.");
                }
            }

            //If no password provided - set a default
            //TODO: Generate random password which can then be part of a sent email to the user?
            String passwordString = JsonHelper.getJsonString(userJson, "password");
            if(passwordString == null) {
                if (userToAdd.getEmailAddress().contains("@test.com"))
                    userToAdd.setPasswordPlainText("Password1");
                else {
                    String randomString = new RandomString(20).nextString();
                    userToAdd.setPasswordPlainText(randomString);
                    userRepository.signUpEmail(userToAdd.getEmailAddress(), userToAdd.getFirstName(),
                            userTypeString, randomString);
                }
            }

            boolean userAdded = userRepository.addUser(userToAdd);

            if (userAdded) {
                if(userToAdd instanceof Coach || userToAdd instanceof Student) {
                    boolean schoolAddedToUser = userRepository.addSchoolToUser(foundSchool, userToAdd);

                    if(!schoolAddedToUser) {
                        ResponseEntity.status(HttpStatus.MULTI_STATUS).body("User created successfully but could not be added to School.");
                    }
                }

                return ResponseEntity.status(HttpStatus.OK).body("User created successfully");
            }
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }

//    /**
//     * The POST request for adding a a coach to a student
//     * URI is /sweng500/addCoachToStudent
//     * @param coachIdJson the JSON of the coach to add
//     * @param studentIdJson the JSON of the student who is getting a new coach
//     * @return STATUS 200 if coach is successfully set, STATUS 409 if coach was not set, STATUS 400 if bad JSON provided
//     */
//    @CrossOrigin(origins = "*")
//    @RequestMapping(value= ADD_COACH_TO_STUDENT, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
//    public ResponseEntity<?> addCoachToStudent(@RequestBody String coachIdJson, @RequestBody String studentIdJson) {
//        try {
//
//            Coach coach = (Coach) userRepository.getUser(JsonHelper.getIdFromJson(coachIdJson));
//            Student student = (Student) userRepository.getUser(JsonHelper.getIdFromJson(studentIdJson));
//
//            boolean added = userRepository.addCoachToStudent(coach, student);
//
//            if (added)
//                return ResponseEntity.status(HttpStatus.OK).body("Coach was added to student.");
//            else
//                return ResponseEntity.status(HttpStatus.CONFLICT).body("Coach was not added.");
//
//        } catch(Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
//        }
//    }

    /**
     * The POST request for adding a a coach to a student
     * URI is /sweng500/addSchoolToUser
     * @param schoolIdJson the id in JSON format of the school to add to the coach
     * @param userIdJson the id in JSON format of the coach to add the school
     * @return STATUS 200 if school is successfully set, STATUS 409 if school was not set, STATUS 400 if bad JSON provided
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_SCHOOL_TO_USER , method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addSchoolToUser(@RequestBody String schoolIdJson, @RequestBody String userIdJson) {
        try {

            AUser user = userRepository.getUser(JsonHelper.getIdFromJson(userIdJson));
            School school = schoolRepository.getSchool(JsonHelper.getIdFromJson(schoolIdJson));

            boolean added = userRepository.addSchoolToUser(school, user);

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("The user was assigned to the specified school.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("The user was NOT assigned to the specified school.  Only Student/Coach can be assigned a school.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    /**
     * Validates a user's password is correct
     * @return the response of the password being reset or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= VALIDATE, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> validatePassword(@RequestBody String passwordJson, HttpServletRequest request) {
        try {

            String passwordString = JsonHelper.getJsonString(passwordJson, "password");

            logger.info("Attempting to validate with: -" + passwordString+"-");

            HttpSession userSession = request.getSession(false);
            AUser user = (AUser) userSession.getAttribute("user");

            boolean valid = user.isPasswordEqual(passwordString);
//            boolean valid = user.getPassword().equals(user.get_SHA_512_SecurePassword(passwordString));
//            boolean valid = userRepository.validate(userId, passwordString);

            if (valid){
                return ResponseEntity.status(HttpStatus.OK).body("Validated");}
            else
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid password");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }

    /**
     * Resets a user's password
     * @param emailAddressJson the email address of the user whos password is being reset
     * @return the response of the password being reset or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= RESET_PASSWORD, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> resetUserPassword(@RequestBody String emailAddressJson) {
        try {
            logger.info("Hit reset password endpoint with json: " + emailAddressJson);
            boolean sent = userRepository.resetPassword(JsonHelper.getJsonString(emailAddressJson, "emailAddress"));

            if (sent){
                return ResponseEntity.status(HttpStatus.OK).body("User's password was reset.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Password could not be reset.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }
//    /**
//     * Resets a user's password
//     * @param emailAddress the email address of the user whos password is being reset
//     * @return the response of the password being reset or not
//     */
//    @CrossOrigin(origins = "*")
//    @RequestMapping(value= RESET_PASSWORD+"/{emailAddress}", method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
//    public ResponseEntity<?> resetUserPassword(@PathVariable("emailAddress") String emailAddress) {
//        try {
//            logger.info("Hit reset password endpoint");
//            boolean sent = userRepository.resetPassword(emailAddress);
//
//            if (sent){
//                return ResponseEntity.status(HttpStatus.OK).body("User's password was reset.");}
//            else
//                return ResponseEntity.status(HttpStatus.CONFLICT).body("Password could not be reset.");
//
//        } catch(Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
//        }
//    }
}
