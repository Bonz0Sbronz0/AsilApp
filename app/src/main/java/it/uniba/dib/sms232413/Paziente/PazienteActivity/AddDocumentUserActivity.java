package it.uniba.dib.sms232413.Paziente.PazienteActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import it.uniba.dib.sms232413.R;

public class AddDocumentUserActivity extends AppCompatActivity {

    EditText nome;
    String userId;
    Button btnSave, btnConfirm, btnEdit;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    String name;
    static final int REQUEST_SELECT_DOCUMENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);

        // Inizializza Firebase Auth e Firebase Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(); // Ottieni l'ID utente

        // Inizializza gli elementi UI
        nome = findViewById(R.id.nome);
        btnSave = findViewById(R.id.btnSave);
        btnSave = findViewById(R.id.btnSave);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnEdit = findViewById(R.id.btnEdit);
        btnConfirm.setVisibility(View.GONE); // Nascondi il pulsante okbtn all'inizio
        btnEdit.setVisibility(View.GONE); // Nascondi il pulsante rribtn all'inizio

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Documents");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nome.getText().toString();
                if (name.isEmpty()) {
                    nome.setError(getString(R.string.checkName));
                    nome.requestFocus();
                } else {
                    selectDocument();
                }
            }
        });
    }

    private void selectDocument() {
        Intent intent = new Intent();
        intent.setType("application/pdf"); // Tipo di file da selezionare, potresti specificare un tipo specifico come "application/pdf"
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleziona il file PDF..."),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            UploadFile(data.getData());
        }
    }

    private void UploadFile(Uri data){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Caricamento...");
        progressDialog.show();

        StorageReference reference = storageReference.child("documenti/documenti-" + userId +"/"+name+".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AddDocumentUserActivity.this, R.string.succAddDocument, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        // Mostra i pulsanti okbtn e rribtn dopo aver caricato il documento
                        btnConfirm.setVisibility(View.VISIBLE);
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        btnEdit.setVisibility(View.VISIBLE);
                        btnEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                name = nome.getText().toString();
                                if (name.isEmpty()) {
                                    nome.setError(getString(R.string.checkName));
                                    nome.requestFocus();
                                } else {
                                    selectDocument();
                                }
                            }
                        });


                        // Nascondi il pulsante btnSave
                        btnSave.setVisibility(View.GONE);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred())/ snapshot.getTotalByteCount();
                        progressDialog.setMessage("Caricamento:" + (int)progress + "%");
                    }
                });
    }
}
