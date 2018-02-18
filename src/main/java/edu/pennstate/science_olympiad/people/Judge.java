package edu.pennstate.science_olympiad.people;

import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.many_to_many.Judge_Event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A judge is someone who is judging one of the {@link edu.pennstate.science_olympiad.Event}s
 */
public class Judge extends AUser {

    private List<Judge_Event> judge_events;

    public Judge() {
        super();
    }

    public List<Judge_Event> getJudge_events() {
        if (judge_events == null)
            judge_events = new ArrayList<Judge_Event>();
        return judge_events;
    }

    public void addEvent(Event event) {
        getJudge_events().add(new Judge_Event(this, event));
    }

    public boolean removeEvent(Event event) {
        for (Iterator<Judge_Event> judge_event_Iter = judge_events.iterator(); judge_event_Iter.hasNext();) {
            if (judge_event_Iter.next().getEvent() == event) {
                judge_event_Iter.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public void copyInfo(AUser aUser) {
        super.copyInfo(aUser);
        this.judge_events = ((Judge)aUser).getJudge_events();
    }
}
