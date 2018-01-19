package edu.pennstate.science_olympiad;

import edu.pennstate.science_olympiad.people.Judge;
import edu.pennstate.science_olympiad.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This  is one of the events in the {@link edu.pennstate.science_olympiad.Olympiad} Competition
 */
public class Event {

    private String name;
    private String description;
    private Location location;
    private Date time;
    private Judge judge;
    private List<Pair<Team, PlaceEnum>> teamsAndPlacement;

    public Event() {
        teamsAndPlacement = new ArrayList<Pair<Team, PlaceEnum>>();
    }

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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Judge getJudge() {
        return judge;
    }

    public void setJudge(Judge judge) {
        this.judge = judge;
    }

    public List<Pair<Team, PlaceEnum>> getTeamsAndPlacement() {
        return teamsAndPlacement;
    }

    public void addTeamAndPlacement(Pair<Team, PlaceEnum> teamPlace) {
        getTeamsAndPlacement().add(teamPlace);
    }

    public void setPlacementForTeam(Team team, PlaceEnum placeEnum) {
        for (Pair<Team, PlaceEnum> pair : getTeamsAndPlacement()) {
            if (pair.getLeft() == team) {
                pair.setRight(placeEnum);
            }
        }
    }

    public void setTeamsAndPlacement(List<Pair<Team, PlaceEnum>> teamsAndPlacement) {
        this.teamsAndPlacement = teamsAndPlacement;
    }
}
