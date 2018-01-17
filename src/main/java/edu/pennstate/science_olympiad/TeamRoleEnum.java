package edu.pennstate.science_olympiad;


/**
 * This establishes the role of a {@link edu.pennstate.science_olympiad.User} on a
 * {@link edu.pennstate.science_olympiad.Team}. This is important because Captains and Co-captains will be able to
 * add or remove other team members
 */
public enum TeamRoleEnum {
    CAPTAIN_ENUM, COCAPTAIN_ENUM, TEAMMATE_ENUM, NONE_ENUM;

    @Override
    public String toString() {
        switch (this) {
            case CAPTAIN_ENUM:
                return "Team Captain";
            case COCAPTAIN_ENUM:
                return "Co-captain";
            case TEAMMATE_ENUM:
                return "Teammate";
            case NONE_ENUM:
                return "No role";
        }
        return "TeamRoleEnum";
    }
}
