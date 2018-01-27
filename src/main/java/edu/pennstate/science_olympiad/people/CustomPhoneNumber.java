package edu.pennstate.science_olympiad.people;

import com.twilio.type.PhoneNumber;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The Twilio phonenumber object can not be inserted into mongo by itself,
 * this class is needed if inserting this object into the DB.
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

