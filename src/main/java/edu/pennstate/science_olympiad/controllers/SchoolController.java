package edu.pennstate.science_olympiad.controllers;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.School;
import edu.pennstate.science_olympiad.URIConstants;
import edu.pennstate.science_olympiad.helpers.mongo.MongoIdVerifier;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.repositories.SchoolRepository;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * This contains all of the endpoints on the web server for managing School objects
 */
@RestController
public class SchoolController implements URIConstants{

    @Autowired
    SchoolRepository schoolRepository;
    @Autowired
    UserRepository userRepository;

    @CrossOrigin(origins = "*")
    @RequestMapping(value= GET_SCHOOLS ,method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public List<School> getSchools() {
        return schoolRepository.getSchools();
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_SCHOOL , method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addSchool(@RequestBody String schoolJson) {
        try {
            Gson gson = new Gson();
            School school = gson.fromJson(schoolJson, School.class);

            boolean added = schoolRepository.addNewSchool(school);

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("School was added.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("School could not be added, already exists.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_SCHOOL_WITH_COACH, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addSchoolWithCoach(@RequestBody String schoolJson, @RequestBody String coachJson) {
        try {
            Gson gson = new Gson();
            Coach coach = gson.fromJson(coachJson, Coach.class);
            School school = gson.fromJson(schoolJson, School.class);

            boolean added = schoolRepository.addNewSchool(school);
            if (added) {
                added = userRepository.addSchoolToCoach(school, coach);
            }

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("School was added and assigned to coach.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("School could not be added, or not assigned to coach.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value= URIConstants.REMOVE_SCHOOL, method= RequestMethod.DELETE ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> removeSchool(@PathVariable("schoolID") String schoolID) {
        try {
            if(! MongoIdVerifier.isValidMongoId(schoolID)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid school ID.");
            }

            boolean removed = schoolRepository.removeSchool(schoolID);

            if (removed){
                return ResponseEntity.status(HttpStatus.OK).body("School was removed.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("School could not be removed, doesn't exist.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }
}
