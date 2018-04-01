package edu.pennstate.science_olympiad.helpers.mongo;

/**
 * MongoIdVerifier
 *  For verifying MongoDB related functions.
 */
public class MongoIdVerifier {

    /**
     * isValidMongoId - Checks if mongoId string is valid
     * @param mongoId the mongoId to be verified
     * @return whether or not it has the valid structure of a mongoId
     */
    public static boolean isValidMongoId(String mongoId) {
        if(mongoId == null) {
            return false;
        } else if (mongoId.length() != 24) {
            return false;
        } else {
            return true;
        }
    }
}
