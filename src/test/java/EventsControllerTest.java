import com.google.gson.Gson;
import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.controllers.EventController;
import edu.pennstate.science_olympiad.controllers.UsersController;
import edu.pennstate.science_olympiad.people.AUser;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.people.Student;
import edu.pennstate.science_olympiad.repositories.EventRepository;
import edu.pennstate.science_olympiad.repositories.SchoolRepository;
import edu.pennstate.science_olympiad.repositories.UserRepository;
import edu.pennstate.science_olympiad.services.EventService;
import edu.pennstate.science_olympiad.services.TeamService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
@WebAppConfiguration
public class EventsControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventService eventService;

    @Mock
    private TeamService teamService;

    @Mock
    private SchoolRepository schoolRepository;

    @InjectMocks
    private EventController eventController;

    private Gson gson;

    @Before
    public void setup() throws Exception {
        gson = new Gson();
        //set up the controller want to test
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(eventController)
                .build();
    }

    @Test
    public void getEvents() throws Exception{
        List<Event> ev = new ArrayList<Event>();
        Event e = new Event();
        ev.add(e);
        Mockito.when(eventService.getEvents()).thenReturn(ev);
        //userRepository needs to be mocked to be used here
        MvcResult result = mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))).andReturn();
    }

    /**
     * Testing the addEvent endpoint
     * @throws Exception
     */
    @Test
    public void addEvent() throws Exception{
        Event event = new Event();
        String eventJson = gson.toJson(event);
        Mockito.when(eventService.addEvent(eventJson)).thenReturn(true);
        //userRepository needs to be mocked to be used here
        MvcResult result = mockMvc.perform(post("/addEvent").content(eventJson))
                .andExpect(status().isOk())
                .andReturn();

        //did not add event
        Mockito.when(eventService.addEvent(eventJson)).thenReturn(false);
        //userRepository needs to be mocked to be used here
        MvcResult result2 = mockMvc.perform(post("/addEvent").content(eventJson))
                .andExpect(status().isConflict())
                .andReturn();

        //bad json
        Mockito.when(eventService.addEvent(eventJson)).thenThrow(Exception.class);
        //userRepository needs to be mocked to be used here
        MvcResult result3 = mockMvc.perform(post("/addEvent").content(eventJson))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void verifyEvent() throws Exception {
        String eventName="unique";

        Mockito.when(eventRepository.verifyEventUnique(eventName)).thenReturn(true);
        //userRepository needs to be mocked to be used here
        MvcResult result1 = mockMvc.perform(get("/verifyEvent/{eventName}",eventName))
                .andExpect(status().isOk())
                .andReturn();

        //not unique
        Mockito.when(eventRepository.verifyEventUnique(eventName)).thenReturn(false);
        //userRepository needs to be mocked to be used here
        MvcResult result2 = mockMvc.perform(get("/verifyEvent/{eventName}",eventName))
                .andExpect(status().isConflict())
                .andReturn();

        //not unique
        Mockito.when(eventRepository.verifyEventUnique(eventName)).thenThrow(Exception.class);
        MvcResult result3 = mockMvc.perform(get("/verifyEvent/{eventName}",eventName))
                .andExpect(status().isBadRequest())
                .andReturn();


    }

    @Test
    public void deleteEvent() throws Exception {
        String userId = "123456789012345678901234";
        Mockito.when(eventService.removeEvent(userId)).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/removeEvent/{eventId}",userId))
                .andExpect(status().isOk());

        //not removed
        Mockito.when(eventService.removeEvent(userId)).thenReturn(false);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/removeEvent/{eventId}",userId))
                .andExpect(status().isConflict());

        //an exception
        Mockito.when(eventService.removeEvent(userId)).thenThrow(Exception.class);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/removeEvent/{eventId}",userId))
                .andExpect(status().isInternalServerError());

        //a bad id
        userId = "1234567834";
        Mockito.when(eventService.removeEvent(userId)).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/removeEvent/{eventId}",userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateEvent() throws Exception {
        String eventId="123456789012345678901234";
        Event event = new Event();
        event.setName("test 1");
        gson = new Gson();
        String eventJson = gson.toJson(event);

        Mockito.when(eventService.updateEvent(eventId,eventJson)).thenReturn(true);
        //Send the fake session with a user to return
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/updateEvent/{eventId}",eventId)
                .content(eventJson))
                .andExpect(status().isOk())
                .andReturn();

        //not updated
        Mockito.when(eventService.updateEvent(eventId,eventJson)).thenReturn(false);
        //Send the fake session with a user to return
        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders.post("/updateEvent/{eventId}",eventId)
                .content(eventJson))
                .andExpect(status().isConflict())
                .andReturn();

        //some exception
        Mockito.when(eventService.updateEvent(eventId,eventJson)).thenThrow(Exception.class);
        //Send the fake session with a user to return
        MvcResult result3 = mockMvc.perform(MockMvcRequestBuilders.post("/updateEvent/{eventId}",eventId)
                .content(eventJson))
                .andExpect(status().isInternalServerError())
                .andReturn();

        //bad id
        eventId="12345678901231234";
        Mockito.when(eventService.updateEvent(eventId,eventJson)).thenThrow(Exception.class);
        //Send the fake session with a user to return
        MvcResult result4 = mockMvc.perform(MockMvcRequestBuilders.post("/updateEvent/{eventId}",eventId)
                .content(eventJson))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void getAnEvent() throws Exception {
        String eventId="123456789012345678901234";
        Mockito.when(eventRepository.getEvent(eventId)).thenReturn(new Event());
        MvcResult result4 = mockMvc.perform(MockMvcRequestBuilders.get("/event/{eventId}",eventId))
                .andExpect(status().isOk())
                .andReturn();

        //event not found, can't return a null
       // Event event = null;
      //  Mockito.when(eventRepository.getEvent(eventId)).thenReturn(event);
       // MvcResult result5 = mockMvc.perform(MockMvcRequestBuilders.get("/event/{eventId}",eventId))
            //    .andExpect(status().isConflict())
              //  .andReturn();

    }

}
