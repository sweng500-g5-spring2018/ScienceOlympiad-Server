package edu.pennstate.science_olympiad.repositories;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.Room;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("roomRepository")

public class RoomRepository {
    Log logger = LogFactory.getLog(getClass());

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * Gets all of the buildings from the database
     * @return all of the buildings
     */
    public List<Room> getAllRooms() {
        return mongoTemplate.findAll(Room.class);
    }


    /**
     * Gets all of the rooms from the database
     * @param buildingID The build to retrieve the rooms for
     * @return all of the rooms
     */
    public List<Room> getBuildingRooms(String buildingID) {

        Query query = new Query();
        query.addCriteria(Criteria.where("buildingID").is(buildingID));
        List<Room> dbRoom = mongoTemplate.find(query, Room.class);

        if (dbRoom != null) {
            logger.info("Found rooms");
            return dbRoom;
        }
        return null;
    }

    /**
     * Retrieves a room from the database
     * @param roomID The room to retrieve
     * @return the room object if found; or null
     */
    public Room getRoom(String roomID) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("_id").is(roomID));
        Room dbRoom = mongoTemplate.findOne(singleQuery, Room.class);

        if (dbRoom != null) {
            return dbRoom;
        }
        return null;
    }

    /**
     * Adds a new room to the database
     * @param room The room to add
     * @return whether the room was added or not
     */
    public boolean addNewRoom(Room room) {
        Query query = new Query();
        query.addCriteria(Criteria.where("room").is(room.getRoomName()));
        boolean exists = mongoTemplate.exists(query, Room.class);

        if (!exists) {
            logger.info("Adding a new room");
            mongoTemplate.insert(room);
            return true;
        }
        logger.info("Room already exists");
        return false;
    }

    /**
     * Removes a room from the database
     * @param roomId The room to remove
     * @return whether the room was removed or not
     */
    public boolean removeRoom (String roomId) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("_id").is(roomId));
        Room dbRoom = mongoTemplate.findOne(singleQuery, Room.class);

        if (dbRoom != null) {
            mongoTemplate.remove(dbRoom);
            return true;
        }
        return false;
    }

    /**
     * updates a room in the database
     * @param roomId The room to update
     * @return whether the room was updated or not
     */
    public boolean updateRoom (String roomId, String roomJson) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("_id").is(roomId));
        Room dbRoom = mongoTemplate.findOne(singleQuery, Room.class);

        Gson gson = new Gson();
        Room tempBuilding = gson.fromJson(roomJson, Room.class);
        if (dbRoom != null) {
            dbRoom.copyInfo(tempBuilding);
            mongoTemplate.save(dbRoom);
            return true;
        }
        return false;
    }

}