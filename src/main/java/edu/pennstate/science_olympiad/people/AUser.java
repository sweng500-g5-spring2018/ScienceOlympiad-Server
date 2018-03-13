package edu.pennstate.science_olympiad.people;

import com.twilio.type.PhoneNumber;
import edu.pennstate.science_olympiad.sms.CustomPhoneNumber;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
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
Log logger = LogFactory.getLog(getClass());

    @Id
    public String id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private String password;
    private int minutesBeforeEvent;
    private boolean receiveText;
    private transient byte[] salt;
    private transient Date lastLoginDate;

    public AUser() {
        firstName = "";
        lastName = "";
        emailAddress = "";
        phoneNumber = "";
        password = "";
        minutesBeforeEvent = 10;
        receiveText = false;

        salt = generateSalt();
    }

    public String getId() {
        return id;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Being passed in should be the String password, in here we will hash and salt it before setting it
     * @param passwordStr the String that a user will type in
     */
    public void setPassword(String passwordStr) {
        this.password = get_SHA_512_SecurePassword(passwordStr);
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

    public boolean isReceiveText() {
        return receiveText;
    }

    public void setReceiveText(boolean receiveText) {
        this.receiveText = receiveText;
    }

    private byte[] generateSalt() {
        try {
            //Always use a SecureRandom generator
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            //Create array for salt
            byte[] salt = new byte[16];
            //Get a random salt
            sr.nextBytes(salt);
            return salt;
        } catch (NoSuchAlgorithmException ex) {
           // logger.warn("Could not generate salt");
        }
        return null;
    }

    public void hashPassword() {
        setPassword(this.password);
    }

    public String get_SHA_512_SecurePassword(String passwordToHash){
        String salt = Arrays.toString(this.salt);
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public boolean isPasswordEqual(String passwordToCheck) {
        String securePasswordToCheck = get_SHA_512_SecurePassword(passwordToCheck);
        return securePasswordToCheck.equals(this.password);
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public void copyInfo(AUser aUser) {
        logger.info("Copying user info");
        this.firstName = aUser.getFirstName();
        this.lastName = aUser.getLastName();
        this.emailAddress = aUser.getEmailAddress();
        this.phoneNumber = aUser.getPhoneNumber();
        this.minutesBeforeEvent = aUser.getMinutesBeforeEvent();
        this.receiveText = aUser.isReceiveText();
    }

}
