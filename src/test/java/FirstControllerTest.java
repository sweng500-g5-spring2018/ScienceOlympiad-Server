import com.google.gson.Gson;
import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.people.AUser;
import edu.pennstate.science_olympiad.people.Admin;
import edu.pennstate.science_olympiad.people.UserFactory;
import edu.pennstate.science_olympiad.sms.EmailSender;
import edu.pennstate.science_olympiad.sms.TextMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.hamcrest.Matcher;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= SpringConfig.class)
@WebAppConfiguration
public class FirstControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private Admin brandon;
    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

    }

    @Test
    public void getAccount() throws Exception {
        //simple check to see if the
        MvcResult result = this.mockMvc.perform(get("/users")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.firstName").value("Brandon"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Gson gson = new Gson();
        Admin brandon = gson.fromJson(content, Admin.class);
        brandon.setPassword("password");
        assert brandon.isPasswordEqual("password");
    }

    @Test
    public void checkJudgesExist() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/getJudges")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn();
    }

    @Test
    public void sendText() throws Exception {
        brandon = (Admin)UserFactory.getInstance().createUser("admin");
        brandon.setFirstName("Brandon");
        brandon.setLastName("Hessler");
        brandon.setPhoneNumber("+19092136132");
        brandon.setEmailAddress("test@brandonhessler.com");
        brandon.setSiteName("Camp Couch");
        brandon.setReceiveText(true);
        TextMessage.getInstance().text(brandon, "This is a test");
    }
}
