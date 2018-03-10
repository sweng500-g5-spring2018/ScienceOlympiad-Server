package edu.pennstate.science_olympiad.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {
    @Autowired
    JavaMailSender mailSender;

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    /**
     * Send an email to a user
     * @param to - the user email to deliver the message
     * @param subject the subject line
     * @param text the text of the email message
     */
    public void sendMail(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("group5capstone@sweng500.com");
        mailSender.send(message);
    }
}
