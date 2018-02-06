package edu.pennstate.science_olympiad.people;

import com.twilio.type.PhoneNumber;
import edu.pennstate.science_olympiad.sms.CustomPhoneNumber;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sun.security.util.Password;

import java.util.Date;

/**
 * This is the abstract class for all users of this application. All other users extend from this.
 * When we first create any user, we create it from the  {@link edu.pennstate.science_olympiad.people.UserFactory},
 * which will call the default constructor. It is advisable to set the firstName, lastName, and emailAddress
 * to begin with, then the user can set the rest of the preferences when they register.
 *
 * Inserts will be entered into the collection : ausers unless otherwise specified
 */

@Document(collection="ausers")
public abstract class AUser {

    @Id
    public String id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private CustomPhoneNumber phoneNumber;
    private String userId;
    private Password password;
    private String salt;
    private int minutesBeforeEvent;
    private Date lastLoginDate;

    public AUser() {
        firstName = "";
        lastName = "";
        emailAddress = "";
        phoneNumber = new CustomPhoneNumber("+15555555555");
        userId = "";
        password = new Password();
        minutesBeforeEvent = 10;
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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(CustomPhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public int getMinutesBeforeEvent() {
        return minutesBeforeEvent;
    }

    public void setMinutesBeforeEvent(int minutesBeforeEvent) {
        this.minutesBeforeEvent = minutesBeforeEvent;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

}
