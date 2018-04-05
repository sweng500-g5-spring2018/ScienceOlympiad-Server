package edu.pennstate.science_olympiad.controllers;

import edu.pennstate.science_olympiad.URIConstants;
import edu.pennstate.science_olympiad.helpers.json.JsonHelper;
import edu.pennstate.science_olympiad.sms.EmailSender;
import edu.pennstate.science_olympiad.sms.TextMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class CommsController implements URIConstants{
    Log logger = LogFactory.getLog(getClass());

    @Autowired
    EmailSender emailSender;
    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * This sends out a test email to an email address that is provided by the user
     * @param toAddressJson the address, in JSON format, to send the email to
     * @return whether or not the email was sent
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= SEND_TEST_EMAIL, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> sendTestEmail(@RequestBody String toAddressJson) {
        try {

            String toAddress = JsonHelper.getJsonString(toAddressJson, "emailAddress");

            logger.info("Hit the send email endpoint. Sending to: " + toAddress);

            boolean sent = emailSender.sendMail(toAddress, "Test Email", "This email is a test.\n\n " +
                    "This could be used as a way for students to be informed that their coach registered them, " +
                    "or to recover a password.");

            logger.info("Email sent: " + sent);

            if (sent){
                return ResponseEntity.status(HttpStatus.OK).body("Email was sent.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email was not sent, an error occurred");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }


    /**
     * Sends a generic text message to the phone number provided by the user
     * @param toNumberJson the reformatted phone number, in string json format, to send the text to
     * @return whether or not the text was sent
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value= SEND_TEST_TEXT, method= RequestMethod.POST ,produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> sendTestText(@RequestBody String toNumberJson) {
        try {

            String toNumber = JsonHelper.getJsonString(toNumberJson, "phoneNumber");
            logger.info("Hit the send text endpoint. Sending text to: " + toNumber);

            boolean sent = TextMessage.getInstance().text(toNumber, "This is a sample text that could be " +
                    "used to unsubscribe, or confirm a phone number.");

            logger.info("Text sent: " + sent);

            if (sent){
                return ResponseEntity.status(HttpStatus.OK).body("Email was sent.");}
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email was not sent, an error occurred");

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Your request could not be processed.");
        }
    }
}
