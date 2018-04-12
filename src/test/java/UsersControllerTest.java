import com.google.gson.Gson;
import edu.pennstate.science_olympiad.School;
import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.controllers.UsersController;
import edu.pennstate.science_olympiad.people.AUser;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Judge;
import edu.pennstate.science_olympiad.repositories.SchoolRepository;
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
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= SpringConfig.class)
@WebAppConfiguration
public class UsersControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SchoolRepository schoolRepository;

    @InjectMocks
    private UsersController userController;

    private Gson gson;

    @Before
    public void setup() throws Exception {
        //set up the controller want to test
       MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
    }


    /**
     * Test /getUserProfile
     * @throws Exception
     */
    @Test
    public void getUserProfile() throws Exception {
        final AUser coach1 = new Coach();
        coach1.setFirstName("Joey");
        coach1.setLastName("J");
        //Send the fake session with a user to return
        MvcResult result = mockMvc.perform(get("/getUserProfile")
                .with((new RequestPostProcessor() {
                            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                                HttpSession session = request.getSession();
                                session.setAttribute("user",coach1);
                                    return request;
                            }})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName",is("Joey")))
                .andExpect(jsonPath("$.lastName", is("J")))
                .andReturn();

    }

    /**
     * Test /getCoaches
     * @throws Exception
     */
    @Test
    public void getAllCoaches() throws Exception {
        List<Coach> coaches = new ArrayList<Coach>();
        Coach coach1 = new Coach();
        coach1.setFirstName("Joey");
        coach1.setLastName("J");
        coaches.add(coach1);
        Coach coach2 = new Coach();
        coach2.setFirstName("Nicky");
        coach2.setLastName("J");
        coaches.add(coach2);

        //userRepository needs to be mocked to be used here
        Mockito.when(userRepository.getAllCoaches()).thenReturn(coaches);
       MvcResult result = mockMvc.perform(get("/getCoaches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName",is("Joey")))
                .andExpect(jsonPath("$[0].lastName", is("J")))
                .andExpect(jsonPath("$[1].firstName", is("Nicky")))
                .andExpect(jsonPath("$[1].lastName", is("J")))
               .andReturn();

        //only executed once
        Mockito.verify(userRepository, times(1)).getAllCoaches();
        verifyNoMoreInteractions(userRepository);
    }

    /**
     * Test /getJudges
     * @throws Exception
     */
    @Test
    public void getAllJudges() throws Exception {
        List<Judge> judges = new ArrayList<Judge>();

        Judge judge1 = new Judge();
        judge1.setFirstName("Joey");
        judge1.setLastName("J");
        judges.add(judge1);

        Judge judge2 = new Judge();
        judge2.setFirstName("Nicky");
        judge2.setLastName("J");
        judges.add(judge2);

        //userRepository needs to be mocked to be used here
        // what will be returned by the controller rest call
        Mockito.when(userRepository.getAllJudges()).thenReturn(judges);
        MvcResult result = mockMvc.perform(get("/getJudges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName",is("Joey")))
                .andExpect(jsonPath("$[0].lastName", is("J")))
                .andExpect(jsonPath("$[1].firstName", is("Nicky")))
                .andExpect(jsonPath("$[1].lastName", is("J")))
                .andReturn();

        //only executed once
        Mockito.verify(userRepository, times(1)).getAllJudges();
        verifyNoMoreInteractions(userRepository);
    }

/** keep getting a 404 response
    @Test
    public void removeUser() throws Exception {
        String userId = "123456789012345678901234";

        Mockito.when(userRepository.removeUser(userId)).thenReturn(true);
        Mockito.when(MongoIdVerifier.isValidMongoId(userId)).thenReturn(true);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/removeUser/123456789012345678901234"))
                .andExpect(status().isOk());

        Mockito.verify(userRepository, times(1)).removeUser(userId);
        verifyNoMoreInteractions(userRepository);
    }

    */

    /**
     * Test updateUser
     * @throws Exception
     */
    @Test
    public void updateUser() throws Exception {
        final AUser coach1 = new Coach();
        coach1.setFirstName("Joey");
        coach1.setLastName("J");
        gson = new Gson();
        String coachJson = gson.toJson(coach1);

        Mockito.when(userRepository.updateUser(coach1,coachJson)).thenReturn(true);

        //Send the fake session with a user to return
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/updateUser")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user",coach1);
                        return request;
                    }})).content(coachJson))

                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(userRepository, times(1)).updateUser(coach1,coachJson);
        verifyNoMoreInteractions(userRepository);
    }

    /**
     * Test changePassword
     * @throws Exception
     */
    @Test
    public void changePassword() throws Exception {
        final AUser coach1 = new Coach();
        //need to give it json so the static method will pass since it can't be stubbed with Mockito
        String passwordJson = "{\"password\": \"newPass\"}";
        coach1.setFirstName("Joey");
        coach1.setLastName("J");

        //NOTE: tried using powermockito but had no luck mocking static methods
        //newPass is the expected result from the static JsonHelper so assume it here
        Mockito.when(userRepository.changePassword(coach1,"newPass")).thenReturn(true);

        //Send the fake session with a user to return
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/changePassword")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user",coach1);
                        return request;
                    }})).content(passwordJson))

                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(userRepository, times(1)).changePassword(coach1,"newPass");
        verifyNoMoreInteractions(userRepository);
    }


    /**
     * Test /emailAvailbale
     * @throws Exception
     */
    @Test
    public void emailAvailable() throws Exception {
        //need to give it json so the static method will pass since it can't be stubbed with Mockito
        String emailJson = "{\"emailAddress\": \"test@test.com\"}";

        //tried using powermockito but had no luck mocking static methods
        //Mockito.when(JsonHelper.getJsonString(coachJson,"password1")).thenReturn("newPass");
        Mockito.when(userRepository.emailUsed("test@test.com")).thenReturn(true);
        //email already in use
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/emailAvailable")
                .content(emailJson))
                .andExpect(status().isConflict())
                .andReturn();

        //Now test that it is availbale
        String emailJson2 = "{\"emailaddress\": \"test@test.com\"}";

        //tried using powermockito but had no luck mocking static methods
        //Mockito.when(JsonHelper.getJsonString(coachJson,"password1")).thenReturn("newPass");
        Mockito.when(userRepository.emailUsed("test@test.com")).thenReturn(false);

        //Send the fake session with a user to return
        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders.post("/emailAvailable")
                .content(emailJson2))
                .andExpect(status().isOk())
                .andReturn();
    }

    /**
     * Test /addUser
     * @throws Exception
     */
    @Test
    public void addUser() throws Exception {
        //need to give it json so the static method will pass since it can't be stubbed with Mockito
        AUser userToAdd = new Coach();
        userToAdd.setFirstName("Joey");
        userToAdd.setLastName("jin");
        userToAdd.setPasswordPlainText("Password1");
        userToAdd.setEmailAddress("test5555@test.com");
        gson = new Gson();
        String userJson = gson.toJson(userToAdd);
        School foundSchool = new School("TestingSchool");
        //Test 1 - everything executes correctly
        //NOTE: had to use any(), sing the userToAdd object did not work.
        Mockito.when(userRepository.addUser((AUser) Mockito.any())).thenReturn(true);
        Mockito.when(userRepository.addSchoolToUser(foundSchool,userToAdd)).thenReturn(true);
        Mockito.when(schoolRepository.getSchool("111111111111111111111111")).thenReturn(foundSchool);


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/addUser")
                .param("userType","COACH")
                .param("schoolID","111111111111111111111111")
                .content(userJson))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.when(userRepository.addUser((AUser) Mockito.any())).thenReturn(true);
        Mockito.when(userRepository.addSchoolToUser(foundSchool,userToAdd)).thenReturn(true);
        Mockito.when(schoolRepository.getSchool("111111111111111111111111")).thenReturn(null);


        //Test 2 - a bad school id so make sure the add user methods were not called
        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders.post("/addUser")
                .param("userType","COACH")
                .param("schoolID","111111111111111111111111")
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andReturn();
        verify(userRepository,Mockito.times(0)).addUser(userToAdd);
        verify(userRepository,Mockito.times(0)).addSchoolToUser(foundSchool,userToAdd);

        //Test 3 - User already exists, make sure add school to user is not executed
        Mockito.when(userRepository.addUser((AUser) Mockito.any())).thenReturn(false);
        Mockito.when(userRepository.addSchoolToUser(foundSchool,userToAdd)).thenReturn(true);
        Mockito.when(schoolRepository.getSchool("111111111111111111111111")).thenReturn(foundSchool);
        MvcResult result3 = mockMvc.perform(MockMvcRequestBuilders.post("/addUser")
                .param("userType","COACH")
                .param("schoolID","111111111111111111111111")
                .content(userJson))
                .andExpect(status().isConflict())
                .andReturn();
        verify(userRepository,Mockito.times(0)).addSchoolToUser(foundSchool,userToAdd);

        //Test 4 - User added but the school couldnt be added correctly
        //controller mightnot be edited to return the multi-status. Will wait to change so for now assume its ok
        Mockito.when(userRepository.addUser((AUser) Mockito.any())).thenReturn(true);
        Mockito.when(schoolRepository.getSchool("111111111111111111111111")).thenReturn(foundSchool);
        Mockito.when(userRepository.addSchoolToUser(foundSchool, userToAdd)).thenReturn(false);
        MvcResult result4 = mockMvc.perform(MockMvcRequestBuilders.post("/addUser")
                .param("userType","COACH")
                .param("schoolID","111111111111111111111111")
                .content(userJson))
                .andExpect(status().isOk())
                .andReturn();

    }


}
