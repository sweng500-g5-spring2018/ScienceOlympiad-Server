package edu.pennstate.science_olympiad.controllers;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.Building;
import edu.pennstate.science_olympiad.Room;
import edu.pennstate.science_olympiad.URIConstants;
import edu.pennstate.science_olympiad.helpers.mongo.MongoIdVerifier;
import edu.pennstate.science_olympiad.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This contains all of the endpoints on the web server for managing room objects
 */
@RestController
public class RoomController implements URIConstants{

    @Autowired
    RoomRepository roomRepository;

    @CrossOrigin(origins = "*")
    @RequestMapping(value= GET_ALL_ROOMS ,method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public List<Room> getAllRooms() {
        try {
            List<Room> rooms = roomRepository.getAllRooms();

            if (rooms.size() > 0)
                return rooms;
            else
                return null;

        } catch (Exception e) {
            return null;
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value= ADD_ROOM , method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addRoom(@RequestBody String roomJson) {
        try {
            Gson gson = new Gson();
            Room room = gson.fromJson(roomJson, Room.class);

            boolean added = roomRepository.addNewRoom(room);

            if (added)
                return ResponseEntity.status(HttpStatus.OK).body("Room was added.");
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Room could not be added, already exists.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request data, malformed JSON.");
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value= REMOVE_ROOM, method= RequestMethod.DELETE ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> removeRoom(@PathVariable("roomID") String roomID) {
        try {
            if(! MongoIdVerifier.isValidMongoId(roomID)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid room ID.");
            }

            boolean removed = roomRepository.removeRoom(roomID);

            if (removed){
                return ResponseEntity.status(HttpStatus.OK).body("Room was removed.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Room could not be removed, doesn't exist.");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }

    /**
     * Updates a specific school in the database
     * @param roomID the id of the school you want to update
     * @param roomJson the json of the info you want to update the school with
     * @return the response of the event being updated or not
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= UPDATE_ROOM, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateRoom(@PathVariable("roomID") String roomID, @RequestBody String roomJson) {
        try {
            if (!MongoIdVerifier.isValidMongoId(roomID)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request, invalid room ID.");
            }

            boolean update = roomRepository.updateRoom(roomID, roomJson);

            if (update) {
                return ResponseEntity.status(HttpStatus.OK).body("Room was updated.");
            } else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Room could not be updated, doesn't exist.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }
}