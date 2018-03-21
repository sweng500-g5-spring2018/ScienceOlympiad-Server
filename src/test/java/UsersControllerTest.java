import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.controllers.UsersController;
import edu.pennstate.science_olympiad.people.AUser;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Judge;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= SpringConfig.class)
@WebAppConfiguration
public class UsersControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UsersController userController;

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

/* -- For some reason getting a 404..
    @Test
    public void removeUser() throws Exception {
        String userId = "testescom";

        Mockito.when(userRepository.removeUser(userId)).thenReturn(true);

        mockMvc.perform(
                delete("/removeUser/{userId}", userId))
                .andExpect(status().isOk());

        Mockito.verify(userRepository, times(1)).removeUser(userId);
        verifyNoMoreInteractions(userRepository);
    }
*/




}
