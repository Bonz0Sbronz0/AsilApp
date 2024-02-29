package it.uniba.dib.sms232413.Paziente.PazienteActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Shared.SessionManagement;
import it.uniba.dib.sms232413.object.CentroAccoglienza;
import it.uniba.dib.sms232413.object.Paziente;

public class ModifyProfileActivity extends AppCompatActivity{

    private EditText telefono;
    private FirebaseFirestore db;

    private Paziente profilo;

    final private String phonePattern = "^(([+]|00)39)?((3[1-6][0-9]))(\\d{7})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile_user);
        // Impostazione della visualizzazione a schermo intero
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Nascondi la ActionBar se presente
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        profilo = getIntent().getParcelableExtra("user_data");
        db = FirebaseFirestore.getInstance(); //inizializza istanza del db
        Button btnSaveModify = findViewById(R.id.btnSaveModify);

        telefono = findViewById(R.id.phoneUserModify);
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        btnSaveModify.setOnClickListener(view -> {

            String phone = telefono.getText().toString();

            //control input
            if(phone.isEmpty()  || !phone.matches(phonePattern)){
                telefono.setError(getString(R.string.checkPhone));
                telefono.requestFocus();
            }else{
                //initialize map
                Map<String,Object> users = new HashMap<>();
                //set data in map for db
                users.put("phone", phone);

                //save to DB
                // Dopo aver salvato i dati del profilo nel database Firebase
                db.collection("User").document(userId).update(users).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(ModifyProfileActivity.this, R.string.successful, Toast.LENGTH_SHORT).show();
                        // Aggiorna i dati del profilo nella SessionManagement
                        finish();
                    }
                });

                Toast.makeText(ModifyProfileActivity.this, R.string.successful, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //chiamato quando la cattura del puntatore della vista cambia
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
