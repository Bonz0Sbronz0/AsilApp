package it.uniba.dib.sms232413.Paziente.PazienteActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Paziente.PazienteFragment.PanicButtonActivity.UsefulNumbersFragment;

public class PanicButtonActivity extends AppCompatActivity {

    public static final int REQUEST_CALL_PHONE_PERMISSION = 7;

    private ImageButton sosButton;
    private Button btnGoToUsefulNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_button);

        sosButton = findViewById(R.id.sosButton);
        btnGoToUsefulNumbers = findViewById(R.id.btnGoToUsefulNumbers);
    }

    @Override
    protected void onStart() {
        super.onStart();

        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPanicButtonClick(v);
            }
        });

        btnGoToUsefulNumbers.setOnClickListener(this::openUsefulNumbersFragment);
    }

    private void openUsefulNumbersFragment(View v) {
        UsefulNumbersFragment usefulNumbersFragment = new UsefulNumbersFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right);
        fragmentTransaction.replace(android.R.id.content, usefulNumbersFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    // Metodo chiamato quando il Panic Button viene premuto
    public void onPanicButtonClick(View view) {
        //TODO extract number of assigned doc from db
        String phoneNumber = "tel:" + "112";

        // Verifica se hai il permesso CALL_PHONE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            // Hai il permesso, avvia l'intent per effettuare la chiamata
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(phoneNumber));
            startActivity(callIntent);
        } else {
            // Non hai il permesso, richiedilo all'utente
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PHONE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permesso concesso, chiamare nuovamente il metodo onPanicButtonClick
                onPanicButtonClick(findViewById(R.id.sosButton));
            } else {
                // Permesso negato, mostra un messaggio o gestisci di conseguenza
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
