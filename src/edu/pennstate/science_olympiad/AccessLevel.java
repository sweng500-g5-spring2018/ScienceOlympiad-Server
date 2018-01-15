package edu.pennstate.science_olympiad;

public enum AccessLevel {
    admin, user, spectator;

    @Override
    public String toString() {
        switch (this) {
            case admin:
                return "Administrator";
            case user:
                return "User";
            case spectator:
                return "Spectator";
        }
        return "AccessLevel";
    }
}
