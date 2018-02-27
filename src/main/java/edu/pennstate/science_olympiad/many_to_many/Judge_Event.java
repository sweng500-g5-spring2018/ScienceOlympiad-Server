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

    private String judgeId;
    private String eventId;

    public Judge_Event(String eventId, String judgeId) {
        this.judgeId = judgeId;
        this.eventId = eventId;
    }

    public String getJudge() {
        return judgeId;
    }

    public String getEvent() {
        return eventId;
    }
}
