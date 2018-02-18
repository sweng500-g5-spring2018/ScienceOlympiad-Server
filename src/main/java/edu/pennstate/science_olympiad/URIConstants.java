package edu.pennstate.science_olympiad;

public interface URIConstants {
    //-----------------------------Event URIs-----------------------------

    String GET_EVENTS               = "/events";
    String NEW_EVENT                = "/addEvent";
    String ASSIGN_JUDGE_TO_EVENT    = "/assignJudgeToEvent";
    String REMOVE_EVENT             = "/removeEvent";
    String UPDATE_EVENT             = "/updateEvent";

    //-----------------------------First Controller URIs-----------------------------
    //none

    //-----------------------------School URIs-----------------------------
    String GET_SCHOOLS              = "/getSchools";
    String ADD_SCHOOL               = "/addSchool";
    String ADD_SCHOOL_WITH_COACH    = "/addSchoolWithCoach";
    String REMOVE_SCHOOL            = "/removeSchool/{schoolID}";
    String UPDATE_SCHOOL            = "/updateSchool";

    //-----------------------------Team URIs-----------------------------
    String GET_TEAMS                = "/getTeams";
    String ADD_TEAM                 = "/addTeam";
    String ADD_STUDENT_TO_TEAM      = "/addStudentToTeam";
    String ADD_COACH_TO_TEAM        = "/addCoachToTeam";
    String REMOVE_TEAM              = "/removeTeam";
    String UPDATE_TEAM              = "/updateTeam";
    String REMOVE_STUDENT_FROM_TEAM = "/removeStudentFromTeam";

    //-----------------------------Users URIs-----------------------------
    String TEST_USER                = "/createTestUser";
    String USERS                    = "/users";
    String ALL_USERS                = "/allUsers";
    String REMOVE_USER              = "/removeUser";
    String UPDATE_USER              = "/updateUser";
    String REMOVE_USERS             = "/removeUsers";
    String EMAIL_AVAILABLE          = "/emailAvailable";
    String ADD_USER                 = "/addUser";
    String ADD_COACH_TO_STUDENT     = "/addCoachToStudent";
    String ADD_SCHOOL_TO_COACH      = "/addSchoolToCoach";
    String RESET_PASSWORD           = "/resetPassword";

    //-----------------------------Auth URIs-----------------------------
    String LOGIN                    = "/auth/login";
    String LOGOUT                   = "/auth/logout";


    //-----------------------------PUBLIC URIs-----------------------------
    String [] PUBLIC_URIS           = {
                                        "/auth/**",
                                        "/emailAvailable",
                                        "/users",
                                        "/testSessionStart",
                                        "/addUser/**",
                                        "/getSchools/**",
                                        "/removeSchool/**",
                                        "/resetPassword"
                                       };


}
