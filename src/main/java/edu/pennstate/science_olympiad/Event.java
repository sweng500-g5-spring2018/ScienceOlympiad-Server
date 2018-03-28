package edu.pennstate.science_olympiad;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.many_to_many.Judge_Event;
import edu.pennstate.science_olympiad.many_to_many.Team_Event;
import edu.pennstate.science_olympiad.people.Judge;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This  is one of the events in the Olympiad Competition
 */
@Document(collection="events")
public class Event {

    @Id
    public String id;

    private String name;
    private String description;
    //private Building building;
    private Room room;
    private Date eventDate;
    private Date startTime;
    private Date endTime;

    public Event() {

    }

    public Event(String name) {
        this.name = name;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getId(){return id;}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Room getRoom() {
        return room;
    }

    public void setLocation(Room room) {
        this.room = room;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void copyInfo(Event event) {
        this.name = event.getName();
        this.description = event.getDescription();
        this.room = event.getRoom();
        this.eventDate = event.getEventDate();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
    }
}
