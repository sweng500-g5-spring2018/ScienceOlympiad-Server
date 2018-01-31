package edu.pennstate.science_olympiad.people;

/**
 * The admin is the person who creates all of the  {@link edu.pennstate.science_olympiad.Event}s and registers the
 * {@link edu.pennstate.science_olympiad.people.Judge}s and {@link edu.pennstate.science_olympiad.people.Coach}s. This
 * person is the base of everything happening.
 */


public class Admin extends AUser {
    private String siteName;

    public Admin() {
        super();
        this.siteName = "";
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

}
