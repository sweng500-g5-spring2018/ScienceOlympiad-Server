package controller_tests;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.Building;
import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.controllers.BuildingController;
import edu.pennstate.science_olympiad.helpers.mongo.MongoIdVerifier;
import edu.pennstate.science_olympiad.people.AUser;
import edu.pennstate.science_olympiad.people.Admin;
import edu.pennstate.science_olympiad.people.UserFactory;
import edu.pennstate.science_olympiad.repositories.BuildingRepository;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes= SpringConfig.class)
@WebAppConfiguration
public class BuildingControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    Gson gson = new Gson();
    private Admin brandon;

    @Mock
    BuildingRepository buildingRepository;

    @Mock
    static MongoIdVerifier mongoIdVerifier;

    @InjectMocks
    private BuildingController buildingController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(buildingController)
                .build();

        brandon  = (Admin) UserFactory.getInstance().createUser("admin");
        brandon.setSiteName("Penn State");
        brandon.setFirstName("Brandon");
        brandon.setLastName("Hessler");
    }

    @Test
    public void getBuildings() throws Exception{
        List<Building> buildings = new ArrayList<Building>();
        Building building1 = new Building();
        building1.setBuilding("Building One");
        building1.setLat(41.2033);
        building1.setLng(77.1945);

        Building building2 = new Building();
        building2.setBuilding("Building of Love");
        building2.setLat(43.9787);
        building2.setLng(15.3846);

        buildings.add(building1);
        buildings.add(building2);

        //userRepository needs to be mocked to be used here
        Mockito.when(buildingRepository.getAllBuildings()).thenReturn(buildings);
        MvcResult result = mockMvc.perform(get("/getBuildings"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].building",is("Building One")))
                .andExpect(jsonPath("$[0].lat", is(41.2033)))
                .andExpect(jsonPath("$[0].lng", is(77.1945)))
                .andExpect(jsonPath("$[1].building",is("Building of Love")))
                .andExpect(jsonPath("$[1].lat", is(43.9787)))
                .andExpect(jsonPath("$[1].lng", is(15.3846)))
                .andReturn();

        //only executed once
        Mockito.verify(buildingRepository, times(1)).getAllBuildings();
        verifyNoMoreInteractions(buildingRepository);

    }

    @Test
    public void addBuilding() throws Exception{
        //Add a building, this should work
        Building building1 = new Building();
        building1.setBuilding("Building One");
        building1.setLat(41.2033);
        building1.setLng(77.1945);

//        Building building2 = new Building();
//        building2.setBuilding("Building of Love");
//        building2.setLat(43.9787);
//        building2.setLng(15.3846);

        String buildingJson = gson.toJson(building1);
        Mockito.when(buildingRepository.addNewBuilding(any(Building.class))).thenReturn(true);

        MvcResult good = mockMvc.perform(MockMvcRequestBuilders.post("/addBuilding")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(buildingJson))

                .andExpect(status().isOk())
                .andReturn();

        //This will fail with a conflict
        Mockito.when(buildingRepository.addNewBuilding(any(Building.class))).thenReturn(false);

        MvcResult conflict = mockMvc.perform(MockMvcRequestBuilders.post("/addBuilding")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(buildingJson))

                .andExpect(status().isConflict())
                .andReturn();

        //This will fail with a malformed JSON
        String notABuildingJson = gson.toJson(brandon, AUser.class);

        MvcResult badRequest = mockMvc.perform(MockMvcRequestBuilders.post("/addBuilding")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(notABuildingJson))

                .andExpect(status().isBadRequest())
                .andReturn();

        //This will throw an exception
        Mockito.when(buildingRepository.addNewBuilding(any(Building.class))).thenThrow(new NullPointerException());

        MvcResult notAcceptable = mockMvc.perform(MockMvcRequestBuilders.post("/addBuilding")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(buildingJson))

                .andExpect(status().isNotAcceptable())
                .andReturn();
    }

    @Test
    public void removeSchool() throws Exception{
        //This building does not have a valid ID

        MvcResult invalid = mockMvc.perform(MockMvcRequestBuilders.delete("/removeBuilding/1")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})))

                .andExpect(status().isBadRequest())
                .andReturn();

        //This building does not exist
        Mockito.when(buildingRepository.removeBuilding(any(String.class))).thenReturn(false);

        MvcResult doesntExist = mockMvc.perform(MockMvcRequestBuilders.delete("/removeBuilding/123456789012345678901234")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})))

                .andExpect(status().isConflict())
                .andReturn();

        //This should pass and the building removed
        Mockito.when(buildingRepository.removeBuilding(any(String.class))).thenReturn(true);

        MvcResult works = mockMvc.perform(MockMvcRequestBuilders.delete("/removeBuilding/123456789012345678901234")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})))

                .andExpect(status().isOk())
                .andReturn();


        //An exception was thrown
        Mockito.when(buildingRepository.removeBuilding(any(String.class))).thenThrow(new NullPointerException());

        MvcResult serverError = mockMvc.perform(MockMvcRequestBuilders.delete("/removeBuilding/123456789012345678901234")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})))

                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updateBuilding() throws Exception {
        //This building does not have a valid ID

        Building building1 = new Building();
        building1.setBuilding("Building One");
        building1.setLat(41.2033);
        building1.setLng(77.1945);
        String buildingJson = gson.toJson(building1, Building.class);

        MvcResult invalid = mockMvc.perform(MockMvcRequestBuilders.post("/updateBuilding/1")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }
                })).content(buildingJson))

                .andExpect(status().isBadRequest())
                .andReturn();

        //This building does not exist
        Mockito.when(buildingRepository.updateBuilding(any(String.class), any(String.class))).thenReturn(false);

        MvcResult doesntExist = mockMvc.perform(MockMvcRequestBuilders.post("/updateBuilding/123456789012345678901234")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }
                })).content(buildingJson))

                .andExpect(status().isConflict())
                .andReturn();

        //This should pass and the building removed
        Mockito.when(buildingRepository.updateBuilding(any(String.class), any(String.class))).thenReturn(true);

        MvcResult works = mockMvc.perform(MockMvcRequestBuilders.post("/updateBuilding/123456789012345678901234")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }
                })).content(buildingJson))

                .andExpect(status().isOk())
                .andReturn();


        //An exception was thrown
        Mockito.when(buildingRepository.updateBuilding(any(String.class), any(String.class))).thenThrow(new NullPointerException());

        MvcResult serverError = mockMvc.perform(MockMvcRequestBuilders.post("/updateBuilding/123456789012345678901234")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }
                })).content(buildingJson))

                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void getABuilding() throws Exception{
        Building building1 = new Building();
        building1.setBuilding("Building One");
        building1.setLat(41.2033);
        building1.setLng(77.1945);

        //Invalid Building ID
        MvcResult invalid = mockMvc.perform(MockMvcRequestBuilders.get("/getBuilding/1")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})))

                .andExpect(status().isBadRequest())
                .andReturn();

        //Building not found
        Mockito.when(buildingRepository.getBuilding(any(String.class))).thenReturn(null);

        MvcResult notFound = mockMvc.perform(MockMvcRequestBuilders.get("/getBuilding/123456789012345678901234")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})))

                .andExpect(status().isConflict())
                .andReturn();

        //Building not found
        Mockito.when(buildingRepository.getBuilding(any(String.class))).thenReturn(building1);

        MvcResult good = mockMvc.perform(MockMvcRequestBuilders.get("/getBuilding/123456789012345678901234")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})))

                .andExpect(status().isOk())
                .andReturn();
    }





}
