package edu.pennstate.science_olympiad.people;

import edu.pennstate.science_olympiad.School;

/**
 * A student is the participant in the Olympiad and the
 *  {@link edu.pennstate.science_olympiad.Event}s therein. There will be more of these actors than any other.
 */
public class Student extends AUser {
    private Coach coach;
    private School school;

    public Student() {
        super();
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public School getSchool() { return school; }

    public void setSchool(School school) { this.school = school; }

    @Override
    public void copyInfo(AUser aUser) {
        super.copyInfo(aUser);
        this.coach = ((Student)aUser).getCoach();
        this.school = ((Student) aUser).getSchool();
    }
}
