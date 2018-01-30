package edu.pennstate.science_olympiad.people;


import edu.pennstate.science_olympiad.ImproperCreationException;
import edu.pennstate.science_olympiad.School;

/**
 * A Coach is a representative of the school and is a chaperon for {@link edu.pennstate.science_olympiad.people.Student}s.
 * A Coach can have many students and many teams.
 */
public class Coach extends AUser {

    private School school;

    public Coach(Admin admin) {
        super();
        if (admin == null)
            throw new ImproperCreationException();
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

}
