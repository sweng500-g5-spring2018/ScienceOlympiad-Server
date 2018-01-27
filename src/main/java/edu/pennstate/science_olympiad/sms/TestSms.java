package edu.pennstate.science_olympiad.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TestSms {

    // Find your Account Sid and Token at twilio.com/user/account
    private static final String ACCOUNT_SID = "AC9984cfcb94d38cbd74d5cfbb4cf5aa17";
    private static final String AUTH_TOKEN = "cba1ed3da109226750b8fd9b90000f55";

    public static void main(String[] args) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        //Text Jeff
        Message.creator(new PhoneNumber("+15709050467"),
                new PhoneNumber("+18143478009"),
                "Greetings SWENG 500 Group 5,  from Java... fuck yea!!!").create();

//        //Text Kyle
//        Message.creator(new PhoneNumber("+19086165430"),
//                new PhoneNumber("+18143478009"),
//                "Greetings SWENG 500 Group 5,  from Java... fuck yea!!!").create();
//
//        //Text Ryan
//        Message.creator(new PhoneNumber("+14127607290"),
//                new PhoneNumber("+18143478009"),
//                "Greetings SWENG 500 Group 5,  from Java... fuck yea!!!").create();

    }

}
