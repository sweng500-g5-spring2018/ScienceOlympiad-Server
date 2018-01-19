package edu.pennstate.science_olympiad;

import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Student;

import java.util.List;

public class School {
    private String schoolName;
    private String schoolContact;
    private String schoolContactPhone;
    private List<Coach> coaches;
    private List<Student> students;

    public School(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolContact() {
        return schoolContact;
    }

    public void setSchoolContact(String schoolContact) {
        this.schoolContact = schoolContact;
    }

    public String getSchoolContactPhone() {
        return schoolContactPhone;
    }

    public void setSchoolContactPhone(String schoolContactPhone) {
        this.schoolContactPhone = schoolContactPhone;
    }

    public List<Coach> getCoaches() {
        return coaches;
    }

    public void addCoach(Coach coach) {
        this.coaches.add(coach);
    }

    public void setCoaches(List<Coach> coaches) {
        this.coaches = coaches;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student) {
        this.students.add(student);
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
