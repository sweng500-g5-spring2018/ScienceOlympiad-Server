package edu.pennstate.science_olympiad;

public class ImproperCreationException extends IllegalArgumentException {
    public ImproperCreationException() {
    }

    @Override
    public String toString() {
        return "Trying to create object from invalid user type";
    }
}
