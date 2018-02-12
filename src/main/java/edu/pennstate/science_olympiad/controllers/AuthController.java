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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Response;
import java.util.List;

/**
 * This class contains all of the endpoints that can be called to add, remove, or login as a user of the system
 */
@RestController
public class AuthController {
    @Autowired
    UserRepository userRepository;


    /**
     * The POST request for logging in as a user
     * URI is /sweng500/login
     * @param loginJson the login information in the form of a JSON object containing only the emailAddress and password
     *                  as contained in the {@link LoginJsonHelper} object
     * @return either the User object if the login is successful, or null if otherwise
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value="auth/login",method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> login(@RequestBody String loginJson, HttpSession session) {
        Gson gson = new Gson();
        LoginJsonHelper helper = gson.fromJson(loginJson, LoginJsonHelper.class);

        if(helper.getEmailAddress() == null || helper.getPassword() == null) {
            session.invalidate();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }

        System.out.println("Email address to try: " + helper.getEmailAddress() );

        AUser userFound = userRepository.getUser(helper);

        System.out.println(userFound);

        if(userFound != null) {
            session.setAttribute("session", userFound);
            return ResponseEntity.status(HttpStatus.OK).body(userFound);
        } else {
            session.invalidate();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The Email Address or Password provided is incorrect");
        }
    }


    /**
     * The POST request for logging out as a user
     * URI is /sweng500/auth/logout
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value="auth/logout",method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> logout(@RequestBody String loginJson, HttpServletRequest request) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject jsonObj = parser.parse(loginJson).getAsJsonObject();

            String email = jsonObj.get("emailAddress").getAsString();

            System.out.println("Trying to logout: " + email);

            if (email == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
            } else {
                HttpSession userSession = request.getSession(false);
                if(userSession == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, invalid logout authorization.");
                }

                AUser sessionedUser = (AUser) userSession.getAttribute("session");

                if(sessionedUser.getEmailAddress().equals(email)) {
                    System.out.println("SUCCESSFULLY LOGGING OUT USER");
                    userSession.invalidate();

                    return ResponseEntity.status(HttpStatus.OK).body("Successfully logged out.");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, invalid logout authorization.");
                }
            }

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    /**
     * The POST request for checking if email username is already in the system
     * URI is /sweng500/emailAvailable
     * @param emailAddressJson the email address to be checked in the system
     * @return STATUS 200 if email address is AVAILABLE, STATUS 409 if it is TAKEN, STATUS 400 if bad JSON provided
     */
//    @CrossOrigin(origins = "*")
//    @RequestMapping(value="emailAvailable",method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
//    public ResponseEntity<?> emailAvailable2(@RequestBody String emailAddressJson) {
//        try {
//            JsonParser parser = new JsonParser();
//            JsonObject jsonObj = parser.parse(emailAddressJson).getAsJsonObject();
//            String email = jsonObj.get("email").getAsString();
//
//            if (!userRepository.emailUsed(email)) {
//                return ResponseEntity.status(HttpStatus.OK).body("Email Address is Available.");
//            } else {
//                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email Address is Not Available.");
//            }
//        } catch(Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data.");
//        }
//    }


}
