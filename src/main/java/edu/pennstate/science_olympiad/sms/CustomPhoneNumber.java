package edu.pennstate.science_olympiad.sms;

import com.twilio.type.PhoneNumber;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The Twilio phonenumber object can not be inserted into mongo by itself,
 * this class is needed if inserting this object into the DB.
 * The format of this number is +12345678910 which is +1 (234) 567-8910
 */
@Document
public class CustomPhoneNumber extends PhoneNumber {

    private String number;
    public CustomPhoneNumber(String number) {
        super(number);
        this.number = number;
    }

    public String getNumber() {
        return this.number;
    }
}

