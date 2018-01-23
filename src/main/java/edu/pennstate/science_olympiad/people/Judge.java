package edu.pennstate.science_olympiad.people;

import edu.pennstate.science_olympiad.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * A judge is someone who is judging one of the {@link edu.pennstate.science_olympiad.Event}s
 */
public class Judge extends AUser {

    private List<Event> events;

    public Judge() {
        super();
        events = new ArrayList<Event>();
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
