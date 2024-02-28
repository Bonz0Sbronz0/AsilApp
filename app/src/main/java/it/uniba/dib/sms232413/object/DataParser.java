package it.uniba.dib.sms232413.object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String, String> getSingleNearbyPlace(JSONObject googlePlaceJSON) {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String nameOfPlace = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        try {
            if (googlePlaceJSON.has("name")) {
                nameOfPlace = googlePlaceJSON.getString("name");
            }
            if (googlePlaceJSON.has("vicinity")) {
                vicinity = googlePlaceJSON.getString("vicinity");
            }

            if (googlePlaceJSON.has("geometry") && googlePlaceJSON.getJSONObject("geometry").has("location")) {
                JSONObject location = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location");
                if (location.has("lat")) {
                    latitude = String.valueOf(location.getDouble("lat"));
                }
                if (location.has("lng")) {
                    longitude = String.valueOf(location.getDouble("lng"));
                }
            }

            reference = googlePlaceJSON.getString("reference");

            googlePlaceMap.put("place_name", nameOfPlace);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }

    private List<HashMap<String, String>> getAllNearbyPlaces(JSONArray jsonArray) {
        int counter = jsonArray.length();

        List<HashMap<String, String>> nearbyPlacesList = new ArrayList<>();

        for (int i = 0; i < counter; i++) {
            try {
                JSONObject placeObject = jsonArray.getJSONObject(i);
                HashMap<String, String> nearbyPlaceMap = getSingleNearbyPlace(placeObject);
                nearbyPlacesList.add(nearbyPlaceMap);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return nearbyPlacesList;
    }

    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray;

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return getAllNearbyPlaces(jsonArray);
    }
}
