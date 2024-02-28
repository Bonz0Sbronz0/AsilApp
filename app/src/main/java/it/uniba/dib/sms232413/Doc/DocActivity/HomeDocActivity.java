package it.uniba.dib.sms232413.Doc.DocActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
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

import it.uniba.dib.sms232413.Database.DBUtenti;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Shared.LoginActivity;
import it.uniba.dib.sms232413.Shared.SessionManagement;
import it.uniba.dib.sms232413.object.LanguageManager;
import it.uniba.dib.sms232413.object.PersonaleAutorizzato;

public class HomeDocActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationViewDoc;
    ImageButton imageButtonUserList, imageButtonAddUser;
    TextView add_user_textview, list_user_textview;
    PersonaleAutorizzato profilo;
    private FloatingActionButton scanQrCode;
    TextView nomeDocTextView;
    ImageButton profileImageButton;
    String userEmail;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_doc_new);
        nomeDocTextView = findViewById(R.id.nomeDocTextView);
        bottomNavigationViewDoc = findViewById(R.id.bottomNavViewDoc);
        if (bottomNavigationViewDoc != null) {
            // Imposta il background del BottomNavigationView come nullo
            bottomNavigationViewDoc.setBackground(null);
        }
        imageButtonUserList = findViewById(R.id.imageButtonUserList);
        imageButtonAddUser = findViewById(R.id.imageButtonAddUser);
        add_user_textview = findViewById(R.id.add_user_textview);
        list_user_textview = findViewById(R.id.list_user_textview);
        profileImageButton = findViewById(R.id.profileImageButton);
        scanQrCode = findViewById(R.id.fab_qr);

        // Leggi la lingua dalle SharedPreferences e imposta la configurazione locale
        setLocaleFromPreferences();
        // Imposta la lingua corrente
        LanguageManager.applyLanguage(this);

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        nomeDocTextView.setText(new SessionManagement(this).getUserAuthSession().getName());
        new DBUtenti().findDocByEmail(userEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                nomeDocTextView.setText(task.getResult().getName());
                profilo = task.getResult();
            }
        });


        bottomNavigationViewDoc.setSelectedItemId(R.id.miHome);

        bottomNavigationViewDoc.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.miHome:
                    return true;
                case R.id.miLanguage :
                    showLanguageSelectionDialog();
                    return true;
                case R.id.miProfile:
                    Intent intent = new Intent(getApplicationContext(), DocProfileActivity.class);
                    intent.putExtra("user_data", profilo);
                    startActivity(intent);
                    return true;
                case R.id.miLogout:
                    Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent2);
                    finish();
                    return true;
            }
            return false;
        });

        imageButtonUserList.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ListUserActivity.class);
            intent.putExtra("user_data", profilo);
            startActivity(intent);
        });

        imageButtonAddUser.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_doc_container, new AddUserFragment())
                    .addToBackStack(null)
                    .commit();
        });

        profileImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), DocProfileActivity.class);
            intent.putExtra("user_data", profilo);
            startActivity(intent);
        });

        scanQrCode.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), QrCodeReaderActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideLoadingPage();
    }

    // Metodo per visualizzare la finestra di selezione della lingua

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
    protected void onResume() {
        super.onResume();
        new DBUtenti().findDocByEmail(userEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                nomeDocTextView.setText(task.getResult().getName());
                profilo = task.getResult();
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
