package edu.pennstate.science_olympiad.controllers;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.Building;
import edu.pennstate.science_olympiad.URIConstants;
import edu.pennstate.science_olympiad.helpers.mongo.MongoIdVerifier;
import edu.pennstate.science_olympiad.repositories.BuildingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This contains all of the endpoints on the web server for managing building objects
 */
@RestController
public class BuildingController implements URIConstants{

    @Autowired
    BuildingRepository buildingRepository;

    @CrossOrigin(origins = "*")
    @RequestMapping(value= GET_BUILDINGS ,method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public List<Building> getBuildings() {
        return buildingRepository.getAllBuildings();
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_BUILDING , method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addBuilding(@RequestBody String buildingJson) {
        try {
            Gson gson = new Gson();
            Building building = gson.fromJson(buildingJson, Building.class);

            if (building.getLat() != null && building.getLng() != null && building.getBuilding() != null) {

                boolean added = buildingRepository.addNewBuilding(building);

                if (added)
                    return ResponseEntity.status(HttpStatus.OK).body("Building was added.");
                else
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Building could not be added, already exists.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
            }
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Something went wrong.");
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value= REMOVE_BUILDING, method= RequestMethod.DELETE ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> removeSchool(@PathVariable("buildingID") String buildingID) {
        try {
            if(! MongoIdVerifier.isValidMongoId(buildingID)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid school ID.");
            }

            boolean removed = buildingRepository.removeBuilding(buildingID);

            if (removed){
                return ResponseEntity.status(HttpStatus.OK).body("Building was removed.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Building could not be removed, doesn't exist.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }

    /**
     * Updates a specific school in the database
     * @param buildingID the id of the school you want to update
     * @param buildingJson the json of the info you want to update the school with
     * @return the response of the event being updated or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= UPDATE_BUILDING, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateBuilding(@PathVariable("buildingID") String buildingID, @RequestBody String buildingJson) {
        try {
            if (!MongoIdVerifier.isValidMongoId(buildingID)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid building ID.");
            }

            boolean update = buildingRepository.updateBuilding(buildingID, buildingJson);

            if (update) {
                return ResponseEntity.status(HttpStatus.OK).body("Building was updated.");
            } else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("building could not be updated, doesn't exist.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }

    /**
     * Get a specific building
     * @param buildingID the id of the school you want to update
     * @return the response containing the building
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= GET_A_BUILDING, method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getABuilding(@PathVariable("buildingID") String buildingID) {

        if (!MongoIdVerifier.isValidMongoId(buildingID)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid building ID.");
        }

        Building building = buildingRepository.getBuilding(buildingID);
        if (building != null) {
            return ResponseEntity.status(HttpStatus.OK).body(building);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Building could not be found");
        }


    }
}
