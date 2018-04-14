package edu.pennstate.science_olympiad.many_to_many;

import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.Team;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This is a bridge class for the database between
 * {@link edu.pennstate.science_olympiad.Team}s and {@link edu.pennstate.science_olympiad.Event}s
 * and this also records the team's score for the event
 */
@Document(collection = "team_events")
public class Team_Event {
    @Id
    public String id;
    private String teamId;
    private String eventId;
    private String teamName;
    private String eventName;
    private Double score;

    public Team_Event(String teamId, String eventId, String teamName, String eventName) {
        this.teamId = teamId;
        this.eventId = eventId;
        this.teamName = teamName;
        this.eventName = eventName;
    }

    public String getTeamId() {
        return this.teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getEventId() {
        return this.eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTeamName() { return this.teamName; }

    public String getEventName() { return this.eventName; }

    public void setTeamName(String teamName) { this.teamName = teamName; }

    public void setEventName(String eventName) { this.eventName = eventName; }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
