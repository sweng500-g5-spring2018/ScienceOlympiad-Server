package edu.pennstate.science_olympiad.services;

import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.many_to_many.Team_Event;
import edu.pennstate.science_olympiad.people.Admin;
import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Student;
import edu.pennstate.science_olympiad.repositories.EventRepository;
import edu.pennstate.science_olympiad.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Bean to control the execution flow of certain business actions related to events
 */
@Service
public class EventService {
    @Autowired
    EventRepository eventRepository;

    public EventRepository getEventRepository(){
        return eventRepository;
    }

    /**
     *
     * @param studentIds - The students selected to add to the team - just testing
     * @param eventName -The event we are adding a team for, used to lookup event
     * @return
     */
    public Object addTeamToEvent(List<String> studentIds, String eventName) {
        //The students and coaches objects should have already been entered into the database so mock them here

        //The coach would be grabbed from the database based on a session of whos logged in
         Coach coach = new Coach();
         eventRepository.saveTestCoach(coach);
        //make db request to get the students with these id's entered in form
         Student student = new Student();
         student.setFirstName("Kyle");
         student.setLastName("Hughes");
         eventRepository.saveTestStudent(student);
         //Create the team based on the gathered coach and the students found from the ids
         Team team = new Team(coach);
         eventRepository.createTeam(team);

         //based on the event name, get the Event already saved in the database ("FirstEvent")
         Event event = eventRepository.getEvent(eventName);

         //now finally create a new team event object
         Team_Event teamEvent = new Team_Event(team,event);

         eventRepository.createTeamEvent(teamEvent);
         return new Pair<String,String>("Success","added a new team to the event");
    }

    /**
     * This will have happened before any teams are added -- just adding one to test
     * @param event
     */
    public void createNewEvent(Event event) {
        //this will also create a new Team_Event Object in the DB
        eventRepository.createNewEvent(event);

    }

    public List<Event> getEvents() {
        return eventRepository.getEvents();
    }
}
