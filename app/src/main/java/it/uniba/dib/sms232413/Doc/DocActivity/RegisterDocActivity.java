package it.uniba.dib.sms232413.Doc.DocActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.uniba.dib.sms232413.Database.DBCentroAccoglienza;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.CentroAccoglienza;


public class RegisterDocActivity extends AppCompatActivity {

    private Button btnRegister;
    private Spinner centroAccoglienzaSpinner;
    private EditText surnameInput;
    private EditText nameInput;
    private EditText emailInput;
    private Spinner genderInput;
    private EditText passwordInput;
    private EditText retypePasswordInput;
    private EditText phoneInput;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    final private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    final private String phonePattern = "^(([+]|00)39)?((3[1-6][0-9]))(\\d{7})$";
    final private String type = "doc"; // Tipo doc perchè la parte di registrazione è riservata solo ai dottori
    // Gli utenti saranno inseriti in seguito dai dottori

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        // Impostazione della visualizzazione a schermo intero
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Nascondi la ActionBar se presente
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnRegister = findViewById(R.id.btnRegister);

        centroAccoglienzaSpinner = findViewById(R.id.nameCara);
        surnameInput = findViewById(R.id.surnameInput);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        genderInput = findViewById(R.id.genderInput);
        passwordInput = findViewById(R.id.passwordInput);
        phoneInput = findViewById(R.id.phoneInput);
        retypePasswordInput = findViewById(R.id.retypePasswordInput);

        loadCentriAccoglienza();

        btnRegister.setOnClickListener(view -> {
            String nameStudio = ((CentroAccoglienza) centroAccoglienzaSpinner.getSelectedItem()).getNameId();
            String surname = surnameInput.getText().toString();
            String name = nameInput.getText().toString();
            String mail = emailInput.getText().toString();
            String gender = genderInput.getSelectedItem().toString();
            String address = ((CentroAccoglienza) centroAccoglienzaSpinner.getSelectedItem()).getLocalita().get("via");
            String password = passwordInput.getText().toString();
            String confirmPassword = retypePasswordInput.getText().toString();
            String phone = phoneInput.getText().toString();


            if(surname.isEmpty()){
                surnameInput.setError(getString(R.string.checkSurname));
                surnameInput.requestFocus();
            }else if(name.isEmpty()){
                nameInput.setError(getString(R.string.checkName));
                nameInput.requestFocus();
            }else if(mail.isEmpty() || !mail.matches(emailPattern)){
                emailInput.setError(getString(R.string.checkEmail));
                emailInput.requestFocus();
            }else if(phone.isEmpty() || !phone.matches(phonePattern)){
                phoneInput.setError(getString(R.string.checkPhone));
                phoneInput.requestFocus();
            }else if(password.isEmpty() || password.length() < 6){
                passwordInput.setError(getString(R.string.checkPassword));
                passwordInput.requestFocus();
            }else if(!password.equals(confirmPassword)){
                retypePasswordInput.setError(getString(R.string.checkPassword2));
                retypePasswordInput.requestFocus();
            }else{
                // Creazione di un nuovo utente con e-mail e password attraverso Firebase Authentication
                auth.createUserWithEmailAndPassword(mail, password)
                        .addOnCompleteListener(task -> {
                            // Verifica se la creazione dell'utente è stata completata con successo
                            if (task.isSuccessful()) {
                                // Se la creazione è riuscita, prepara i dati dell'utente da memorizzare nel database Firestore
                                Map<String,Object> vet = new HashMap<>();
                                vet.put("receptionceter", nameStudio);
                                vet.put("surname", surname);
                                vet.put("name", name);
                                vet.put("email", mail);
                                vet.put("gender", gender);
                                vet.put("address", address);
                                vet.put("password", password);
                                vet.put("phone", phone);
                                vet.put("type",type);
                                // Ottieni l'ID univoco dell'utente creato
                                vet.put("id", Objects.requireNonNull(auth.getCurrentUser()).getUid());

                                // Salva i dati dell'utente nel documento corrispondente nel database Firestore
                                db.collection("User").document(auth.getCurrentUser().getUid()).set(vet);
                                Toast.makeText(RegisterDocActivity.this, getString(R.string.successful), Toast.LENGTH_SHORT).show();
                                sendDocToNextActivity();
                            } else {
                                Toast.makeText(RegisterDocActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void loadCentriAccoglienza() {
        DBCentroAccoglienza dbCentroAccoglienza = new DBCentroAccoglienza();
        dbCentroAccoglienza.getCollectionInstance().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<CentroAccoglienza> centriAccoglienza = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    CentroAccoglienza centro = document.toObject(CentroAccoglienza.class);
                    centriAccoglienza.add(centro);
                }

                ArrayAdapter<CentroAccoglienza> centroAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, centriAccoglienza);
                centroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                centroAccoglienzaSpinner.setAdapter(centroAdapter);
            } else {
                Toast.makeText(this, "Errore nel recupero dei centri di accoglienza", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendDocToNextActivity() {
        Intent intent = new Intent(RegisterDocActivity.this, HomeDocActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}