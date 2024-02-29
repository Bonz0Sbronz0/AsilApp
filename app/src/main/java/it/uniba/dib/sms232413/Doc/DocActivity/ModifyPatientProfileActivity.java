package it.uniba.dib.sms232413.Doc.DocActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.uniba.dib.sms232413.Database.DBCentroAccoglienza;
import it.uniba.dib.sms232413.Database.DBUtenti;
import it.uniba.dib.sms232413.Paziente.PazienteActivity.ModifyProfileActivity;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Shared.SessionManagement;
import it.uniba.dib.sms232413.object.CentroAccoglienza;
import it.uniba.dib.sms232413.object.Paziente;
import it.uniba.dib.sms232413.object.PersonaleAutorizzato;

public class ModifyPatientProfileActivity extends AppCompatActivity{

    private EditText telefono, nome, cognome;
    private Spinner genere, centroaccoglienza;
    private FirebaseFirestore db;
    String emailSelectedUser;
    Paziente paziente;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    final private String phonePattern = "^(([+]|00)39)?((3[1-6][0-9]))(\\d{7})$";
    PersonaleAutorizzato personaleAutorizzato;
    String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_patient_profile);

        // Impostazione della visualizzazione a schermo intero
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Nascondi la ActionBar se presente
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        personaleAutorizzato =getIntent().getParcelableExtra("user_data");
        db = FirebaseFirestore.getInstance(); //inizializza istanza del db
        Button btnSaveModify = findViewById(R.id.btnSaveModify);
        emailSelectedUser = getIntent().getStringExtra("selected_user_email");
        fStore.collection("User").whereEqualTo("email", emailSelectedUser).get().addOnCompleteListener(task -> {
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
        Log.d("emailpaziente::", emailSelectedUser);
        telefono = findViewById(R.id.phoneUserModify);
        nome = findViewById(R.id.nameUserModify);
        cognome = findViewById(R.id.surnameUserModify);
        genere = findViewById(R.id.genderUserModify);
        centroaccoglienza = findViewById(R.id.receptioncenterUserModify);
        loadCentriAccoglienza();


        btnSaveModify.setOnClickListener(view -> {

            String name = nome.getText().toString();
            String surname = cognome.getText().toString();
            String gender = genere.getSelectedItem().toString();
            String receptionceter = ((CentroAccoglienza) centroaccoglienza.getSelectedItem()).getNameId();
            String phone = telefono.getText().toString();

            //control input
            if(name.isEmpty()){
                nome.setError(getString(R.string.checkName));
                nome.requestFocus();
            }else if(phone.isEmpty()  || !phone.matches(phonePattern)){
                telefono.setError(getString(R.string.checkPhone));
                telefono.requestFocus();
            }else if(surname.isEmpty()){
                cognome.setError(getString(R.string.checkSurname));
                cognome.requestFocus();
            }else {
                //initialize map
                Map<String,Object> users = new HashMap<>();
                //set data in map for db
                users.put("name", name);
                users.put("surname", surname);
                users.put("gender", gender);
                users.put("receptionceter", receptionceter);
                users.put("phone", phone);

                //save to DB
                db.collection("User").document(paziente.getId()).update(users).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ModifyPatientProfileActivity.this, R.string.successful, Toast.LENGTH_SHORT).show();
                        // Navigate back to ListUserActivity after successful update
                        Intent intent = new Intent(ModifyPatientProfileActivity.this, ListUserActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("user_data", personaleAutorizzato);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ModifyPatientProfileActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //chiamato quando la cattura del puntatore della vista cambia
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void loadCentriAccoglienza() {
        DBCentroAccoglienza dbCentroAccoglienza = new DBCentroAccoglienza();
        dbCentroAccoglienza.getCollectionInstance().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<CentroAccoglienza> centriAccoglienza = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CentroAccoglienza centro = document.toObject(CentroAccoglienza.class);
                        centriAccoglienza.add(centro);
                    }

                    // Crea un ArrayAdapter per i nomi dei centri di accoglienza
                    ArrayAdapter<CentroAccoglienza> centroAdapter = new ArrayAdapter<>(ModifyPatientProfileActivity.this,
                            android.R.layout.simple_spinner_item, centriAccoglienza);
                    centroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Assegna l'adapter allo Spinner
                    ModifyPatientProfileActivity.this.centroaccoglienza.setAdapter(centroAdapter);
                } else {
                    // Gestisci l'errore
                    Toast.makeText(ModifyPatientProfileActivity.this, "Errore nel recupero dei centri di accoglienza", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}