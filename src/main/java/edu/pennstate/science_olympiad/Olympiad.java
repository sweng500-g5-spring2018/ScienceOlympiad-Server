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

    private static Olympiad INSTANCE;

    public static Olympiad getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Olympiad();
        return INSTANCE;
    }

    private Olympiad() {
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

    public void addAdmin(Admin admin) {
        this.admins.add(admin);
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public void addTeam(Team team) {
        this.teams.add(team);
    }
}
