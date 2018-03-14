import com.google.gson.Gson;
import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.people.Admin;
import edu.pennstate.science_olympiad.sms.EmailSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
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

