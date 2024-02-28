package it.uniba.dib.sms232413.Paziente.PazienteActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import it.uniba.dib.sms232413.R;

public class CityInfoActivity extends AppCompatActivity {

    CardView cardGeneralInfos;
    CardView cardPlacesOfInterest;
    CardView cardUsefulNumbers;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info);
        // Impostazione della visualizzazione a schermo intero
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Nascondi la ActionBar se presente
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestLocationPermission();
        }

        cardPlacesOfInterest = findViewById(R.id.cardPlacesOfInterest);
        cardGeneralInfos = findViewById(R.id.cardGeneralInfos);
        cardUsefulNumbers = findViewById(R.id.cardUsefulNumbers);

        // Imposta i listener per gestire i clic sui pulsanti
        cardGeneralInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WikiCityInfoActivity.class);
                startActivity(intent);
            }
        });

        cardPlacesOfInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Codice per aprire l'activity desiderata
                Intent intent = new Intent(CityInfoActivity.this, PlacesOfInterestActivity.class);
                startActivity(intent);
            }
        });

        cardUsefulNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Codice per aprire l'activity desiderata
                Intent intent = new Intent(CityInfoActivity.this, PanicButtonActivity.class);
                startActivity(intent);
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestLocationPermission() {
        // Verifica se l'app dispone di autorizzazioni Posizione
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Richiedi autorizzazioni Posizione
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_LOCATION_PERMISSION);
        }
    }
}
