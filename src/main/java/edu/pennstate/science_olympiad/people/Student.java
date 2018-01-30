package edu.pennstate.science_olympiad.people;

/**
 * A student is the participant in the {@link edu.pennstate.science_olympiad.Olympiad} and the
 *  {@link edu.pennstate.science_olympiad.Event}s therein. There will be more of these actors than any other.
 */
public class Student extends AUser {
    //This is a variable to keep track of whether a student has opted out of SMS messages
    private boolean hasOptedOut;

    public Student() {
        super();
    }

    public boolean hasOptedOut() {
        return hasOptedOut;
    }

    public void setHasOptedOut(boolean hasOptedOut) {
        this.hasOptedOut = hasOptedOut;
    }
}
