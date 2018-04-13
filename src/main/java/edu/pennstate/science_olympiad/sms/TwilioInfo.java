package edu.pennstate.science_olympiad.sms;

public class TwilioInfo {

    private static TwilioInfo INSTANCE;
    private String phoneNumber;
    private String accountSid;
    private String authToken;

    private TwilioInfo() {
    }

    public static TwilioInfo getInstance() {
        if (INSTANCE == null)
            INSTANCE = new TwilioInfo();
        return INSTANCE;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
