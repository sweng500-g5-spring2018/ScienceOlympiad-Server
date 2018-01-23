package edu.pennstate.science_olympiad.people;


import edu.pennstate.science_olympiad.School;
import edu.pennstate.science_olympiad.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * A Coach is a representative of the school and is a chaperon for {@link edu.pennstate.science_olympiad.people.Student}s.
 * A Coach can have many students and many teams.
 */
public class Coach extends AUser {

    private School school;
    private List<Team> teams;
    private List<Student> students;

    public Coach() {
        super();
        teams = new ArrayList<Team>();
        students = new ArrayList<Student>();
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student) {
        getStudents().add(student);
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
