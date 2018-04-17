package controller_tests;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.School;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.controllers.SchoolController;
import edu.pennstate.science_olympiad.controllers.TeamController;
import edu.pennstate.science_olympiad.people.*;
import edu.pennstate.science_olympiad.repositories.SchoolRepository;
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

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
@WebAppConfiguration
public class SchoolControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SchoolController schoolController;

    private Gson gson;

    @Before
    public void setup() throws Exception {
        gson = new Gson();
        //set up the controller want to test
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(schoolController)
                .build();
    }

    @Test
    public void getSchools() throws Exception {
        List<School> schools = new ArrayList<School>();
        schools.add(new School("TestSchool"));
        Mockito.when(schoolRepository.getAllSchools()).thenReturn(schools);
        //userRepository needs to be mocked to be used here
        mockMvc.perform(get("/getSchools"))
                .andExpect(status().isOk());
    }

    @Test
    public void addSchool() throws Exception {
        gson = new Gson();
        School school = new School("TestSchool");
        String schoolJson = gson.toJson(school);
        Mockito.when(schoolRepository.addNewSchool(any(School.class))).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addSchool").content(schoolJson))
                .andExpect(status().isOk());

        //not added
        Mockito.when(schoolRepository.addNewSchool(any(School.class))).thenReturn(false);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addSchool").content(schoolJson))
                .andExpect(status().isConflict());

        //exception
        Mockito.when(schoolRepository.addNewSchool(any(School.class))).thenThrow(Exception.class);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addSchool").content(schoolJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addSchoolWithCoach() throws Exception {
        gson = new Gson();
        School school = new School("TestSchool");
        String schoolJson = gson.toJson(school);
        Coach coach = new Coach();
        String coachJson = gson.toJson(coach);
        Mockito.when(userRepository.getUser(any(String.class))).thenReturn(coach);
        Mockito.when(schoolRepository.addNewSchool(any(School.class))).thenReturn(true);
        Mockito.when(userRepository.addSchoolToUser(any(School.class), any(Coach.class))).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addSchoolWithCoach").content(schoolJson).content(coachJson))
                .andExpect(status().isOk());

        Mockito.when(schoolRepository.addNewSchool(any(School.class))).thenReturn(false);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addSchoolWithCoach").content(schoolJson).content(coachJson))
                .andExpect(status().isConflict());

        Mockito.when(schoolRepository.addNewSchool(any(School.class))).thenThrow(Exception.class);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/addSchoolWithCoach").content(schoolJson).content(coachJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void removeSchool() throws Exception {
        String schoolId = "123456789012345678901234";
        Mockito.when(schoolRepository.removeSchool(schoolId)).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/removeSchool/{SchoolID}", schoolId))
                .andExpect(status().isOk());

        //not removed
        Mockito.when(schoolRepository.removeSchool(schoolId)).thenReturn(false);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/removeSchool/{SchoolID}", schoolId))
                .andExpect(status().isConflict());

        //an exception
        Mockito.when(schoolRepository.removeSchool(schoolId)).thenThrow(Exception.class);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/removeSchool/{SchoolID}", schoolId))
                .andExpect(status().isInternalServerError());

        //a bad id
        schoolId = "1234567834";
        Mockito.when(schoolRepository.removeSchool(schoolId)).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/removeSchool/{SchoolID}", schoolId))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateSchool() throws Exception {
        gson = new Gson();
        String schoolId = "123456789012345678901234";
        School school = new School("TestSchool");
        String schoolJson = gson.toJson(school);
        Mockito.when(schoolRepository.updateSchool(schoolId, schoolJson)).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/updateSchool/{SchoolID}", schoolId).content(schoolJson))
                .andExpect(status().isOk());

        //not removed
        Mockito.when(schoolRepository.updateSchool(schoolId, schoolJson)).thenReturn(false);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/updateSchool/{SchoolID}", schoolId).content(schoolJson))
                .andExpect(status().isConflict());

        //an exception
        Mockito.when(schoolRepository.updateSchool(schoolId, schoolJson)).thenThrow(Exception.class);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/updateSchool/{SchoolID}", schoolId).content(schoolJson))
                .andExpect(status().isInternalServerError());

        //a bad id
        schoolId = "1234567834";
        Mockito.when(schoolRepository.updateSchool(schoolId, schoolJson)).thenReturn(true);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/updateSchool/{SchoolID}", schoolId).content(schoolJson))
                .andExpect(status().isBadRequest());
    }
}