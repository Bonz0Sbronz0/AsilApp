package it.uniba.dib.sms232413.Paziente.PazienteActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;
import java.util.Objects;

import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Shared.LoginActivity;
import it.uniba.dib.sms232413.Shared.SessionManagement;
import it.uniba.dib.sms232413.object.LanguageManager;
import it.uniba.dib.sms232413.object.Paziente;

public class HomeUserActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationViewPatient;
    ImageButton imageButtonMeasurements;
    ImageButton imageButtonCara;
    ImageButton imageButtonDocuments;
    ImageButton imageButtonPlacesOfInterest;
    ImageButton imageButtonGeneralInfo;
    ImageButton imageButtonExpenses;
    FloatingActionButton fabQr;
    TextView greeting2TextView;
    TextView nomePazienteTextView;
    ImageButton videoThumbnail;
    Paziente paziente;
    ImageButton profileImageButton;FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user_new);
        // Leggi la lingua dalle SharedPreferences e imposta la configurazione locale
        setLocaleFromPreferences();
        // Imposta la lingua corrente
        LanguageManager.applyLanguage(this);
        // Inizializza bottomNavigationView correttamente
        bottomNavigationViewPatient = findViewById(R.id.bottomNavViewPatient);

        // Assicurati che bottomNavigationView non sia nullo
        if (bottomNavigationViewPatient != null) {
            // Imposta il background del BottomNavigationView come nullo
            bottomNavigationViewPatient.setBackground(null);
        }

        videoThumbnail = findViewById(R.id.videoThumbnail);
        nomePazienteTextView = findViewById(R.id.nomePazienteTextView);
        profileImageButton = findViewById(R.id.profileImageButton);
        bottomNavigationViewPatient.setSelectedItemId(R.id.miHome);

        imageButtonMeasurements = findViewById(R.id.imageButtonMeasurements);
        imageButtonCara = findViewById(R.id.imageButtonCara);
        imageButtonDocuments = findViewById(R.id.imageButtonDocuments);
        imageButtonPlacesOfInterest = findViewById(R.id.imageButtonPlacesOfInterest);
        imageButtonGeneralInfo = findViewById(R.id.imageButtonGeneralInfo);
        imageButtonExpenses = findViewById(R.id.imageButtonExpenses);
        greeting2TextView = findViewById(R.id.greeting2TextView);

        fabQr = findViewById(R.id.fab_qr);

        fStore.collection("User").whereEqualTo("email", userEmail).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    // Ottieni i dati del documento
                    String id = document.getString("id");
                    String name = document.getString("name");
                    String surname = document.getString("surname");
                    String gender = document.getString("gender");
                    String birthdate = document.getString("birthdate");
                    String placeBirth = document.getString("placeBirth");
                    String nationality = document.getString("nationality");
                    String receptioncenter = document.getString("receptionceter");
                    String email = document.getString("email");
                    String telefono = document.getString("telefono");
                    String emailDoc = document.getString("name");
                    String idDoc = document.getString("emailDoc");
                    String password = document.getString("password");
                    paziente = new Paziente(id, name, surname, gender, birthdate, placeBirth,
                            nationality, receptioncenter, email,
                            telefono, emailDoc, idDoc, password);
                    nomePazienteTextView.setText(paziente.getName());
                }
            } else {
                // Si è verificato un errore durante l'operazione di lettura
                Exception exception = task.getException();
                if (exception != null) {
                    // Log dell'errore
                    Log.e("Firestore", "Errore durante l'operazione di lettura", exception);
                }
                // Puoi gestire l'errore in base alle tue esigenze
            }
        });
        bottomNavigationViewPatient.setSelectedItemId(R.id.miHome);

    }

    @Override
    protected void onStart() {
        super.onStart();

        hideLoadingPage();

        bottomNavigationViewPatient.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.miHome -> {

                    return true;
                }
                case R.id.miLanguage -> {
                    showLanguageSelectionDialog();
                    return true;
                }
                case R.id.miProfile -> {
                    Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                    overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
                    intent.putExtra("user_data", paziente);
                    startActivity(intent);
                    return true;
                }
                case R.id.miLogout -> {
                    Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent2);
                    finish();
                    return true;
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

        profileImageButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);
            intent.putExtra("user_data", paziente);
            startActivity(intent);
        });

        fabQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PanicButtonActivity.class);
                startActivity(intent);
            }
        });

        videoThumbnail.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
            startActivity(intent);
        });
    }

    // Metodo per visualizzare la finestra di selezione della lingua
    private void showLanguageSelectionDialog() {
        // Creazione di un AlertDialog personalizzato
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Ottieni il layout del popup della lingua personalizzato
        View customView = getLayoutInflater().inflate(R.layout.custom_popup_language, null);

        // Imposta la vista personalizzata come vista del dialogo
        builder.setView(customView);

        // Crea e mostra il dialogo
        AlertDialog dialog = builder.create();
        dialog.show();

        // Ottieni i pulsanti della tua vista personalizzata e gestisci il loro clic
        Button btnIta = customView.findViewById(R.id.btnIta);
        Button btnEn = customView.findViewById(R.id.btnEn);
        Button btnFr = customView.findViewById(R.id.btnFr);

        btnIta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("it");
                dialog.dismiss(); // Chiudi il dialogo dopo la selezione
            }
        });

        btnEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
                dialog.dismiss(); // Chiudi il dialogo dopo la selezione
            }
        });

        btnFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("fr");
                dialog.dismiss(); // Chiudi il dialogo dopo la selezione
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Salva la lingua selezionata in SharedPreferences
        saveLocaleToPreferences();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Salva la lingua selezionata in SharedPreferences
        saveLocaleToPreferences();
    }

    // Metodo per leggere la lingua dalle SharedPreferences e impostare la configurazione locale
    private void setLocaleFromPreferences() {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        String language = preferences.getString("language", Locale.getDefault().getLanguage());
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }

    // Metodo per impostare impostazioni locali e aggiornare la configurazione
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        // Salva la lingua selezionata in SharedPreferenze
        saveLocaleToPreferences();

        // Riavvia l'attività per applicare la nuova lingua
        recreate(); // Ricarica l'attività corrente per applicare la nuova lingua
    }

    // Metodo per salvare la lingua selezionata nelle SharedPreferences
    private void saveLocaleToPreferences() {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", Locale.getDefault().getLanguage());
        editor.apply();
    }

    private void showLoadingPage() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Caricamento in corso...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    // Metodo per nascondere la pagina di caricamento
    private void hideLoadingPage() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}