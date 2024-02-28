package it.uniba.dib.sms232413.Doc.DocActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import it.uniba.dib.sms232413.Database.DBCentroAccoglienza;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

import it.uniba.dib.sms232413.Paziente.PazienteActivity.HomeUserActivity;
import it.uniba.dib.sms232413.Paziente.PazienteActivity.PanicButtonActivity;
import it.uniba.dib.sms232413.Paziente.PazienteActivity.UserProfileActivity;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.CentroAccoglienza;
import it.uniba.dib.sms232413.object.Paziente;
import it.uniba.dib.sms232413.Shared.LoginActivity;
import it.uniba.dib.sms232413.object.PersonaleAutorizzato;


public class DocProfileActivity extends AppCompatActivity {

    private TextView nome, cognome, genere, telefono, indirizzo, email, nameCenterDoc;
    private Button editButton;
    private PersonaleAutorizzato profilo;
    BottomNavigationView bottomNavigationViewDoc;
    FloatingActionButton scanQrCode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_profile2);
        // Impostazione della visualizzazione a schermo intero
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Nascondi la ActionBar se presente
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        nome = findViewById(R.id.nameDoc);
        cognome = findViewById(R.id.surnameDoc);
        genere = findViewById(R.id.genderDoc);
        telefono = findViewById(R.id.phoneDoc);
        indirizzo = findViewById(R.id.addressDoc);
        email = findViewById(R.id.emailDoc);
        nameCenterDoc = findViewById(R.id.nameCenterDoc);
        editButton = findViewById(R.id.buttonEdit);


        Intent intent = getIntent();
        if (intent.hasExtra("user_data")) {
            profilo = intent.getParcelableExtra("user_data");
            if (profilo != null) { // Assicurati che profilo non sia nullo
                // Ora puoi utilizzare l'oggetto Parcelable come desideri
                nome.setText(profilo.getName());
                cognome.setText(profilo.getSurname());
                genere.setText(profilo.getGender());
                telefono.setText(profilo.getPhone());
                indirizzo.setText(profilo.getAddress());
                email.setText(profilo.getEmail());
                Log.d("name Center Doc", (profilo.getReceptionceter()));

                findReceptionCenter(profilo);
                if (profilo.getGender().equals(getString(R.string.maschio))) {
                    genere.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_male_24, 0, 0, 0);
                } else if (profilo.getGender().equals(getString(R.string.femmina))) {
                    genere.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_female_24, 0, 0, 0);
                } else {
                    genere.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_question_mark_24, 0, 0, 0);
                }
            }
        }

        editButton.setOnClickListener(view -> {
            Intent change = new Intent(getApplicationContext(), ModifyProfileDoc.class);
            change.putExtra("user_data", profilo);
            startActivity(change);
        });

        bottomNavigationViewDoc = findViewById(R.id.bottomNavViewPatient);
        if (bottomNavigationViewDoc != null) {
            // Imposta il background del BottomNavigationView come nullo
            bottomNavigationViewDoc.setBackground(null);
        }
        scanQrCode = findViewById(R.id.fab_qr);

        bottomNavigationViewDoc.setSelectedItemId(R.id.miProfile);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bottomNavigationViewDoc.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.miHome -> {
                    startActivity(new Intent(getApplicationContext(), HomeDocActivity.class));
                    return true;
                }
                case R.id.miLanguage -> {
                    showLanguageSelectionDialog();
                    return true;
                }
                case R.id.miProfile -> {
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
        scanQrCode.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), QrCodeReaderActivity.class);
            startActivity(intent);
        });
    }

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

    private void saveLocaleToPreferences() {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", Locale.getDefault().getLanguage());
        editor.apply();
    }

    private void findReceptionCenter(PersonaleAutorizzato personaleAutorizzato){
        new DBCentroAccoglienza().readAll()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        List<CentroAccoglienza> centri = task.getResult();
                        for (CentroAccoglienza centro : centri){
                            if (centro.getNameId().equals(personaleAutorizzato.getReceptionceter())){
                                Log.d("name Center Doc", (centro.getNome()));
                                nameCenterDoc.setText(centro.getNome());
                            }
                        }
                    }
                });
    }
}
