import com.google.gson.Gson;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.controllers.EventController;
import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.helpers.request.LoginJsonHelper;
import edu.pennstate.science_olympiad.many_to_many.Team_Event;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Judge;
import edu.pennstate.science_olympiad.repositories.EventRepository;
import edu.pennstate.science_olympiad.repositories.SchoolRepository;
import edu.pennstate.science_olympiad.services.EventService;
import edu.pennstate.science_olympiad.services.TeamService;
import edu.pennstate.science_olympiad.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.any;
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

        //event not found
       Mockito.when(eventRepository.getEvent(eventId)).thenReturn(null);
        MvcResult result5 = mockMvc.perform(MockMvcRequestBuilders.get("/event/{eventId}",eventId))
               .andExpect(status().isConflict())
                .andReturn();

    }

    @Test
    public void getEventJudges() throws Exception {
        String eventId="123456789012345678901234";
        List<Judge> judges = new ArrayList<Judge>();
        judges.add(new Judge());
        Mockito.when(eventService.getEventJudges(eventId)).thenReturn(judges);
        MvcResult result4 = mockMvc.perform(MockMvcRequestBuilders.get("/event/judges/{eventId}",eventId))
                .andExpect(status().isOk())
                .andReturn();

    }

    /** Insert scoring tests here, endpoints may change so wait until the feature is finished  **/

    @Test
    public void getEventTeams() throws Exception {
        String eventId="123456789012345678901234";
        List<Team> teams = new ArrayList<Team>();
        teams.add(new Team(new Coach()));
        Mockito.when(eventService.getEventTeams(eventId)).thenReturn(teams);
        MvcResult result4 = mockMvc.perform(MockMvcRequestBuilders.get("/event/teams/{eventId}",eventId))
                .andExpect(status().isOk())
                .andReturn();

    }


    @Test
    public void registerTeamForEvent() throws Exception {
        String nameJson = gson.toJson("hellooo");

        String eventId="123456789012345678901234";
        String teamId="123456789012345678901235";
        Mockito.when(eventRepository.registerTeamToEvent(teamId,eventId, null, null)).thenReturn(true);
        MvcResult result4 = mockMvc.perform(MockMvcRequestBuilders.post("/event/{eventId}/{teamId}",eventId,teamId).content(nameJson))
                .andExpect(status().isOk())
                .andReturn();

        //not added
        Mockito.when(eventRepository.registerTeamToEvent(teamId,eventId, null, null)).thenReturn(false);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/event/{eventId}/{teamId}",eventId,teamId).content(nameJson))
                .andExpect(status().isConflict())
                .andReturn();

        //bad team id
        teamId="1234501235";
        Mockito.when(eventRepository.registerTeamToEvent(teamId,eventId, null, null)).thenReturn(false);
        MvcResult result1 = mockMvc.perform(MockMvcRequestBuilders.post("/event/{eventId}/{teamId}",eventId,teamId).content(nameJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        //bad event id
        teamId="123456789012345678901235";
        eventId="1234501235";
        Mockito.when(eventRepository.registerTeamToEvent(teamId,eventId, null, null)).thenReturn(false);
        MvcResult resul2t = mockMvc.perform(MockMvcRequestBuilders.post("/event/{eventId}/{teamId}",eventId,teamId).content(nameJson))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @Test
    public void deleteTeamFromEvent() throws Exception {
        String eventId="123456789012345678901234";
        String teamId= "123456789012345678901235";
        Mockito.when(eventRepository.removeTeamFromEvent(eventId,teamId)).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/event/{eventId}/removeTeam/{teamId}",eventId,teamId))
                .andExpect(status().isOk());

        //not removed
        Mockito.when(eventRepository.removeTeamFromEvent(eventId,teamId)).thenReturn(false);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/event/{eventId}/removeTeam/{teamId}",eventId,teamId))
                .andExpect(status().isConflict());


        //an exception
        Mockito.when(eventRepository.removeTeamFromEvent(eventId,teamId)).thenThrow(Exception.class);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/event/{eventId}/removeTeam/{teamId}",eventId,teamId))
                .andExpect(status().isInternalServerError());


        //a bad team id
        teamId = "1234567834";
        Mockito.when(eventRepository.removeTeamFromEvent(eventId,teamId)).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/event/{eventId}/removeTeam/{teamId}",eventId,teamId))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void deleteJudgeFromEvent() throws Exception {
        String eventId="123456789012345678901234";
        String judgeId="123456789012345678901235";
        Mockito.when(eventRepository.removeJudgeFromEvent(eventId,judgeId)).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/event/{eventId}/removeJudge/{judgeId}",eventId,judgeId))
                .andExpect(status().isOk());

        //not removed
        Mockito.when(eventRepository.removeJudgeFromEvent(eventId,judgeId)).thenReturn(false);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/event/{eventId}/removeJudge/{judgeId}",eventId,judgeId))
                .andExpect(status().isConflict());


        //an exception
        Mockito.when(eventRepository.removeJudgeFromEvent(eventId,judgeId)).thenThrow(Exception.class);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/event/{eventId}/removeJudge/{judgeId}",eventId,judgeId))
                .andExpect(status().isInternalServerError());


        //a bad team id
        judgeId = "1234567834";
        Mockito.when(eventRepository.removeJudgeFromEvent(eventId,judgeId)).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/event/{eventId}/removeJudge/{judgeId}",eventId,judgeId))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void addScoreToEvent() throws Exception {
        String teamEventId="123456789012345678901234";
        String score = "10";
        String scoreJson = "{\"score\":\"10\"}";
        Mockito.when(eventRepository.addScoreToTeamEvent(any(Double.class),any(String.class))).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addScore/{teamEventId}}",teamEventId).content(scoreJson))
                .andExpect(status().isOk());

        Mockito.when(eventRepository.addScoreToTeamEvent(any(Double.class),any(String.class))).thenReturn(false);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addScore/{teamEventId}}",teamEventId).content(scoreJson))
                .andExpect(status().isConflict());


    }

    @Test
    public void getScoresForEvent() throws Exception {
        List<Team_Event> te = new ArrayList<Team_Event>();
        String eventId="123456789012345678901234";
        te.add(new Team_Event("123456789012345678901234","123456789012345678901234","team1","event1"));
        Mockito.when(eventRepository.getAllScoresForEvent("123456789012345678901234")).thenReturn(te);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/getEventScores{eventId}",eventId))
                .andExpect(status().isOk());

        Mockito.when(eventRepository.getAllScoresForEvent("123456789012345678901234")).thenReturn(null);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/getEventScores{eventId}",eventId))
                .andExpect(status().isConflict());

    }


    @Test
    public void getScoresForTeam() throws Exception {
        List<Team_Event> te = new ArrayList<Team_Event>();
        String teamId="123456789012345678901234";
        te.add(new Team_Event("123456789012345678901234","123456789012345678901234","team1","event1"));
        Mockito.when(eventRepository.getAllScoresForTeam("123456789012345678901234")).thenReturn(te);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/getTeamScores{teamId}",teamId))
                .andExpect(status().isOk());

        Mockito.when(eventRepository.getAllScoresForTeam("123456789012345678901234")).thenReturn(null);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/getTeamScores{teamId}",teamId))
                .andExpect(status().isConflict());

    }


}
