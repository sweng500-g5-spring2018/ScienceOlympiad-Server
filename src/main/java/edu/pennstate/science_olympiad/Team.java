package edu.pennstate.science_olympiad;

import edu.pennstate.science_olympiad.many_to_many.Team_Event;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Student;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is one of the participants/ competitors in the {@link edu.pennstate.science_olympiad.Olympiad}. A team is
 * sometimes comprised of one {@link edu.pennstate.science_olympiad.people.Student} but other times it will have more.
 */
@Document
public class Team {

    @Id
    public String id;
    //The advisor or coach to the team
    private Coach coach;

    //All of the members of this team
    private List<Student> students;

    private School school;

    // This is a list of Pairs. Each Pair is an event paired with the team's score at that event.
    private List<Team_Event> team_events;

    public Team(Coach coach) {
        Olympiad.getInstance().addTeam(this);
        this.coach = coach;
        students = new ArrayList<Student>();
        team_events = new ArrayList<Team_Event>();
    }

    public Coach getCoach() {
        return coach;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public List<Team_Event> getTeam_events() {
        return team_events;
    }

    public void addEvent(Event event) {
        team_events.add(new Team_Event(this, event));
    }

    public boolean removeEvent(Event event) {
        for (Iterator<Team_Event> team_event_Iter = team_events.iterator(); team_event_Iter.hasNext();) {
            if (team_event_Iter.next().getEvent() == event) {
                Olympiad.getInstance().getEvents().remove(event);
                team_event_Iter.remove();
                return true;
            }
        }
        return false;
    }
}
