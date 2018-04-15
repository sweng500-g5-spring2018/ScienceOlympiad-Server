package edu.pennstate.science_olympiad.helpers.request;

public class LoginJsonHelper {

    private String emailAddress;
    private String password;

    public LoginJsonHelper(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPassword() {
        return password;
    }
}
