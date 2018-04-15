package edu.pennstate.science_olympiad.sms;

public class TextingHelper {

    public TextingHelper() {
    }

    public boolean sendMessage(String toNumber, String message) {
        return TextMessage.getInstance().text(toNumber, message);
    }
}
