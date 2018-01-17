package edu.pennstate.science_olympiad;

import sun.security.util.Password;
import java.math.BigDecimal;

/**
 * This is one person using this system. This includes {@link edu.pennstate.science_olympiad.Olympiad} administrators,
 * competition participants, and spectators.
 */
public class User {

    private String firstName;
    private String lastName;
    //private Password password;
   // private BigDecimal salt;
   // private AccessLevelEnum accessLevel;
   //
    // private TeamRoleEnum teamRoleEnum;

    public User() {
        firstName = "testing";
        lastName = "testing";


    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
