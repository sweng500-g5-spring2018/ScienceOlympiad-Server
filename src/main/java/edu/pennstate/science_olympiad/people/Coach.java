package edu.pennstate.science_olympiad.people;


import edu.pennstate.science_olympiad.School;

import java.util.ArrayList;
import java.util.List;

public class Coach extends AUser {

    private School school;
    private List<Student> students;

    public Coach() {
        students = new ArrayList<Student>();
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
