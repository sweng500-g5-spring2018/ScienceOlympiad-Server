package edu.pennstate.science_olympiad.controllers;


import com.google.gson.Gson;
import edu.pennstate.science_olympiad.School;
import edu.pennstate.science_olympiad.URIConstants;
import edu.pennstate.science_olympiad.helpers.mongo.MongoIdVerifier;
import edu.pennstate.science_olympiad.people.*;
import edu.pennstate.science_olympiad.repositories.SchoolRepository;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import edu.pennstate.science_olympiad.util.Pair;
import edu.pennstate.science_olympiad.helpers.json.JsonHelper;
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
    @Autowired
    UserRepository userRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    SchoolRepository schoolRepository;

    /**
     * URI is /sweng500/createTestUser
     * @return success, true pair
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= TEST_USER ,method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public Object addUser() {
        // log.info(">>addUser()");
        Admin admin = (Admin) UserFactory.getInstance().createUser("admin");
        admin.setFirstName("Kyle");
        admin.setLastName("H");
        admin.setEmailAddress("test@email");
        admin.setPhoneNumber("+12345678910");
        userRepository.addUser(admin);

        Coach coach = (Coach) UserFactory.getInstance().createUser("coach");
        coach.setFirstName("Coach");
        coach.setLastName("Nixon");
        userRepository.addUser(coach);
        // log.info("<<addUser()");
        Pair response = new Pair("success","true");
        return response;
    }

    /**
     * Creates a test User and returns it to the requester
     * URI is /sweng500/users
     * @return Brandon's information
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= USERS, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public AUser getBrandon() {
        Admin admin = (Admin)UserFactory.getInstance().createUser("admin");
        admin.setFirstName("Brandon");
        admin.setLastName("Hessler");
        admin.setEmailAddress("PennState@brandonhessler.com");
        admin.setPassword("password");
        return admin;
    }

    /**
     * Returns a list of all of the users of the system
     * URI is /sweng500/allUsers
     * @return all of the users in the database in JSON form
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= ALL_USERS, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public List<AUser> getAllUsers() {
        return mongoTemplate.findAll(AUser.class);
    }

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
            List<Coach> judges = userRepository.getAllJudges();

            return ResponseEntity.status(HttpStatus.OK).body(judges);

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
     * Updates a specific user to the database
     * @param userId the id of the user you want to update
     * @return the response of the user being updated or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= UPDATE_USER, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateUser(@RequestParam(name="userType") String userType, @PathVariable("userId") String userId, @RequestBody String updatedUserJson) {
        try {
            if(! MongoIdVerifier.isValidMongoId(userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid user ID.");
            }

            boolean update = userRepository.updateUser(userType, userId, updatedUserJson);

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
     * @param userId the id of the user you want to update
     * @return the response of the user being updated or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= CHANGE_PASSWORD, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> changePassword(@PathVariable("userId") String userId, @RequestBody String passwordJson) {
        try {
            if(! MongoIdVerifier.isValidMongoId(userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid user ID.");
            }

            boolean update = userRepository.changePassword(userId, JsonHelper.getJsonString(passwordJson, "newPassword"));

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
     * @param schoolID (not required) the string of the schoolID for the School to add to the User if they are of type {@link edu.pennstate.science_olympiad.people.Coach}
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

            try {
                Gson gson = new Gson();

                if (userType.equalsIgnoreCase(IUserTypes.ADMIN)){
                    userToAdd = gson.fromJson(userJson, Admin.class);
                } else if (userType.equalsIgnoreCase(IUserTypes.COACH)){
                    userToAdd = gson.fromJson(userJson, Coach.class);
                } else if (userType.equalsIgnoreCase(IUserTypes.JUDGE)){
                    userToAdd = gson.fromJson(userJson, Judge.class);
                } else if (userType.equalsIgnoreCase(IUserTypes.STUDENT)){
                    userToAdd = gson.fromJson(userJson, Student.class);
                } else {
                    userToAdd = null;
                }
            } catch(Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
            }

            if(userToAdd == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, invalid user type.");
            }

            if(userToAdd instanceof Coach) {

                foundSchool = schoolRepository.getSchool(schoolID);

                if (foundSchool == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid school ID.");
                }
            }

            boolean userAdded = userRepository.addUser(userToAdd);

            if (userAdded) {
                if(userToAdd instanceof Coach) {
                    boolean schoolAddedToUser = userRepository.addSchoolToCoach(foundSchool, (Coach) userToAdd);

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

    /**
     * The POST request for adding a a coach to a student
     * URI is /sweng500/addCoachToStudent
     * @param coachIdJson the JSON of the coach to add
     * @param studentIdJson the JSON of the student who is getting a new coach
     * @return STATUS 200 if coach is successfully set, STATUS 409 if coach was not set, STATUS 400 if bad JSON provided
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_COACH_TO_STUDENT, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addCoachToStudent(@RequestBody String coachIdJson, @RequestBody String studentIdJson) {
        try {

            Coach coach = (Coach) userRepository.getUser(JsonHelper.getIdFromJson(coachIdJson));
            Student student = (Student) userRepository.getUser(JsonHelper.getIdFromJson(studentIdJson));

            boolean added = userRepository.addCoachToStudent(coach, student);

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("Coach was added to student.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Coach was not added.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    /**
     * The POST request for adding a a coach to a student
     * URI is /sweng500/addSchoolToCoach
     * @param schoolIdJson the id in JSON format of the school to add to the coach
     * @param coachIdJson the id in JSON format of the coach to add the school
     * @return STATUS 200 if school is successfully set, STATUS 409 if school was not set, STATUS 400 if bad JSON provided
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_SCHOOL_TO_COACH , method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addSchoolToCoach(@RequestBody String schoolIdJson, @RequestBody String coachIdJson) {
        try {

            Coach coach = (Coach) userRepository.getUser(JsonHelper.getIdFromJson(coachIdJson));
            School school = schoolRepository.getSchool(JsonHelper.getIdFromJson(schoolIdJson));

            boolean added = userRepository.addSchoolToCoach(school, coach);

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("School was to coach.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("School was not set for the coach.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    /**
     * Resets a user's password
     * @param userId the id of the user whos password is being reset
     * @return the response of the password being reset or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= RESET_PASSWORD, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> resetUserPassword(@PathVariable("userId") String userId) {
        try {
            if(! MongoIdVerifier.isValidMongoId(userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid user ID.");
            }

            boolean removed = userRepository.resetPassword(userId);

            if (removed){
                return ResponseEntity.status(HttpStatus.OK).body("User was removed.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User could not be removed, doesn't exist.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }

    /**
     * Validates a user's password is correct
     * @param userId the id of the user whos password is being reset
     * @return the response of the password being reset or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= VALIDATE, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> validatePassword(@PathVariable("userId") String userId, @RequestBody String passwordJson) {
        try {
            if(! MongoIdVerifier.isValidMongoId(userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid user ID.");
            }

            boolean valid = userRepository.validate(userId, JsonHelper.getJsonString(passwordJson, "password"));

            if (valid){
                return ResponseEntity.status(HttpStatus.OK).body("Validated");}
            else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalidated");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }

}
