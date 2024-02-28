package it.uniba.dib.sms232413.Paziente.PazienteActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

import it.uniba.dib.sms232413.Paziente.PazienteAdapter.UserProfileAdapter;
import it.uniba.dib.sms232413.Paziente.PazienteFragment.MyQrCodeFragment;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Shared.LoginActivity;
import it.uniba.dib.sms232413.Shared.SessionManagement;
import it.uniba.dib.sms232413.object.Paziente;

public class UserProfileActivity extends AppCompatActivity {

    ImageView myQrCodeIcon;
    UserProfileAdapter userProfileAdapter;
    Button buttonEdit;
    BottomNavigationView bottomNavigationViewDoc;
    View currentView;
    MyQrCodeFragment  myQrCodeFragment = new MyQrCodeFragment();
    BottomNavigationView bottomNavigationViewPatient;
    FloatingActionButton fabQr;
    Paziente profilo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profilo = getIntent().getParcelableExtra("user_data");
        currentView = findViewById(R.id.profile_container);
        myQrCodeIcon = findViewById(R.id.my_qr_code_icon);
        buttonEdit = findViewById(R.id.buttonEdit);

        bottomNavigationViewPatient = findViewById(R.id.bottomNavViewPatient);
        if (bottomNavigationViewPatient != null) {
            // Imposta il background del BottomNavigationView come nullo
            bottomNavigationViewPatient.setBackground(null);
        }
        fabQr = findViewById(R.id.fab_qr);

        bottomNavigationViewPatient.setSelectedItemId(R.id.miProfile);
    }

    @Override
    protected void onStart() {
        super.onStart();
        userProfileAdapter = new UserProfileAdapter(currentView);
        userProfileAdapter.setProfileView(profilo);
        myQrCodeIcon.setOnClickListener(view-> {
                    Bundle mBundle = new Bundle();
                    mBundle.putParcelable("current_user", profilo);
                    myQrCodeFragment.setArguments(mBundle);
                    getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profile_container, myQrCodeFragment)
                    .addToBackStack(null)
                    .commit();
                });
        buttonEdit.setOnClickListener(view->{
            Intent intent = new Intent(getApplicationContext(), ModifyProfileActivity.class);
            intent.putExtra("user_data", profilo);
            startActivity(intent);
            finish();
        });

        bottomNavigationViewPatient.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.miHome -> {
                    startActivity(new Intent(getApplicationContext(), HomeUserActivity.class));
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
        fabQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PanicButtonActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
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


}