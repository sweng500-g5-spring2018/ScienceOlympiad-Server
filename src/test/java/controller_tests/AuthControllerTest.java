package controller_tests;


import com.google.gson.Gson;
import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.controllers.AuthController;
import edu.pennstate.science_olympiad.helpers.request.LoginJsonHelper;
import edu.pennstate.science_olympiad.people.Admin;
import edu.pennstate.science_olympiad.people.UserFactory;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= SpringConfig.class)
@WebAppConfiguration
public class AuthControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private Admin brandon;

    @Autowired
    UserRepository userRepository;

    @InjectMocks
    private AuthController authController;

    /**
     * Before the login we need to make sure there is a particular user in the database
     */
    @Before
    public void setup() {
        brandon  = (Admin)UserFactory.getInstance().createUser("admin");
        brandon.setSiteName("Penn State");
        brandon.setFirstName("Brandon");
        brandon.setLastName("Hessler");
        brandon.setPhoneNumber("+18056162550");
        brandon.setEmailAddress("brandon@test.com");
        brandon.setReceiveText(false);
        brandon.setPasswordPlainText("Password");

        userRepository.addUser(brandon);

        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }

    @Test
    public void login() throws Exception{
        String emailAddress = "brandon@test.com";
        String rawPassword = "Password";
        LoginJsonHelper loginJsonHelper = new LoginJsonHelper(emailAddress, rawPassword);
        Gson gson = new Gson();
        String loginJson = gson.toJson(loginJsonHelper);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        return request;
                    }})).content(loginJson))

                .andExpect(status().isOk())
                .andReturn();
    }
}
