package controller_tests;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.Building;
import edu.pennstate.science_olympiad.Room;
import edu.pennstate.science_olympiad.config.SpringConfig;
import edu.pennstate.science_olympiad.controllers.RoomController;
import edu.pennstate.science_olympiad.people.Admin;
import edu.pennstate.science_olympiad.people.UserFactory;
import edu.pennstate.science_olympiad.repositories.RoomRepository;
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

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes= SpringConfig.class)
@WebAppConfiguration
public class RoomControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    Gson gson = new Gson();
    private Admin brandon;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomController roomController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(roomController)
                .build();

        brandon  = (Admin) UserFactory.getInstance().createUser("admin");
        brandon.setSiteName("Penn State");
        brandon.setFirstName("Brandon");
        brandon.setLastName("Hessler");
        brandon.setEmailAddress("pennstate@brandonhessler.com");
    }

    @Test
    public void getAllRooms() throws Exception {
        List<Room> rooms = new ArrayList<Room>();
        Room room1 = new Room();
        room1.setBuildingID("123456789012345678901234");
        room1.setCapacity(120);
        room1.setRoomName("Room One");

        Room room2 = new Room();
        room2.setBuildingID("123456789012345678901234");
        room2.setCapacity(42);
        room2.setRoomName("Room Two");

        rooms.add(room1);
        rooms.add(room2);

        //this will be good
        Mockito.when(roomRepository.getAllRooms()).thenReturn(rooms);
        MvcResult result = mockMvc.perform(get("/getAllRooms"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].roomName",is("Room One")))
                .andExpect(jsonPath("$[0].capacity", is(120)))
                .andExpect(jsonPath("$[1].roomName",is("Room Two")))
                .andExpect(jsonPath("$[1].capacity", is(42)))
                .andReturn();

        //only executed once
        Mockito.verify(roomRepository, times(1)).getAllRooms();
        verifyNoMoreInteractions(roomRepository);

        //This will return null
        Mockito.when(roomRepository.getAllRooms()).thenReturn(null);

        MvcResult nullRooms = mockMvc.perform(MockMvcRequestBuilders.get("/getAllRooms")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})))

                .andReturn();

        assert nullRooms.getResponse().getContentType() == null;

        //This will throw an exception and return null
        Mockito.when(roomRepository.getAllRooms()).thenThrow(new NullPointerException());

        MvcResult exception = mockMvc.perform(MockMvcRequestBuilders.get("/getAllRooms")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})))
                .andReturn();

        assert exception.getResponse().getContentType() == null;
    }

    @Test
    public void addRoom() throws Exception {
        Room room1 = new Room();
        room1.id = "123456789012345678901234";
        room1.setBuildingID("123456789012345678901234");
        room1.setCapacity(120);
        room1.setRoomName("Room One");

        String roomJson = gson.toJson(room1);

        //This will throw an exception and return null
        Mockito.when(roomRepository.addNewRoom(any(Room.class))).thenThrow(new NullPointerException());

        MvcResult exception = mockMvc.perform(MockMvcRequestBuilders.post("/addRoom")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(roomJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        //This will return a conflict
        Mockito.when(roomRepository.addNewRoom(any(Room.class))).thenReturn(false);

        MvcResult conflict = mockMvc.perform(MockMvcRequestBuilders.post("/addRoom")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(roomJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        //This will work correctly
        Mockito.when(roomRepository.addNewRoom(any(Room.class))).thenReturn(true);

        MvcResult good = mockMvc.perform(MockMvcRequestBuilders.post("/addRoom")
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(roomJson))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void removeRoom() throws Exception {
        String badRoomId = "1234";
        String roomId = "123456789012345678901234";

        //This will return a bad request
        MvcResult badId = mockMvc.perform(MockMvcRequestBuilders.delete("/removeRoom/" + badRoomId)
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})))
                .andExpect(status().isBadRequest())
                .andReturn();

        assert badId.getResponse().getContentAsString().equals("Bad request, invalid room ID.");

        //This will throw an exception and return null
        Mockito.when(roomRepository.removeRoom(anyString())).thenThrow(new NullPointerException());

        MvcResult exception = mockMvc.perform(MockMvcRequestBuilders.delete("/removeRoom/" + roomId)
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})))
                .andExpect(status().isInternalServerError())
                .andReturn();

        //This will return a conflict
        Mockito.when(roomRepository.removeRoom(anyString())).thenReturn(false);

        MvcResult conflict = mockMvc.perform(MockMvcRequestBuilders.delete("/removeRoom/" + roomId)
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})))
                .andExpect(status().isBadRequest())
                .andReturn();

        //This will work correctly
        Mockito.when(roomRepository.removeRoom(anyString())).thenReturn(true);

        MvcResult good = mockMvc.perform(MockMvcRequestBuilders.delete("/removeRoom/" + roomId)
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void updateRoom() throws Exception {
        Room room1 = new Room();
        room1.setBuildingID("123456789012345678901234");
        room1.setCapacity(120);
        room1.setRoomName("Room One");

        String roomJson = gson.toJson(room1);

        String badRoomId = "1234";
        String roomId = "123456789012345678901234";

        //This will return a bad request
        MvcResult badId = mockMvc.perform(MockMvcRequestBuilders.post("/updateRoom/" + badRoomId)
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(roomJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        assert badId.getResponse().getContentAsString().equals("Bad request, invalid room ID.");

        //This will throw an exception and return null
        Mockito.when(roomRepository.updateRoom(anyString(), anyString())).thenThrow(new NullPointerException());

        MvcResult exception = mockMvc.perform(MockMvcRequestBuilders.post("/updateRoom/" + roomId)
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(roomJson))
                .andExpect(status().isInternalServerError())
                .andReturn();

        //This will return a conflict
        Mockito.when(roomRepository.updateRoom(anyString(), anyString())).thenReturn(false);

        MvcResult conflict = mockMvc.perform(MockMvcRequestBuilders.post("/updateRoom/" + roomId)
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(roomJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        //This will work correctly
        Mockito.when(roomRepository.updateRoom(anyString(), anyString())).thenReturn(true);

        MvcResult good = mockMvc.perform(MockMvcRequestBuilders.post("/updateRoom/" + roomId)
                .with((new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", brandon);
                        return request;
                    }})).content(roomJson))
                .andExpect(status().isOk())
                .andReturn();
    }
}
