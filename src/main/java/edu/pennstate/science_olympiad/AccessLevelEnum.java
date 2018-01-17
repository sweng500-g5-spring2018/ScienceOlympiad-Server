package edu.pennstate.science_olympiad;

/**
 * This distinguishes {@link edu.pennstate.science_olympiad.User}s so that permissions can be assigned
 */
public enum AccessLevelEnum {
    ADMIN_ENUM, USER_ENUM, SPECTATOR_ENUM;

    @Override
    public String toString() {
        switch (this) {
            case ADMIN_ENUM:
                return "Administrator";
            case USER_ENUM:
                return "User";
            case SPECTATOR_ENUM:
                return "Spectator";
        }
        return "AccessLevelEnum";
    }
}
