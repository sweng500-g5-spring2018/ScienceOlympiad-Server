package edu.pennstate.science_olympiad.helpers.response;

import edu.pennstate.science_olympiad.people.*;

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

    public static String getUserType(AUser user) {
        if(user instanceof Admin) {
            return IUserTypes.ADMIN;
        } else if (user instanceof Coach) {
            return IUserTypes.COACH;
        } else if (user instanceof Judge) {
            return IUserTypes.JUDGE;
        } else if (user instanceof Student) {
            return IUserTypes.STUDENT;
        } else {
            return "AUser";
        }
    }
}
