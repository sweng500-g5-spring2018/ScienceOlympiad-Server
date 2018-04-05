package edu.pennstate.science_olympiad.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.people.AUser;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;

public class TextMessage {

    // Find your Account Sid and Token at twilio.com/user/account
    private static TextMessage INSTANCE;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

    @Autowired
    TwilioInfo twilioInfo;

//    public static void main(String[] args) {
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//        textKyle("Paid Twilio is all set up");
//        textJeff("Paid Twilio is all set up");
//        textRyan("Paid Twilio is all set up");
//    }

    private TextMessage() {
        Twilio.init(twilioInfo.getAccountSid(), twilioInfo.getAuthToken());
    }

    public static TextMessage getInstance() {
        if (INSTANCE == null)
            INSTANCE = new TextMessage();
        return INSTANCE;
    }

    public boolean text(AUser user, String message) {
        try {
            if (user != null && user.getPhoneNumber() != null) {
                text(user.getPhoneNumber(), message);
                return true;
            }
        } catch (Exception e){}
        return false;
    }

    public boolean text(String phoneNumber, String message) {
        try {
            Message.creator(new PhoneNumber(phoneNumber), twilioInfo.getPhoneNumber(), message).create();
            return true;
        } catch (Exception e) {}
        return false;
    }

    public void textEventTime(AUser user, Event event) {
        if (user != null && event != null) {
            String message = user.getFirstName() + ", you have an event starting soon. " +
                    event.getName() + " is starting at " + dateFormat.format(event.getStartTime()) +
                    " and is at Building " + event.getRoom() + ".";
            text(user, message);
        }
    }

//This is for the bulk notifications (up to 10,000 at a time). I am not sure where we get the SERVICE_SID
    /*
     *import java.util.Arrays;
import java.util.List;
import com.twilio.Twilio;
import com.twilio.rest.notify.v1.service.Notification;

public class Example {
  // Find your Account Sid and Token at twilio.com/console
  public static final String ACCOUNT_SID = "ACCOUNT_SID";
  public static final String AUTH_TOKEN = "AUTH_TOKEN";

  public static final String SERVICE_SID = "ISXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

  public static void main(String[] args) {
    // Initialize the client
    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

    List toBindings = Arrays.asList(
    "{\"binding_type\":\"sms\",\"address\":\"+15555555555\"}",
    "{\"binding_type\":\"facebook-messenger\",\"address\":\"123456789123\"}");

    Notification notification = Notification
	    .creator(SERVICE_SID)
	    .setBody("Hello Bob")
	    .setToBinding(toBindings)
	    .create();

    System.out.println(notification.getSid());
  }
}
     */

}
