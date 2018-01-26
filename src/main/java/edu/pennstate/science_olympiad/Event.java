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
    private Date startTime;
    private Date endTime;
    //event most likely has multiple judges
    private List<Judge> judge;
    private List<Pair<Team, Double>> teamsAndScores;

    public Event() {
        teamsAndScores = new ArrayList<Pair<Team, Double>>();
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

    public List<Judge> getJudges() {
        return judge;
    }

    public void setJudge(List<Judge> judge) {
        this.judge = judge;
    }
    public void addJudge(Judge judge) {
        this.judge.add(judge);
    }

    public List<Pair<Team, Double>> getTeamsAndScores() {
        return teamsAndScores;
    }

    public void addTeamAndScore(Pair<Team, Double> teamScore) {
        getTeamsAndScores().add(teamScore);
    }

    public void setScoreForTeam(Team team, Double score) {
        for (Pair<Team, Double> pair : getTeamsAndScores()) {
            if (pair.getLeft() == team) {
                pair.setRight(score);
            }
        }
    }

    public void setTeamsAndScores(List<Pair<Team, Double>> teamsAndScores) {
        this.teamsAndScores = teamsAndScores;
    }
}
