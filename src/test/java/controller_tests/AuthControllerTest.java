package controller_tests;


import com.google.gson.Gson;
import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.controllers.AuthController;
import edu.pennstate.science_olympiad.helpers.request.LoginJsonHelper;
import edu.pennstate.science_olympiad.people.AUser;
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
import org.mockito.runners.MockitoJUnitRunner;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes= SpringConfig.class)
@WebAppConfiguration
public class AuthControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private Admin brandon;
    Gson gson = new Gson();

    @Mock
    UserRepository userRepository;

    @Mock
    HttpSession userSession;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    private AuthController authController;

    /**
     * Before the login we need to make sure there is a particular user in the database
     */
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();

        brandon  = (Admin)UserFactory.getInstance().createUser("admin");
        brandon.setSiteName("Penn State");
        brandon.setFirstName("Brandon");
        brandon.setLastName("Hessler");
        brandon.setPhoneNumber("+18056162550");
        brandon.setEmailAddress("brandon@test.com");
        brandon.setReceiveText(false);
        brandon.setPasswordPlainText("Password");

    }

    @Test
    public void login() throws Exception{

        //This test should return ok
        String emailAddress = "brandon@test.com";
        String rawPassword = "Password";
        LoginJsonHelper loginJsonHelper = new LoginJsonHelper(emailAddress, rawPassword);
        String loginJson = gson.toJson(loginJsonHelper);

        Mockito.when(userRepository.getUser(any(LoginJsonHelper.class))).thenReturn(brandon);

        MvcResult good = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        return request;
                    }})).content(loginJson))

                .andExpect(status().isOk())
                .andReturn();

        // This test should return unauthorized
        Mockito.when(userRepository.getUser(any(LoginJsonHelper.class))).thenReturn(null);

        MvcResult unauthorized = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        return request;
                    }})).content(loginJson))

                .andExpect(status().isUnauthorized())
                .andReturn();

        //This test should return Bad Request
        emailAddress = "brandon@test.com";
        rawPassword = "";

        loginJsonHelper = new LoginJsonHelper(emailAddress, rawPassword);
        loginJson = gson.toJson(loginJsonHelper);

        MvcResult malformed = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        return request;
                    }})).content(loginJson))

                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void logout() throws Exception {
        //This should work
        String emailAddress = "brandon@test.com";
        String rawPassword = "Password";
        LoginJsonHelper logoutJsonHelper = new LoginJsonHelper(emailAddress, rawPassword);
        String logoutJson = gson.toJson(logoutJsonHelper);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(logoutJson))

                .andExpect(status().isOk())
                .andReturn();


        //This should return Bad Request
        emailAddress = "kevinSucks@test.com";
        rawPassword = "Password";
        logoutJsonHelper = new LoginJsonHelper(emailAddress, rawPassword);
        logoutJson = gson.toJson(logoutJsonHelper);

        MvcResult bad = mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(logoutJson))

                .andExpect(status().isBadRequest())
                .andReturn();

        //This should return ok since the session was already terminated
        Mockito.when((AUser) userSession.getAttribute(any(String.class))).thenReturn(null);

        MvcResult alreadyTerminated = mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", null);
                        return request;
                    }})).content(logoutJson))

                .andExpect(status().isOk())
                .andReturn();
        assert alreadyTerminated.getResponse().getContentAsString().equals("Session already terminated.");

        //This should return accepted since the session is null
        Mockito.when(request.getSession(false)).thenReturn(null);

        MvcResult nullSession = mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = null;
                        return request;
                    }})).content(logoutJson))

                .andExpect(status().isAccepted())
                .andReturn();

        //This should return bad request for malformed json
        logoutJson = gson.toJson("emailAddress");

        MvcResult badJson = mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(logoutJson))

                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
