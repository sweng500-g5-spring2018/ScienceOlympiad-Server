package edu.pennstate.science_olympiad;

public interface URIConstants {

    //-----------------------------Building URIs--------------------------
    String GET_BUILDINGS            = "/getBuildings";
    String ADD_BUILDING             = "/addBuilding";
    String REMOVE_BUILDING          = "/removeBuilding/{buildingID}";
    String UPDATE_BUILDING          = "/updateBuilding/{buildingID}";
    String GET_A_BUILDING           = "/getBuilding/{buildingID}";

    //-----------------------------Event URIs------------------------------
    String GET_EVENTS               = "/events";
    String NEW_EVENT                = "/addEvent";
    String ASSIGN_JUDGE_TO_EVENT    = "/assignJudgeToEvent";
    String VERIFY_EVENT             = "/verifyEvent/{eventName}";
    String REMOVE_EVENT             = "/removeEvent/{eventId}";
    String UPDATE_EVENT             = "/updateEvent/{eventId}";
    String GET_AN_EVENT             = "/event/{eventId}";
    String GET_EVENT_JUDGES         = "/event/judges/{eventId}";
    String ADD_SCORE                = "/addScore/{score}";
    String GET_TEAM_EVENTS          = "/getTeamEvents";
    String GET_SCORES_FOR_TEAM      = "/getTeamScores{teamId}";
    String GET_SCORES_FOR_EVENT     = "/getEventScores{eventId}";
    String GET_EVENT_TEAMS          = "/event/teams/{eventId}";
    String REGISTER_TEAM_FOR_EVENT  ="/event/{eventId}/{teamId}";
    String REMOVE_JUDGE_FROM_EVENT  ="/event/{eventId}/removeJudge/{judgeId}";
    String REMOVE_TEAM_FROM_EVENT   ="/event/{eventId}/removeTeam/{teamId}";

    //-----------------------------First Controller URIs-----------------------------
    //none

    //-----------------------------Room URIs--------------------------
    String GET_ROOMS                = "/getRooms/{buildingID}";
    String GET_ALL_ROOMS            = "/getAllRooms";
    String ADD_ROOM                 = "/addRoom";
    String REMOVE_ROOM              = "/removeRoom/{roomID}";
    String UPDATE_ROOM              = "/updateRoom/{roomID}";

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
    String REMOVE_TEAM              = "/removeTeam/{teamId}";
    String UPDATE_TEAM              = "/updateTeam";
    String REMOVE_STUDENT_FROM_TEAM = "/removeStudentFromTeam";
    String GET_TEAMS_BY_USER        = "/getTeamsByUser";
    String UPDATE_STUDENTS_IN_TEAM  = "/updateStudentsInTeam";

    //-----------------------------Users URIs-----------------------------
    String TEST_USER                = "/createTestUser";
    String USERS                    = "/users";
    String ALL_USERS                = "/allUsers";
    String GET_JUDGES               = "/getJudges";
    String GET_COACHES              = "/getCoaches";
    String REMOVE_USER              = "/removeUser/{userId}";
    String UPDATE_USER              = "/updateUser";
    String REMOVE_USERS             = "/removeUsers";
    String EMAIL_AVAILABLE          = "/emailAvailable";
    String ADD_USER                 = "/addUser";
    String ADD_SCHOOL_TO_USER       = "/addSchoolToUser";
    String RESET_PASSWORD           = "/resetPassword";
    String CHANGE_PASSWORD          = "/changePassword";
    String VALIDATE                 = "/validate";
    String GET_USER_PROFILE         = "/getUserProfile";
    String GET_STUDENTS_FROM_SCHOOL = "/getStudentsFromSchool";
    String DELETE_STUDENT           = "/deleteStudent/{studentId}";

    //-----------------------------Communication URIs-----------------------------
    String SEND_TEST_EMAIL          = "/sendTestEmail";
    String SEND_TEST_TEXT           = "/sendTestText";

    //-----------------------------Auth URIs-----------------------------
    String LOGIN                    = "/auth/login";
    String LOGOUT                   = "/auth/logout";


    //-----------------------------PUBLIC URIs-----------------------------
    String [] PUBLIC_URIS           = {
                                        "/auth/**",
                                        "/emailAvailable",
                                        "/addUser/**",
                                        "/getSchools/**",
                                        "/resetPassword",
                                        "/validate",
                                        "/users",
                                        "/getJudges"
                                       };


}
