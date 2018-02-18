package edu.pennstate.science_olympiad.helpers.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonHelper {

    public static String getJsonString (String jsonString, String strToExtract) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = parser.parse(jsonString).getAsJsonObject();
        return jsonObj.get(strToExtract).getAsString();
    }

    public static String getIdFromJson (String jsonString) {
        return getJsonString(jsonString, "id");
    }

}
