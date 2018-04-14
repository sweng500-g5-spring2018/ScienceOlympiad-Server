import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.sms.EmailSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= SpringConfig.class)
@WebAppConfiguration
public class EmailSenderTest {

        @Autowired
        EmailSender emailSender;

        /**
        * Run a simple test to make sure the email server is running correctly
         * Test.com will not actually send anything right now..
        */
        @Test
        public void testEmailConnection() throws Exception {
            emailSender.sendMail("kyle.hughes025@test.com","Test","This is a test email");

        }
    }

