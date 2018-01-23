package edu.pennstate.science_olympiad;

import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Student;
import edu.pennstate.science_olympiad.util.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This is one of the participants/ competitors in the {@link edu.pennstate.science_olympiad.Olympiad}. A team is
 * sometimes comprised of one {@link edu.pennstate.science_olympiad.people.Student} but other times it will have more.
 */
public class Team {

    //The advisor or coach to the team
    private Coach coach;

    //All of the members of this team
    private List<Student> students;

    // This is a list of Pairs. Each Pair is an event paired with the team's score at that event.
    private List<Pair<Event, Double>> eventScoreList;

    public Team() {
        students = new ArrayList<Student>();
        eventScoreList = new ArrayList<Pair<Event, Double>>();
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Pair<Event, Double>> getEventScoreList() {
        return eventScoreList;
    }

    public void setEventScoreList(List<Pair<Event, Double>> eventScoreList) {
        this.eventScoreList = eventScoreList;
    }
}
