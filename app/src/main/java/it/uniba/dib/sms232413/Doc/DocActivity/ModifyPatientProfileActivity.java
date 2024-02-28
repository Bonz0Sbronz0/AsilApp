package it.uniba.dib.sms232413.Doc.DocActivity;

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
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Shared.SessionManagement;
import it.uniba.dib.sms232413.object.CentroAccoglienza;

public class ModifyPatientProfileActivity extends AppCompatActivity{

    private EditText telefono;
    private FirebaseFirestore db;
    String emailSelectedUser;
    final private String phonePattern = "^(([+]|00)39)?((3[1-6][0-9]))(\\d{7})$";

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

        db = FirebaseFirestore.getInstance(); //inizializza istanza del db
        Button btnSaveModify = findViewById(R.id.btnSaveModify);
        emailSelectedUser = getIntent().getStringExtra("selected_user_email");
        telefono = findViewById(R.id.phoneUserModify);


        btnSaveModify.setOnClickListener(view -> {

            String phone = telefono.getText().toString();

            //control input
            if(phone.isEmpty()  || !phone.matches(phonePattern)){
                telefono.setError(getString(R.string.checkPhone));
                telefono.requestFocus();
            }else {
                //initialize map
                Map<String,Object> users = new HashMap<>();
                //set data in map for db
                users.put("phone", phone);

                //save to DB
                db.collection("User").document(emailSelectedUser).update(users).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(ModifyPatientProfileActivity.this, R.string.successful, Toast.LENGTH_SHORT).show();
                    }
                    finish();
                });

                Toast.makeText(ModifyPatientProfileActivity.this, R.string.successful, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //chiamato quando la cattura del puntatore della vista cambia
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}