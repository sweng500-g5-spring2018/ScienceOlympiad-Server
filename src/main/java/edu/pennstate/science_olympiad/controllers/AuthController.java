package edu.pennstate.science_olympiad.controllers;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.pennstate.science_olympiad.helpers.request.LoginJsonHelper;
import edu.pennstate.science_olympiad.helpers.response.LoginResponseHelper;
import edu.pennstate.science_olympiad.people.*;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This class contains all of the endpoints that can be called to add, remove, or login as a user of the system
 */
@RestController
public class AuthController {
    @Autowired
    UserRepository userRepository;

    /**
     * POST Request for authenticating a user with the system.  Establishes a session if they are valid.
     *
     * URI: sweng500/auth/login
     * @param loginJson JSON containing login credentials
     * @param request Incoming Servlet request
     * @return JSON containing user information on SUCCESS
     */
//    @CrossOrigin(origins = {"http://localhost:3000", "http://sweng500.com", "https://localhost:3000", "https://sweng500.com"})
    @CrossOrigin(origins = "*")
    @RequestMapping(value="auth/login",method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> login(@RequestBody String loginJson, HttpServletRequest request) {
        Gson gson = new Gson();
        LoginJsonHelper helper = gson.fromJson(loginJson, LoginJsonHelper.class);

        if(helper.getEmailAddress() == null || helper.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }

        AUser userFound = (AUser) userRepository.getUser(helper);

        if(userFound != null) {
            HttpSession session = request.getSession();

            if(!session.isNew()) {
                session.invalidate();

                session = request.getSession();
            }

            session.setAttribute("user", userFound);

            LoginResponseHelper loginResponse = new LoginResponseHelper(userFound.getEmailAddress(),
                                                                        "NULLTYPEHELPER",
                                                                        session.getId());

            System.out.println("SUCCESSFULLY LOGGED IN USER " + userFound.getEmailAddress());
            return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The Email Address or Password provided is incorrect");
        }
    }


    /**
     * POST Request to terminate a user's session and log them out of the system
     *
     * URI: sweng500/auth/logout
     * @param logoutJson JSON containing the user's emailAddress
     * @param request Incoming Servlet request
     * @return String revealing the status of the operations
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value="auth/logout",method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> logout(@RequestBody String logoutJson, HttpServletRequest request) {
        String email = null;

        try {
            JsonParser parser = new JsonParser();
            JsonObject jsonObj = parser.parse(logoutJson).getAsJsonObject();

            email = jsonObj.get("emailAddress").getAsString();

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }

        HttpSession userSession = request.getSession(false);

        if(userSession == null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Session already terminated.");
        }

        AUser sessionedUser = (AUser) userSession.getAttribute("user");

        if(sessionedUser == null) {
            return ResponseEntity.status(HttpStatus.OK).body("Session already terminated.");
        }

        if(sessionedUser.getEmailAddress().equals(email)) {
            userSession.invalidate();

            return ResponseEntity.status(HttpStatus.OK).body("Successfully logged out.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data.  You're not who you say you are...");
        }
    }

}
