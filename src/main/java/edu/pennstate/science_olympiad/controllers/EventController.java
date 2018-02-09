package edu.pennstate.science_olympiad.controllers;

import edu.pennstate.science_olympiad.Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.pennstate.science_olympiad.services.EventService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventController {
@Autowired
private EventService eventService;
    Log logger = LogFactory.getLog(getClass());

    /**
     * Return the events to display in the events view
     * @return
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value="events",method= RequestMethod.GET ,produces={MediaType.APPLICATION_JSON_VALUE})
    public Object createTeamEvent() {

        List<Event> events =  eventService.getEvents();
        logger.info("found these events " + events.toString());
        return events;
    }
}
