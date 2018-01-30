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
 * This  is one of the events in the {@link edu.pennstate.science_olympiad.Olympiad} Competition
 */
@Document(collection="events")
public class Event {

    @Id
    public String id;

    private String name;
    private String description;
    private Location location;
    private Date startTime;
    private Date endTime;
    //event most likely has multiple judges

    private List<Judge_Event> judge_events;
    //may not need this becuase when we add a team to this event we will create it in the service
    private List<Team_Event> team_events;

    public Event() {

    }
    public Event(String name) {
        //Olympiad.getInstance().addEvent(this);
        this.name = name;
       // judge_events = new ArrayList<Judge_Event>();
       // team_events = new ArrayList<Team_Event>();
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public void addJudgeToEvent(Judge judge) {
        judge_events.add(new Judge_Event(judge, this));
    }

    public boolean removeJudge(Judge judge) {
        for (Iterator<Judge_Event> judge_event_Iter = judge_events.iterator(); judge_event_Iter.hasNext();) {
            if (judge_event_Iter.next().getJudge() == judge) {
                judge_event_Iter.remove();
                return true;
            }
        }
        return false;
    }

    public void addTeamToEvent(Team team) {
        team_events.add(new Team_Event(team, this));
    }

    public boolean removeTeam(Team team) {
        for (Iterator<Team_Event> team_event_Iter = team_events.iterator(); team_event_Iter.hasNext();) {
            if (team_event_Iter.next().getTeam() == team) {
                Olympiad.getInstance().getTeams().remove(team);
                team_event_Iter.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
