package controller_tests;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.controllers.TeamController;
import edu.pennstate.science_olympiad.people.*;
import edu.pennstate.science_olympiad.repositories.TeamRepository;
import edu.pennstate.science_olympiad.repositories.UserRepository;
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

import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
@WebAppConfiguration
public class TeamControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TeamController teamController;

    private Gson gson;

    @Before
    public void setup() throws Exception {
        gson = new Gson();
        //set up the controller want to test
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(teamController)
                .build();
    }

    @Test
    public void getTeams() throws Exception{
       List<Team> teams = new ArrayList<Team>();
       teams.add(new Team(new Coach()));
        //not removed
        Mockito.when(teamRepository.getTeams()).thenReturn(teams);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/getTeams"))
                .andExpect(status().isOk());
    }

    @Test
    public void getTeamsByUser() throws Exception {
        final Coach coach1 = new Coach();
        //fake teams to return
        List<Team> teams = new ArrayList<Team>();
        teams.add(new Team(new Coach()));
        //Send the fake session with a user to return
        Mockito.when(teamRepository.getTeamByCoach(any(Coach.class))).thenReturn(teams);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/getTeamsByUser")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", coach1);
                        return request;
                    }
                })))
                .andExpect(status().isOk())
                .andReturn();

        //getting admins
        final Admin admin1 = new Admin();
        Mockito.when(teamRepository.getTeams()).thenReturn(teams);
        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders.get("/getTeamsByUser")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", admin1);
                        return request;
                    }
                })))
                .andExpect(status().isOk())
                .andReturn();

        //unauthorized user
        final Judge judge1 = new Judge();
        Mockito.when(teamRepository.getTeams()).thenReturn(teams);
        MvcResult result3 = mockMvc.perform(MockMvcRequestBuilders.get("/getTeamsByUser")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", judge1);
                        return request;
                    }
                })))
                .andExpect(status().isUnauthorized())
                .andReturn();

        //an exception occured
        Mockito.when(teamRepository.getTeamByCoach(any(Coach.class))).thenThrow(Exception.class);
        MvcResult result4 = mockMvc.perform(MockMvcRequestBuilders.get("/getTeamsByUser")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", coach1);
                        return request;
                    }
                })))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void addTeam() throws Exception {
        gson = new Gson();
        Team team = new Team(new Coach());
        String teamJson = gson.toJson(team);
        Mockito.when(teamRepository.addNewTeam(any(Team.class))).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addTeam").content(teamJson))
                .andExpect(status().isOk());

        //not added
        Mockito.when(teamRepository.addNewTeam(any(Team.class))).thenReturn(false);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addTeam").content(teamJson))
                .andExpect(status().isConflict());

        //exception
        Mockito.when(teamRepository.addNewTeam(any(Team.class))).thenThrow(Exception.class);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addTeam").content(teamJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addStudentToTeam() throws Exception {
        gson = new Gson();
    Student student = new Student();
    String studentJson = gson.toJson(student);
    Team team = new Team(new Coach());
    String teamJson = gson.toJson(team);
        Mockito.when(teamRepository.getTeam(any(String.class))).thenReturn(team);
        Mockito.when(userRepository.getUser(any(String.class))).thenReturn(student);
        Mockito.when(teamRepository.addStudentToTeam(student,team)).thenReturn((short) 0);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addStudentToTeam").content(studentJson).content(teamJson))
                .andExpect(status().isOk());

        Mockito.when(teamRepository.addStudentToTeam(student,team)).thenReturn((short) 1);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addStudentToTeam").content(studentJson).content(teamJson))
                .andExpect(status().isConflict());

        Mockito.when(teamRepository.addStudentToTeam(student,team)).thenReturn((short) 2);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addStudentToTeam").content(studentJson).content(teamJson))
                .andExpect(status().isConflict());

        //default case statement
        Mockito.when(teamRepository.addStudentToTeam(student,team)).thenReturn((short) 3);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addStudentToTeam").content(studentJson).content(teamJson))
                .andExpect(status().isBadRequest());

        //Exception
        Mockito.when(teamRepository.addStudentToTeam(student,team)).thenThrow(Exception.class);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addStudentToTeam").content(studentJson).content(teamJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void updateStudentsInTeam() throws Exception {
        gson = new Gson();
        Team team = new Team(new Coach());
        //not worried about the actual contents of this json request since the service handles it and we aren't touching
        //that
        String studentsToTeamJson = gson.toJson(team);
        Mockito.when(teamService.updateTeamWithNewStudents(studentsToTeamJson)).thenReturn(team);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/updateStudentsInTeam").content(studentsToTeamJson))
                .andExpect(status().isOk());

        //its null
        Mockito.when(teamService.updateTeamWithNewStudents(studentsToTeamJson)).thenReturn(null);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/updateStudentsInTeam").content(studentsToTeamJson))
                .andExpect(status().isInternalServerError());

        //exception
        Mockito.when(teamService.updateTeamWithNewStudents(studentsToTeamJson)).thenThrow(Exception.class);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/updateStudentsInTeam").content(studentsToTeamJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addCoachToTeam() throws Exception {
        gson = new Gson();
        Coach coach = new Coach();
        String coachJson = gson.toJson(coach);
        Team team = new Team(new Coach());
        String teamJson = gson.toJson(team);
        Mockito.when(teamRepository.getTeam(any(String.class))).thenReturn(team);
        Mockito.when(userRepository.getUser(any(String.class))).thenReturn(coach);
        Mockito.when(teamRepository.addCoachToTeam(coach,team)).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addCoachToTeam").content(coachJson).content(teamJson))
                .andExpect(status().isOk());

        //team not found
        Mockito.when(teamRepository.addCoachToTeam(coach,team)).thenReturn(false);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addCoachToTeam").content(coachJson).content(teamJson))
                .andExpect(status().isConflict());

        //Exception
        Mockito.when(teamRepository.addCoachToTeam(coach,team)).thenThrow(Exception.class);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addCoachToTeam").content(coachJson).content(teamJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void removeTeam() throws Exception {
        String teamId="123456789012345678901234";
        Team team = new Team(new Coach());
        Mockito.when(teamService.isTeamInEvent(teamId)).thenReturn(false);
        Mockito.when(teamRepository.getTeam(teamId)).thenReturn(team);
        Mockito.when(teamRepository.removeTeam(team)).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/removeTeam/{teamId}",teamId))
                .andExpect(status().isOk());

        //team not found
        Mockito.when(teamRepository.removeTeam(team)).thenReturn(false);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/removeTeam/{teamId}",teamId))
                .andExpect(status().isConflict());

        //team is in an event
        Mockito.when(teamService.isTeamInEvent(teamId)).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/removeTeam/{teamId}",teamId))
                .andExpect(status().isConflict());

        //Exception
        Mockito.when(teamService.isTeamInEvent(teamId)).thenReturn(false);
        Mockito.when(teamRepository.removeTeam(team)).thenThrow(Exception.class);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/removeTeam/{teamId}",teamId))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void removeStudentFromTeam() throws Exception {
        gson = new Gson();
        Student student = new Student();
        String studentJson = gson.toJson(student);
        Team team = new Team(new Coach());
        String teamJson = gson.toJson(team);
        Mockito.when(teamRepository.getTeam(any(String.class))).thenReturn(team);
        Mockito.when(userRepository.getUser(any(String.class))).thenReturn(student);
        Mockito.when(teamRepository.removeStudentFromTeam(student,team)).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/removeStudentFromTeam").content(studentJson).content(teamJson))
                .andExpect(status().isOk());

        //student not removed
        Mockito.when(teamRepository.removeStudentFromTeam(student,team)).thenReturn(false);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/removeStudentFromTeam").content(studentJson).content(teamJson))
                .andExpect(status().isConflict());

        //exception
        Mockito.when(teamRepository.removeStudentFromTeam(student,team)).thenThrow(Exception.class);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/removeStudentFromTeam").content(studentJson).content(teamJson))
                .andExpect(status().isBadRequest());
    }
}
