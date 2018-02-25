package edu.pennstate.science_olympiad.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.people.AUser;

import java.text.SimpleDateFormat;

public class TextMessage {

    // Find your Account Sid and Token at twilio.com/user/account
    private static TextMessage INSTANCE;
    private static final String ACCOUNT_SID = "AC9984cfcb94d38cbd74d5cfbb4cf5aa17";
    private static final String AUTH_TOKEN = "cba1ed3da109226750b8fd9b90000f55";
    private static final PhoneNumber FROM_NUMBER = new PhoneNumber("+18143478009");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

//    public static void main(String[] args) {
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//        textKyle("Paid Twilio is all set up");
//        textJeff("Paid Twilio is all set up");
//        textRyan("Paid Twilio is all set up");
//    }

    private TextMessage() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static TextMessage getInstance() {
        if (INSTANCE == null)
            INSTANCE = new TextMessage();
        return INSTANCE;
    }

    public void text(AUser user, String message) {
        if (user != null && user.getPhoneNumber()!= null)
            Message.creator(user.getPhoneNumber(), FROM_NUMBER, message).create();
    }

    public void textEventTime(AUser user, Event event) {
        if (user != null && event != null) {
            String message = user.getFirstName() + ", you have an event starting soon. " +
                    event.getName() + " is starting at " + dateFormat.format(event.getStartTime()) +
                    " and is at Building " + event.getBuilding() + ".";
            text(user, message);
        }
    }

    private static void textBrandon(String message) {
        if (message == null)
            message = "Let's crush this course!";

        Message.creator(new PhoneNumber("+19092136132"), FROM_NUMBER, message).create();
    }

    private static void textJeff(String message) {
        if (message == null)
            message = "Let's crush this course!";

        Message.creator(new PhoneNumber("+15709050467"), FROM_NUMBER, message).create();
    }
    private static void textKyle(String message) {
        if (message == null)
            message = "Let's crush this course!";

        Message.creator(new PhoneNumber("+19086165430"), FROM_NUMBER, message).create();
    }
    private static void textRyan(String message) {
        if (message == null)
            message = "Let's crush this course!";

        Message.creator(new PhoneNumber("+14127607290"), FROM_NUMBER, message).create();
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
