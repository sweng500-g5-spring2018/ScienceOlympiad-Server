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
@Document(collection="team_events")
public class Team_Event {
    @Id
    public String id;
    @DBRef
    private Team team;
    @DBRef
    private Event event;
    private Double score;

    public Team_Event(Team team, Event event) {
        this.team = team;
        this.event = event;
    }

    public Team getTeam() {
        return team;
    }
    public void setTeam(Team t)  {this.team =t;}

    public Event getEvent() {
        return event;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
