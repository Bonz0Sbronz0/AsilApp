package it.uniba.dib.sms232413.Doc.DocActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232413.Database.DBCentroAccoglienza;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.CentroAccoglienza;
import it.uniba.dib.sms232413.object.PersonaleAutorizzato;


public class ModifyProfileDoc extends AppCompatActivity {

    private EditText  telefono;
    private Spinner genere, nomeStudio;
    private PersonaleAutorizzato user;
    private FirebaseFirestore db;

    final private String phonePattern = "^(([+]|00)39)?((3[1-6][0-9]))(\\d{7})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile_doc);
        // Impostazione della visualizzazione a schermo intero
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Nascondi la ActionBar se presente
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        db = FirebaseFirestore.getInstance(); //inizializza istanza del db
        Button btnSaveModify = findViewById(R.id.btnSaveModify);

        nomeStudio = findViewById(R.id.nameCenterDocModify);
        telefono = findViewById(R.id.phoneDocModify);
        user = getIntent().getParcelableExtra("user_data");

        assert user != null;
        telefono.setText(user.getPhone());
        loadCentriAccoglienza();
        btnSaveModify.setOnClickListener(view -> {

            String nameCenter = ((CentroAccoglienza) nomeStudio.getSelectedItem()).getNameId();;
            String phone = telefono.getText().toString();

            if(phone.isEmpty()  || !phone.matches(phonePattern)) {
                telefono.setError(getString(R.string.checkPhone));
                telefono.requestFocus();
            }else{
                //initialize map
                Map<String,Object> users = new HashMap<>();
                //set data in map for db

                users.put("name_center", nameCenter);
                users.put("phone", phone);

                //save to DB
                db.collection("User").document(user.getId()).update(users).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(ModifyProfileDoc.this, R.string.successful, Toast.LENGTH_SHORT).show();
                    }
                    sendUserToNextActivity(); //si passa alla prossima activity
                });
                Toast.makeText(ModifyProfileDoc.this, R.string.successful, Toast.LENGTH_SHORT).show();

            }
        });


    }



    private void sendUserToNextActivity() {
        Intent intent = new Intent(ModifyProfileDoc.this, HomeDocActivity.class); //cambiare activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //chiamato quando la cattura del puntatore della vista cambia
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
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

                nomeStudio.setAdapter(centroAdapter);
            } else {
                Toast.makeText(this, "Errore nel recupero dei centri di accoglienza", Toast.LENGTH_SHORT).show();
            }
        });
    }
}