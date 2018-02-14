package edu.pennstate.science_olympiad;

public interface URIConstants {
    //-----------------------------Event URIs-----------------------------

    String GET_EVENTS               = "/events";
    String NEW_EVENT                = "/addEvent";
    String ASSIGN_JUDGE_TO_EVENT    = "/assignJudgeToEvent";

    //-----------------------------First Controller URIs-----------------------------
    //none

    //-----------------------------School URIs-----------------------------
    String GET_SCHOOLS              = "/getSchools";
    String ADD_SCHOOL               = "/addSchool";
    String ADD_SCHOOL_WITH_COACH    = "/assSchoolWithCoach";
    String REMOVE_SCHOOL            = "/removeSchool";

    //-----------------------------Team URIs-----------------------------
    String GET_TEAMS                = "/getTeams";
    String ADD_TEAM                 = "/addTeam";
    String ADD_STUDENT_TO_TEAM      = "/addStudentToTeam";
    String ADD_COACH_TO_TEAM        = "/addCoachToTeam";
    String REMOVE_TEAM              = "/removeTeam";
    String REMOVE_STUDENT_FROM_TEAM = "/removeStudentFromTeam";

    //-----------------------------Users URIs-----------------------------
    String TEST_USER                = "/createTestUser";
    String USERS                    = "/users";
    String ALL_USERS                = "/allUsers";
    String REMOVE_USERS             = "/removeUsers";
    String LOGIN                    = "/login";
    String EMAIL_AVAILABLE          = "/emailAvailable";
    String ADD_USER                 = "/addUser";
    String ADD_COACH_TO_STUDENT     = "/addCoachToStudent";
    String ADD_SCHOOL_TO_COACH      = "/addSchoolToCoach";
}
