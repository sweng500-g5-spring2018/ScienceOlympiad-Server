package edu.pennstate.science_olympiad.people;

/**
 * A student is the participant in the Olympiad and the
 *  {@link edu.pennstate.science_olympiad.Event}s therein. There will be more of these actors than any other.
 */
public class Student extends AUser {
    //This is a variable to keep track of whether a student has opted out of SMS messages
    private boolean hasOptedOut = false;
    private Coach coach;

    public Student() {
        super();
    }

    public boolean hasOptedOut() {
        return hasOptedOut;
    }

    public void setHasOptedOut(boolean hasOptedOut) {
        this.hasOptedOut = hasOptedOut;
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
        this.hasOptedOut = ((Student)aUser).hasOptedOut;
        this.coach = ((Student)aUser).getCoach();
    }
}
