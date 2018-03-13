package edu.pennstate.science_olympiad.people;

/**
 * A student is the participant in the Olympiad and the
 *  {@link edu.pennstate.science_olympiad.Event}s therein. There will be more of these actors than any other.
 */
public class Student extends AUser {
    //This is a variable to keep track of whether a student has opted out of SMS messages
    private Coach coach;

    public Student() {
        super();
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    @Override
    public void copyInfo(AUser aUser) {
        super.copyInfo(aUser);
        this.coach = ((Student)aUser).getCoach();
    }
}
