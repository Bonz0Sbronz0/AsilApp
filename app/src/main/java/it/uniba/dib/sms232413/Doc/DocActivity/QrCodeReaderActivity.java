package it.uniba.dib.sms232413.Doc.DocActivity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Locale;

import it.uniba.dib.sms232413.Doc.DocAdapter.UserCardAdapter;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Shared.SessionManagement;
import it.uniba.dib.sms232413.object.LanguageManager;
import it.uniba.dib.sms232413.object.PersonaleAutorizzato;
import it.uniba.dib.sms232413.object.QrCodeReader;
import it.uniba.dib.sms232413.object.UserCard;

public class QrCodeReaderActivity extends AppCompatActivity {

    private UserCard userCard;
    private FloatingActionButton scanQrCode;
    BottomNavigationView bottomNavigationViewDoc;
    ImageView qr_imageView;
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted->{
                        if (Boolean.TRUE.equals(isGranted)){
                            showCamera();
                        }else{
                            Toast.makeText(this, "Il permesso per la fotocamera è necessario.", Toast.LENGTH_SHORT).show();
                        }
                    });

    private final ActivityResultLauncher<ScanOptions> scanResult =
            registerForActivityResult(new ScanContract(), result->{
                if (result.getContents() == null){
                    Toast.makeText(this, "Result not found", Toast.LENGTH_SHORT).show();
                }else{
                    setResultFromScan(result.getContents());
                }

            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_reader);
        // Leggi la lingua dalle SharedPreferences e imposta la configurazione locale
        setLocaleFromPreferences();
        // Imposta la lingua corrente
        LanguageManager.applyLanguage(this);
        bottomNavigationViewDoc = findViewById(R.id.bottomNavViewDoc);
        qr_imageView = findViewById(R.id.qr_imageView);
        View popupView = LayoutInflater.from(this).inflate(R.layout.user_card, null);
        userCard = new UserCard(popupView, this);
        scanQrCode = findViewById(R.id.fab);
        initViews();
        bottomNavigationViewDoc.setSelectedItemId(R.id.miHome);
        if (bottomNavigationViewDoc != null) {
            // Imposta il background del BottomNavigationView come nullo
            bottomNavigationViewDoc.setBackground(null);
        }

        bottomNavigationViewDoc.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.miHome:
                    finish();
                    return true;
                case R.id.miLanguage :
                    showLanguageSelectionDialog();
                    return true;
                case R.id.miProfile:
                    PersonaleAutorizzato profilo = new SessionManagement(this).getUserAuthSession();
                    Intent intent = new Intent(getApplicationContext(), DocProfileActivity.class);
                    intent.putExtra("user_data", profilo);
                    startActivity(intent);
                    return true;
                case R.id.miLogout:
                    // TODO: Handle logout
                    return true;
            }
            return false;
        });
    }


    private void initViews() {
        qr_imageView.setOnClickListener(view->
                checkPermissionAndShowActivity(this)
        );
        scanQrCode.setOnClickListener(view->
                checkPermissionAndShowActivity(this)
        );
    }

    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED){
            showCamera();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show();
        }else{
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }

    }

    private void showCamera() {
        QrCodeReader qrCodeReader = new QrCodeReader();
        qrCodeReader.setQrCodeReader();
        scanResult.launch(qrCodeReader);
    }
    private void setResultFromScan(String id) {
        UserCardAdapter.setCurrentPatienceCard(userCard, id, this);
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
}