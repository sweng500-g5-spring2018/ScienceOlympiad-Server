package edu.pennstate.science_olympiad.controllers;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.pennstate.science_olympiad.School;
import edu.pennstate.science_olympiad.URIConstants;
import edu.pennstate.science_olympiad.helpers.mongo.MongoIdVerifier;
import edu.pennstate.science_olympiad.people.*;
import edu.pennstate.science_olympiad.repositories.SchoolRepository;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import edu.pennstate.science_olympiad.sms.CustomPhoneNumber;
import edu.pennstate.science_olympiad.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        admin.setPhoneNumber(new CustomPhoneNumber("+12345678910"));
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
     * Removes all of the users from the database
     * URI is /sweng500/removeUsers
     * @return true if the code is executed
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= REMOVE_USERS, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
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
            JsonParser parser = new JsonParser();
            JsonObject jsonObj = parser.parse(emailAddressJson).getAsJsonObject();
            String email = jsonObj.get("emailAddress").getAsString();

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
     * @param coachJson the JSON of the coach to add
     * @param studentJson the JSON of the student who is getting a new coach
     * @return STATUS 200 if coach is successfully set, STATUS 409 if coach was not set, STATUS 400 if bad JSON provided
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_COACH_TO_STUDENT, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addCoachToStudent(@RequestBody String coachJson, @RequestBody String studentJson) {
        try {
            Gson gson = new Gson();
            Coach coach = gson.fromJson(coachJson, Coach.class);
            Student student = gson.fromJson(studentJson, Student.class);

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
     * @param schoolJson the JSON of the school to add to the coach
     * @param coachJson the JSON of the coach to add the school
     * @return STATUS 200 if school is successfully set, STATUS 409 if school was not set, STATUS 400 if bad JSON provided
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_SCHOOL_TO_COACH , method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addSchoolToCoach(@RequestBody String schoolJson, @RequestBody String coachJson) {
        try {
            Gson gson = new Gson();
            Coach coach = gson.fromJson(coachJson, Coach.class);
            School school = gson.fromJson(schoolJson, School.class);

            boolean added = userRepository.addSchoolToCoach(school, coach);

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("School was to coach.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("School was not set for the coach.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

}
