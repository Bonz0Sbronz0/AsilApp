package it.uniba.dib.sms232413.object;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetPlacesOfInterest extends AsyncTask<Object,String,String> {

    private String googlePlaceData, url;
    private GoogleMap mMap;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData = downloadUrl.readUrl(url);
            Log.d("GetPlacesOfInterest", "Google Place Data: " + googlePlaceData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearByPlacesList = null;
        DataParser dataParser = new DataParser();
        try {
            nearByPlacesList = dataParser.parse(s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (nearByPlacesList != null) {
            Log.d("GetPlacesOfInterest", "Nearby Places List Size: " + nearByPlacesList.size());
            DisplayNearbyPlaces(nearByPlacesList);
        } else {
            Log.e("GetPlacesOfInterest", "Nearby Places List is null");
        }
    }

    private void DisplayNearbyPlaces(List<HashMap<String, String>> nearByPlacesList) {

        for (int i = 0; i < nearByPlacesList.size(); i++) {

            MarkerOptions markerOptions = new MarkerOptions();

            HashMap<String, String> googleNearbyPlaces = nearByPlacesList.get(i);
            String nameOfPlace = googleNearbyPlaces.get("place_name");
            String vicinity = googleNearbyPlaces.get("vicinity");

            String latValue = googleNearbyPlaces.get("lat");
            String lngValue = googleNearbyPlaces.get("lng");

            Log.d("GetPlacesOfInterest", "latValue: " + latValue + ", lngValue: " + lngValue);

            if (latValue != null && !latValue.trim().isEmpty() && lngValue != null && !lngValue.trim().isEmpty()) {
                try {
                    double lat = Double.parseDouble(latValue);
                    double lng = Double.parseDouble(lngValue);

                    LatLng latLng = new LatLng(lat, lng);
                    markerOptions.position(latLng);
                    markerOptions.title(nameOfPlace + " : " + vicinity);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                } catch (NumberFormatException e) {
                    Log.e("GetPlacesOfInterest", "Error parsing latitude or longitude: " + e.getMessage());
                }
            } else {
                Log.e("GetPlacesOfInterest", "Latitude or longitude value is null or empty");
            }
        }
    }
}
