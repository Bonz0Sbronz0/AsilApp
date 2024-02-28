package it.uniba.dib.sms232413.Paziente.PazienteActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

import it.uniba.dib.sms232413.Doc.DocActivity.DocProfileActivity;
import it.uniba.dib.sms232413.R;

public class HomeUserNewActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationViewPatient;
    ImageButton imageButtonMeasurements;
    ImageButton imageButtonCara;
    ImageButton imageButtonDocuments;
    ImageButton imageButtonPlacesOfInterest;
    ImageButton imageButtonGeneralInfo;
    ImageButton imageButtonExpenses;
    FloatingActionButton fabQr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user_new);

        // Inizializza bottomNavigationView correttamente
        bottomNavigationViewPatient = findViewById(R.id.bottomNavViewPatient);

        // Assicurati che bottomNavigationView non sia nullo
        if (bottomNavigationViewPatient != null) {
            // Imposta il background del BottomNavigationView come nullo
            bottomNavigationViewPatient.setBackground(null);
        }

        bottomNavigationViewPatient.setSelectedItemId(R.id.miHome);



        imageButtonMeasurements = findViewById(R.id.imageButtonMeasurements);
        imageButtonCara = findViewById(R.id.imageButtonCara);
        imageButtonDocuments = findViewById(R.id.imageButtonDocuments);
        imageButtonPlacesOfInterest = findViewById(R.id.imageButtonPlacesOfInterest);
        imageButtonGeneralInfo = findViewById(R.id.imageButtonGeneralInfo);
        imageButtonExpenses = findViewById(R.id.imageButtonExpenses);

        fabQr = findViewById(R.id.fab_qr);

    }

    @Override
    protected void onStart() {
        super.onStart();

        bottomNavigationViewPatient.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.miHome -> {
                    return true;
                }
                case R.id.miLanguage -> {
                    showLanguageSelectionDialog();
                }
                case R.id.miProfile -> {
                    startActivity(new Intent(getApplicationContext(), DocProfileActivity.class));
                    overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_in_left_activity);
                    finish();
                }
                case R.id.miLogout -> {
                    //TODO logout
                }
            }
            return false;
        });

        imageButtonMeasurements.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), StoricoMisurazioniActivity.class);
            startActivity(intent);
        });

        imageButtonCara.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), InfoCentroActivity.class);
            startActivity(intent);
        });

        imageButtonDocuments.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DocumentActivity.class);
            startActivity(intent);
        });

        imageButtonPlacesOfInterest.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), PlacesOfInterestActivity.class);
            startActivity(intent);
        });

        imageButtonGeneralInfo.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), WikiCityInfoActivity.class);
            startActivity(intent);
        });

        imageButtonExpenses.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CostActivity.class);
            startActivity(intent);
        });

        fabQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PanicButtonActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showLanguageSelectionDialog() {
        String[] languages = {"English", "Italiano", "Français"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Select Language");
        builder.setItems(languages, (dialog, which) -> {
            String languageCode = "";
            switch (which) {
                case 0:
                    languageCode = "en";
                    break;
                case 1:
                    languageCode = "it";
                    break;
                case 2:
                    languageCode = "fr";
                    break;
            }
            setLocale(languageCode);
        });
        builder.show();
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        recreate();
    }
}