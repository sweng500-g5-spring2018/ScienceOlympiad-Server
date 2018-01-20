package edu.pennstate.science_olympiad;

import edu.pennstate.science_olympiad.people.Coach;
import edu.pennstate.science_olympiad.people.Student;
import edu.pennstate.science_olympiad.util.Pair;

import java.math.BigDecimal;
import java.util.List;

/**
 * This is one of the participants/ competitors in the {@link edu.pennstate.science_olympiad.Olympiad}. A team is
 * sometimes comprised of one {@link edu.pennstate.science_olympiad.User} but other times it will have more.
 */
public class Team {

    //The advisor or coach to the team
    private Coach coach;

    //All of the members of this team
    private List<Student> students;

    // This is a list of Pairs. Each Pair is an event paired with the team's score at that event.
    private List<Pair<Event, BigDecimal>> eventScoreList;

}
