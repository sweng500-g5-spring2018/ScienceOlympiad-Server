package edu.pennstate.science_olympiad.controllers;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.pennstate.science_olympiad.LoginJsonHelper;
import edu.pennstate.science_olympiad.people.*;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import edu.pennstate.science_olympiad.sms.CustomPhoneNumber;
import edu.pennstate.science_olympiad.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import javax.xml.ws.Response;
import java.util.List;

/**
 * This class contains all of the endpoints that can be called to add, remove, or login as a user of the system
 */
@RestController
public class UsersController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * URI is /sweng500/createTestUser
     * @return success, true pair
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value="createTestUser",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
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
    @RequestMapping(value="users",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
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
    @RequestMapping(value="allUsers",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public List<AUser> getAllUsers() {
        return mongoTemplate.findAll(AUser.class);
    }

    /**
     * Removes all of the users from the database
     * URI is /sweng500/removeUsers
     * @return true if the code is executed
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value="removeUsers",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public boolean removeAllUsers() {
        userRepository.removeAllUsers();
        return true;
    }

    /**
     * The POST request for logging in as a user
     * URI is /sweng500/login
     * @param loginJson the login information in the form of a JSON object containing only the emailAddress and password
     *                  as contained in the {@link edu.pennstate.science_olympiad.LoginJsonHelper} object
     * @return either the User object if the login is successful, or null if otherwise
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value="login",method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public AUser login(@RequestBody String loginJson) {
        Gson gson = new Gson();
        LoginJsonHelper helper = gson.fromJson(loginJson, LoginJsonHelper.class);

        return userRepository.getUser(helper);
    }

    /**
     * The POST request for checking if email username is already in the system
     * URI is /sweng500/emailAvailable
     * @param emailAddressJson the email address to be checked in the system
     * @return STATUS 200 if email address is AVAILABLE, STATUS 409 if it is TAKEN, STATUS 400 if bad JSON provided
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value="emailAvailable",method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> emailAvailable(@RequestBody String emailAddressJson) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject jsonObj = parser.parse(emailAddressJson).getAsJsonObject();
            String email = jsonObj.get("email").getAsString();

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
     * @param userJson the JSON of all of the user's data
     * @return STATUS 200 if user is successfully added, STATUS 409 if user was not created, STATUS 400 if bad JSON provided
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value="addUser",method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addUser(@RequestParam String userType, @RequestBody String userJson) {
        try {
            Gson gson = new Gson();
            boolean added = false;
            if (userType.equalsIgnoreCase(IUserTypes.ADMIN)){
                Admin admin = gson.fromJson(userJson, Admin.class);
                added = userRepository.addUser(admin);

            } else if (userType.equalsIgnoreCase(IUserTypes.COACH)){
                Coach coach = gson.fromJson(userJson, Coach.class);
                added = userRepository.addUser(coach);

            } else if (userType.equalsIgnoreCase(IUserTypes.JUDGE)){
                Judge judge = gson.fromJson(userJson, Judge.class);
                added = userRepository.addUser(judge);

            } else if (userType.equalsIgnoreCase(IUserTypes.STUDENT)){
                Student student = gson.fromJson(userJson, Student.class);
                added = userRepository.addUser(student);

            }

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("User was added.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

}
