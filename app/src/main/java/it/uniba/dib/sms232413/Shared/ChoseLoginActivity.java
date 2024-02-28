package it.uniba.dib.sms232413.Shared;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.LoginSession;

public class ChoseLoginActivity extends AppCompatActivity {

    private ImageButton cardUser, cardDoc;
    FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_new);
        // Impostazione della visualizzazione a schermo intero
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Nascondi la ActionBar se presente
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        cardUser = findViewById(R.id.imageButtonUserLogin);
        cardDoc = findViewById(R.id.imageButtonDoctorLogin);

        cardDoc.setOnClickListener(view -> {
            showLoadingPage(); // Mostra la ProgressBar quando l'utente preme il pulsante per il login
            LoginSession.login(this, "prof@gmail.com", "admin02");
        });

        cardUser.setOnClickListener(view -> {
            showLoadingPage(); // Mostra la ProgressBar quando l'utente preme il pulsante per il login
            LoginSession.login(this, "abdul@gmail.com", "N3wUt3nt3");
        });

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
