package edu.pennstate.science_olympiad.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.people.AUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;

public class TextMessage {

    // Find your Account Sid and Token at twilio.com/user/account
    private static TextMessage INSTANCE;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    Log logger = LogFactory.getLog(getClass());

    private TwilioInfo twilioInfo;

//    public static void main(String[] args) {
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//        textKyle("Paid Twilio is all set up");
//        textJeff("Paid Twilio is all set up");
//        textRyan("Paid Twilio is all set up");
//    }

    private TextMessage() {
        if (twilioInfo == null)
            twilioInfo = TwilioInfo.getInstance();

        logger.info("Init TextMessage: " + twilioInfo.getAccountSid() + " " + twilioInfo.getAuthToken());
        Twilio.init(twilioInfo.getAccountSid(), twilioInfo.getAuthToken());
    }

    public static TextMessage getInstance() {
        Log logger = LogFactory.getLog("TextMessage");
        logger.info(">>TextMessage.getInstance() " + INSTANCE);
        SpringConfig springConfig = new SpringConfig();
        springConfig.twilioInfo();
        try {
            if (INSTANCE == null) {
                INSTANCE = new TextMessage();
                logger.info("Called new");
            }
            return INSTANCE;
        }catch (Exception e) {
            logger.info(e.getMessage(), e);
            return null;
        }
    }

    public boolean text(AUser user, String message) {
        try {
            if (user != null && user.getPhoneNumber() != null && user.isReceiveText()) {
                text(user.getPhoneNumber(), message);
                return true;
            }
        } catch (Exception e){}
        return false;
    }

    public boolean text(String phoneNumber, String message) {
        try {
            if (phoneNumber.equals("+18005555555"))
                throw new NullPointerException();
            if (!phoneNumber.equals("+18056162550")) {
                Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber(twilioInfo.getPhoneNumber()), message).create();
                return true;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
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
