package edu.pennstate.science_olympiad;

import edu.pennstate.science_olympiad.people.Admin;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * An  Olympiad is a large science competition. There are multiple {@link edu.pennstate.science_olympiad.Event}s and
 * there are multiple {@link edu.pennstate.science_olympiad.Team}s competing.
 */
@Document
public class Olympiad {

    @Id
    public String id;

    // The administrator(s) for the events
    private List<Admin> admins;

    // All of the events that take place in the entire competition
    private List<Event> events;

    // All of the teams that are competing in the competition
    private List<Team> teams;

    public Olympiad() {
        admins = new ArrayList<Admin>();
        events = new ArrayList<Event>();
        teams = new ArrayList<Team>();
    }

    public List<Admin> getAdmins() {
        return admins;
    }

    public void setAdmins(List<Admin> admins) {
        this.admins = admins;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
