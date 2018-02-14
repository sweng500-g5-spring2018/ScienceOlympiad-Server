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

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
