package edu.pennstate.science_olympiad;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This  describes a building that a {@link edu.pennstate.science_olympiad.people.AUser} or
 * {@link Event} may be at, as well as its name.
 */
@Document
public class Room {

    @Id
    public String id;

    private String roomName;
    private String buildingID;
    private int capacity;

    public Room() {
    }

    public void setRoomName(String roomName) { this.roomName = roomName; }

    public void setBuildingID(String buildingID) { this.buildingID = buildingID; }

    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getRoomName() { return roomName; }

    public String getBuildingID() { return buildingID; }

    public int getCapacity() { return capacity; }

    public void copyInfo(Room r) {
        this.roomName = r.getRoomName();
        this.buildingID = r.getBuildingID();
        this.capacity = r.getCapacity();
    }
}