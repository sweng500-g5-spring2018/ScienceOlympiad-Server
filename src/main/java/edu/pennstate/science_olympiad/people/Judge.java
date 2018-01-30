package edu.pennstate.science_olympiad.people;

import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.ImproperCreationException;
import edu.pennstate.science_olympiad.Olympiad;
import edu.pennstate.science_olympiad.many_to_many.Judge_Event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A judge is someone who is judging one of the {@link edu.pennstate.science_olympiad.Event}s
 */
public class Judge extends AUser {

    private List<Judge_Event> judge_events;

    Judge(Admin admin) {
        super();
        if (admin == null)
            throw new ImproperCreationException();

        judge_events = new ArrayList<Judge_Event>();
    }

    public List<Judge_Event> getJudge_events() {
        return judge_events;
    }

    public void addEvent(Event event) {
        judge_events.add(new Judge_Event(this, event));
    }

    public boolean removeEvent(Event event) {
        for (Iterator<Judge_Event> judge_event_Iter = judge_events.iterator(); judge_event_Iter.hasNext();) {
            if (judge_event_Iter.next().getEvent() == event) {
                Olympiad.getInstance().getEvents().remove(event);
                judge_event_Iter.remove();
                return true;
            }
        }
        return false;
    }
}
