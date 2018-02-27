package edu.pennstate.science_olympiad.people;

import edu.pennstate.science_olympiad.Event;
import edu.pennstate.science_olympiad.helpers.request.NewJudgeHelper;
import edu.pennstate.science_olympiad.many_to_many.Judge_Event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A judge is someone who is judging one of the {@link edu.pennstate.science_olympiad.Event}s
 */
public class Judge extends AUser {

   // private List<Judge_Event> judge_events;

    public Judge() {
        super();
    }

    /**
     * Creates a new judge object based on json from event creation
     * @param helper
     */

    public void copyInfoFromJson(NewJudgeHelper helper) {
        this.setFirstName(helper.getFname());
        this.setLastName(helper.getFname());
        this.setEmailAddress(helper.getEmail());

    }

}
