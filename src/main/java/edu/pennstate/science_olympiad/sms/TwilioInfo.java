package edu.pennstate.science_olympiad.sms;

public class TwilioInfo {

    private String phoneNumber;
    private String accountSid;
    private String authToken;

    public TwilioInfo(String phoneNumber, String accountSid, String authToken) {
        this.phoneNumber = phoneNumber;
        this.accountSid = accountSid;
        this.authToken = authToken;
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
