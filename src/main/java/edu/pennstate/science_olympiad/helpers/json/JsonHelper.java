package edu.pennstate.science_olympiad.helpers.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonHelper {

    public static String getJsonString (String jsonString, String strToExtract) {
        JsonParser parser = new JsonParser();

        try {
            JsonElement found = parser.parse(jsonString).getAsJsonObject().get(strToExtract);

            if(found != null) {
                return found.getAsString();
            }
        }catch(Exception e) {
            return null;
        }

        return null;
    }




    public static String getJsonObject (String jsonString, String strToExtract) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = parser.parse(jsonString).getAsJsonObject();
        return jsonObj.getAsJsonObject(strToExtract).toString();
    }

    public static String getJsonPrimitive (String jsonString, String strToExtract) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = parser.parse(jsonString).getAsJsonObject();
        return jsonObj.getAsJsonPrimitive(strToExtract).getAsString();
    }

    /**
     * Remove an element from a json object and returning the same object
     * @param jsonString
     * @param strToRemove
     * @return
     */
    public static String removeAndGetElement(String jsonString,String strToRemove,String  strToGet) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = parser.parse(jsonString).getAsJsonObject();
        jsonObj.getAsJsonObject(strToGet).remove(strToRemove);

        return jsonObj.getAsJsonObject(strToGet).toString();
    }
    public static JsonArray getJsonList (String jsonString, String strToExtract) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = parser.parse(jsonString).getAsJsonObject();

        return jsonObj.getAsJsonArray(strToExtract);
    }

    public static String getIdFromJson (String jsonString) {
        return getJsonString(jsonString, "id");
    }

}
