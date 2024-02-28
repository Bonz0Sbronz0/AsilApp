package it.uniba.dib.sms232413.Paziente.PazienteActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.GetPlacesOfInterest;

import it.uniba.dib.sms232413.object.GoogleDirectionsService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesOfInterestActivity extends FragmentActivity implements
        OnMapReadyCallback,
        LocationListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Marker currentUserLocationMarker;

    private ImageButton nearbyHospitalsBtn;

    private ImageButton nearbySchoolsBtn;

    private ImageButton nearbyRestaurantsBtn;

    private ImageButton zoomInButton;

    private ImageButton zoomOutButton;

    private LinearLayout mapOverlayLayout;

    private GoogleDirectionsService directionsService;

    private double latitude, longitude;

    private final int proximityRadius = 10000;

    private static final float DEFAULT_ZOOM = 10.0f;
    private static final float INTEREST_PLACE_ZOOM = 15.0f;
    private static final int REQUEST_CHECK_SETTINGS = 6;
    private final int yOffset = 50;

    private static final int Request_User_Location_Code = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_of_interest);

        nearbyHospitalsBtn = findViewById(R.id.nearbyHospitalsBtn);
        nearbySchoolsBtn = findViewById(R.id.nearbySchoolsBtn);
        nearbyRestaurantsBtn = findViewById(R.id.nearbyRestaurantsBtn);

        mapOverlayLayout = findViewById(R.id.mapOverlayLayout);

        zoomInButton = findViewById(R.id.zoomInButton);
        zoomOutButton = findViewById(R.id.zoomOutButton);

        String hospital = "hospital", school = "school", restaurant = "restaurant";

        Object transferData[] = new Object[2];
        GetPlacesOfInterest getPlacesOfInterest = new GetPlacesOfInterest();

        // Inizializza il servizio di direzioni
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        directionsService = retrofit.create(GoogleDirectionsService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }
        enableLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    onLocationChanged(locationResult.getLastLocation());
                }
            }
        };

        nearbyHospitalsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                String url = getUrl(latitude, longitude, hospital);
                transferData[0] = mMap;
                transferData[1] = url;

                GetPlacesOfInterest getPlacesOfInterest = new GetPlacesOfInterest();
                getPlacesOfInterest.execute(transferData);
                Toast searchToast = Toast.makeText(PlacesOfInterestActivity.this, getString(R.string.alla_ricerca_di_ospedali_vicini), Toast.LENGTH_SHORT);
                searchToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 300);
                searchToast.show();
            }
        });

        nearbySchoolsBtn.setOnClickListener((new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mMap.clear();
                String url = getUrl(latitude, longitude, school);
                transferData[0] = mMap;
                transferData[1] = url;

                GetPlacesOfInterest getPlacesOfInterest = new GetPlacesOfInterest();
                getPlacesOfInterest.execute(transferData);
                Toast.makeText(PlacesOfInterestActivity.this, getString(R.string.scuole_vicine), Toast.LENGTH_SHORT).show();
            }
        }));

        nearbyRestaurantsBtn.setOnClickListener((new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mMap.clear();
                String url = getUrl(latitude, longitude, restaurant);
                transferData[0] = mMap;
                transferData[1] = url;

                GetPlacesOfInterest getPlacesOfInterest = new GetPlacesOfInterest();
                getPlacesOfInterest.execute(transferData);
                Toast.makeText(PlacesOfInterestActivity.this, getString(R.string.ristorante_nelle_vicinanze), Toast.LENGTH_SHORT).show();
            }
        }));

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
    }

    private void enableLocation() {
        com.google.android.gms.location.LocationRequest locationRequest = com.google.android.gms.location.LocationRequest.create()
                .setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 secondi
                .setFastestInterval(5 * 1000); // 5 secondi

        com.google.android.gms.location.LocationSettingsRequest.Builder builder = new com.google.android.gms.location.LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> {
            // La localizzazione è già attivata, esegui le azioni necessarie
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // La localizzazione non è attivata, ma può essere risolta mostrando un prompt all'utente
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Errore durante l'avvio dell'intent
                }
            }
        });

    }

    private String getUrl(double latitude,double longitude,String nearByPlace){

        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + proximityRadius);
        googleURL.append("&type=" + nearByPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyBK0S_zLy6NPPNCxETrZiLadklGu4hsGSY");

        Log.d("PlacesOfInterestActivity", "url = " + googleURL.toString());

        return googleURL.toString();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildLocationRequest();
            startLocationUpdates();
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Ottieni la posizione del marker
                LatLng destination = marker.getPosition();

                // Crea un contesto per le API di Google Maps
                GeoApiContext context = new GeoApiContext.Builder()
                        .apiKey("AIzaSyBK0S_zLy6NPPNCxETrZiLadklGu4hsGSY")
                        .build();

                // Ottieni la tua posizione corrente
                LatLng origin = new LatLng(latitude, longitude);

                // Esegui la richiesta delle indicazioni
                try {
                    DirectionsResult result = DirectionsApi.newRequest(context)
                            .mode(TravelMode.DRIVING) // Puoi cambiare il modo in base alle tue esigenze
                            .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                            .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                            .await();

                    // Esegui l'elaborazione del risultato qui, ad esempio, mostrando le indicazioni nella tua UI
                    // result.routes contiene le informazioni sul percorso

                    // Nota: Ricorda di gestire la UI sul thread principale (usare runOnUiThread se necessario)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Mostra le indicazioni nella tua UI
                            // Questo è solo un esempio, dovrai personalizzarlo ulteriormente
                            if (result.routes != null && result.routes.length > 0) {
                                String instructions = result.routes[0].legs[0].startAddress + " to " + result.routes[0].legs[0].endAddress + "\n\n";
                                for (int i = 0; i < result.routes[0].legs[0].steps.length; i++) {
                                    instructions += result.routes[0].legs[0].steps[i].htmlInstructions + "\n";
                                }
                                // Ora puoi mostrare "instructions" nella tua UI
                                // Ad esempio, utilizzando un AlertDialog, un TextView, ecc.
                                Log.d("Directions", instructions);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false; // Imposta false per consentire l'azione predefinita di aprire Google Maps
            }
        });
    }

    private void buildLocationRequest() {
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        buildLocationRequest();
                        startLocationUpdates();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.permesso_negato), Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentUserLocationMarker = mMap.addMarker(markerOptions);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop location updates when the activity is destroyed
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

}