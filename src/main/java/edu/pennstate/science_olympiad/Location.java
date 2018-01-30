package edu.pennstate.science_olympiad;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This  describes a location that a {@link edu.pennstate.science_olympiad.people.AUser} or
 * {@link edu.pennstate.science_olympiad.Event} may be at, as well as its name.
 */
@Document
public class Location {

    @Id
    public String id;

    private String building;
    private String roomNumber;
    private Double latitiude;
    private Double longitude;

    public Location() {
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Double getLatitiude() {
        return latitiude;
    }

    public void setLatitiude(Double latitiude) {
        this.latitiude = latitiude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
