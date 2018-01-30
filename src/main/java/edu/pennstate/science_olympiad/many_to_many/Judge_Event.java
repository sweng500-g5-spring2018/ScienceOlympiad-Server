package edu.pennstate.science_olympiad.many_to_many;


import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.people.Judge;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This is a bridge class for the database between
 * {@link edu.pennstate.science_olympiad.people.Judge}s and {@link edu.pennstate.science_olympiad.Event}s
 */
@Document
public class Judge_Event {
    @Id
    public String id;

    private Judge judge;
    private Event event;

    public Judge_Event(Judge judge, Event event) {
        this.judge = judge;
        this.event = event;
    }

    public Judge getJudge() {
        return judge;
    }

    public Event getEvent() {
        return event;
    }
}
