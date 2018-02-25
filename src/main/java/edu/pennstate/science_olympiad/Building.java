package edu.pennstate.science_olympiad;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This  describes a building that a {@link edu.pennstate.science_olympiad.people.AUser} or
 * {@link edu.pennstate.science_olympiad.Event} may be at, as well as its name.
 */
@Document
public class Building {

    @Id
    public String id;
    private String building;
    private Double lat;
    private Double lng;

    public Building() {
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void copyInfo(Building building) {
        this.building = building.getBuilding();
        this.lat = building.getLat();
        this.lng = building.getLng();
    }
}
