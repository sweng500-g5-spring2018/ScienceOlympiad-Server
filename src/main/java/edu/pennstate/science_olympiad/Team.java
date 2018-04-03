package edu.pennstate.science_olympiad;

import edu.pennstate.science_olympiad.many_to_many.Team_Event;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Student;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is one of the participants/ competitors in the Olympiad. A team is
 * sometimes comprised of one {@link edu.pennstate.science_olympiad.people.Student} but other times it will have more.
 */
@Document(collection="teams")
public class Team {

    @Id
    public String id;

    private String name;

    //The advisor or coach to the team
    @DBRef
    private Coach coach;

    @DBRef
    private School school;

    //All of the members of this team
    @DBRef
    private List<Student> students;

    // This is a list of Pairs. Each Pair is an event paired with the team's score at that event.
    private List<Team_Event> team_events;

    public Team(Coach coach) {
        this.coach = coach;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public List<Student> getStudents() {
        if (students == null)
            students = new ArrayList<Student>();
        return students;
    }

    public void addStudents(List<Student> students) {
        this.students.addAll(students);
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
        if (team_events == null)
            team_events = new ArrayList<Team_Event>();

        return team_events;
    }

    public void addEvent(Event event) {
        team_events.add(new Team_Event(this, event));
    }

    public boolean removeEvent(Event event) {
        for (Iterator<Team_Event> team_event_Iter = team_events.iterator(); team_event_Iter.hasNext();) {
            if (team_event_Iter.next().getEvent() == event) {
                team_event_Iter.remove();
                return true;
            }
        }
        return false;
    }

    public void copyInfo(Team team) {
        this.coach = team.getCoach();
        this.students = team.getStudents();
        this.school = team.getSchool();
        this.team_events = team.getTeam_events();
    }
}
