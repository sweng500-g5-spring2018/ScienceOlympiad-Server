package edu.pennstate.science_olympiad.people;

public class Admin extends AUser {
    private String siteName;

    public Admin() {
        this.siteName = "";
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
