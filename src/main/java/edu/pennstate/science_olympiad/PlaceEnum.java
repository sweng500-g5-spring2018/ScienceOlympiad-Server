package edu.pennstate.science_olympiad;

public enum PlaceEnum {
    FIRST_ENUM, SECOND_ENUM, THIRD_ENUM, NONE_ENUM;

    @Override
    public String toString() {
        switch (this) {
            case FIRST_ENUM:
                return "1st place";
            case SECOND_ENUM:
                return "2nd place";
            case THIRD_ENUM:
                return "3rd place";
            case NONE_ENUM:
                return "Did not place";
        }
        return "PlaceEnum{}";
    }
}
