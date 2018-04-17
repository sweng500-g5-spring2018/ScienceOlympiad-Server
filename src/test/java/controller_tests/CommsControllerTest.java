package controller_tests;

import com.google.gson.Gson;
import com.twilio.type.PhoneNumber;
import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.controllers.BuildingController;
import edu.pennstate.science_olympiad.controllers.CommsController;
import edu.pennstate.science_olympiad.helpers.json.JsonHelper;
import edu.pennstate.science_olympiad.helpers.mongo.MongoIdVerifier;
import edu.pennstate.science_olympiad.people.Admin;
import edu.pennstate.science_olympiad.repositories.BuildingRepository;
import edu.pennstate.science_olympiad.sms.EmailSender;
import edu.pennstate.science_olympiad.sms.TextMessage;
import edu.pennstate.science_olympiad.sms.TextingHelper;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes= SpringConfig.class)
@WebAppConfiguration
public class CommsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    Gson gson = new Gson();
    private Admin brandon;

    @Mock
    EmailSender emailSender;

    @Mock
    TextingHelper textingHelper;

    @Mock
    PhoneNumber toPhone;

    @Mock
    BuildingRepository buildingRepository;

    @InjectMocks
    private CommsController commsController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(commsController)
                .build();

    }

    @Test
    public void sendTestEmail() throws Exception{
        String emailAddress = "brandon@test.com";
        String toAddress = gson.toJson(emailAddress, String.class);

        //The email was sent
        Mockito.when(emailSender.sendMail(any(String.class), any(String.class), any(String.class))).thenReturn(true);

        MvcResult good = mockMvc.perform(MockMvcRequestBuilders.post("/sendTestEmail")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(toAddress))

                .andExpect(status().isOk())
                .andReturn();

        //The email was not sent due to conflict
        Mockito.when(emailSender.sendMail(any(String.class), any(String.class), any(String.class))).thenReturn(false);

        MvcResult conflict = mockMvc.perform(MockMvcRequestBuilders.post("/sendTestEmail")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(toAddress))

                .andExpect(status().isConflict())
                .andReturn();

        //An exception was caused and caught
        Mockito.when(emailSender.sendMail(any(String.class), any(String.class), any(String.class))).thenThrow(new NullPointerException());

        MvcResult exception = mockMvc.perform(MockMvcRequestBuilders.post("/sendTestEmail")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(toAddress))

                .andExpect(status().isInternalServerError())
                .andReturn();

    }

    @Test
    public void sendTestText() throws Exception{
        JSONObject obj = new JSONObject();
        obj.put("phoneNumber", "+18056162550");
        String toNumber = obj.toJSONString();

        //The text was sent
        Mockito.when(textingHelper.sendMessage(any(String.class), any(String.class))).thenReturn(true);

        MvcResult good = mockMvc.perform(MockMvcRequestBuilders.post("/sendTestText")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(toNumber))

                .andExpect(status().isOk())
                .andReturn();


        //The text was not sent due to conflict
        JSONObject obj2 = new JSONObject();
        obj2.put("phoneNumber", "+18005555555");
        String toNumber2 = obj2.toJSONString();
        Mockito.when(textingHelper.sendMessage(any(String.class), any(String.class))).thenReturn(false);

        MvcResult conflict = mockMvc.perform(MockMvcRequestBuilders.post("/sendTestText")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(toNumber2))

                .andExpect(status().isConflict())
                .andReturn();


        //An exception was caused and caught
        JSONObject obj3 = new JSONObject();
        obj2.put("phoneNumber", "asdfhjetyw");
        String toNumber3 = obj3.toJSONString();

        Mockito.when(textingHelper.sendMessage(any(String.class), any(String.class))).thenThrow(new NullPointerException());

        MvcResult exception = mockMvc.perform(MockMvcRequestBuilders.post("/sendTestText")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(toNumber3))

                .andExpect(status().isInternalServerError())
                .andReturn();

    }

}
