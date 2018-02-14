package edu.pennstate.science_olympiad.helpers.response;

public class LoginResponseHelper {

    private String emailAddress;
    private String role;
    private String session;

    public LoginResponseHelper(String emailAddress, String role, String session) {
        this.emailAddress = emailAddress;
        this.role = role;
        this.session = session;
    }

    public String getEmailAddress() { return emailAddress; }

    public String getRole() { return role; }

    public String getSession() { return session; }

    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public void setRole(String role) { this.role = role; }

    public void setSession(String session) { this.session = session; }
}
