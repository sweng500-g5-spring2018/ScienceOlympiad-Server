package edu.pennstate.science_olympiad.people;

import edu.pennstate.science_olympiad.School;
import edu.pennstate.science_olympiad.Team;

/**
 * A student is the participant in the {@link edu.pennstate.science_olympiad.Olympiad} and the
 *  {@link edu.pennstate.science_olympiad.Event}s therein. There will be more of these actors than any other.
 */
public class Student extends AUser {
    private School school;
    private Team team;


    public Student() {
        super();
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

}
