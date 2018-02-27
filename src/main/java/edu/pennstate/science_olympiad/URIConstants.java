package edu.pennstate.science_olympiad;

public interface URIConstants {

    //-----------------------------Building URIs--------------------------
    String GET_BUILDINGS            = "/getBuildings";
    String ADD_BUILDING             = "/addBuilding";
    String REMOVE_BUILDING          = "/removeBuilding/{buildingID}";
    String UPDATE_BUILDING          = "/updateBuilding/{buildingID}";

    //-----------------------------Event URIs-----------------------------
    String GET_EVENTS               = "/events";
    String NEW_EVENT                = "/addEvent";
    String ASSIGN_JUDGE_TO_EVENT    = "/assignJudgeToEvent";
    String VERIFY_EVENT             = "/verifyEvent/{eventName}";
    String REMOVE_EVENT             = "/removeEvent";
    String UPDATE_EVENT             = "/updateEvent";
    String GET_AN_EVENT             = "/event/{eventId}";

    //-----------------------------First Controller URIs-----------------------------
    //none

    //-----------------------------School URIs-----------------------------
    String GET_SCHOOLS              = "/getSchools";
    String ADD_SCHOOL               = "/addSchool";
    String ADD_SCHOOL_WITH_COACH    = "/addSchoolWithCoach";
    String REMOVE_SCHOOL            = "/removeSchool/{schoolID}";
    String UPDATE_SCHOOL            = "/updateSchool/{schoolID}";

    //-----------------------------Team URIs-----------------------------
    String GET_TEAMS                = "/getTeams";
    String ADD_TEAM                 = "/addTeam";
    String ADD_STUDENT_TO_TEAM      = "/addStudentToTeam";
    String ADD_COACH_TO_TEAM        = "/addCoachToTeam";
    String REMOVE_TEAM              = "/removeTeam";
    String UPDATE_TEAM              = "/updateTeam";
    String REMOVE_STUDENT_FROM_TEAM = "/removeStudentFromTeam";
    String REMOVE_STUDENT_FROM_SPECIFIC_TEAM = "/removeStudentFromSpecificTeam";

    //-----------------------------Users URIs-----------------------------
    String TEST_USER                = "/createTestUser";
    String USERS                    = "/users";
    String ALL_USERS                = "/allUsers";
    String GET_JUDGES               = "/getJudges";
    String REMOVE_USER              = "/removeUser";
    String UPDATE_USER              = "/updateUser";
    String REMOVE_USERS             = "/removeUsers";
    String EMAIL_AVAILABLE          = "/emailAvailable";
    String ADD_USER                 = "/addUser";
    String ADD_COACH_TO_STUDENT     = "/addCoachToStudent";
    String ADD_SCHOOL_TO_COACH      = "/addSchoolToCoach";
    String RESET_PASSWORD           = "/resetPassword";
    String CHANGE_PASSWORD          = "/changePassword";
    String VALIDATE                 = "/validate";
    String GET_USER_PROFILE         = "/getUserProfile";

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
                                        "/resetPassword",
                                        "/getJudges",
                                        "/removeBuilding/**",
                                        "/removeEvent",
                                        "/removeSchool/**",
                                        "/removeTeam",
                                        "/removeUser"
                                       };


}
