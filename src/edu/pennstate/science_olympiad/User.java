package edu.pennstate.science_olympiad;


import edu.pennstate.science_olympiad.util.Pair;
import sun.security.util.Password;

import java.math.BigDecimal;
import java.util.List;

public class User {

    private String firstName;
    private String lastName;
    private Password password;
    private BigDecimal salt;
    private AccessLevel accessLevel;

    private List<Pair<Event, BigDecimal>> scoresList;

}
