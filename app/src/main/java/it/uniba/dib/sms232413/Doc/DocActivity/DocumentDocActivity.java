package it.uniba.dib.sms232413.Doc.DocActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


import it.uniba.dib.sms232413.Paziente.PazienteAdapter.DocumentAdapter;
import it.uniba.dib.sms232413.Paziente.PazienteListener.AllDocumentsSelectListener;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.Documento;
import it.uniba.dib.sms232413.object.Paziente;

public class DocumentDocActivity extends AppCompatActivity implements AllDocumentsSelectListener {

    ArrayList<Documento> documentiList;
    RecyclerView recyclerView;
    SearchView searchView;
    DocumentAdapter documentAdapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    ImageView addDocument;
    String userId;
    TextView testo;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_doc_side);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        addDocument = findViewById(R.id.add_document);
        testo  = findViewById(R.id.textViewNoData);
        Paziente paziente = getIntent().getParcelableExtra("paziente");
        userId = paziente.getId();

        documentiList = new ArrayList<>();
        documentAdapter = new DocumentAdapter(documentiList, DocumentDocActivity.this, this, userId);


        recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(documentAdapter);
        searchView = findViewById(R.id.userSearchView);
        searchView.clearFocus();
        storageReference = FirebaseStorage.getInstance().getReference();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return true;
            }
        });

        addDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddDocumentDocActivity.class);
                intent.putExtra("paziente", paziente);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        documentiList.clear();
        viewAllFiles(); // Aggiornato per visualizzare tutti i file
    }

    private void filterList(String s) {
        ArrayList<Documento> filteredList = new ArrayList<>();

        for (Documento documento : documentiList) {
            if (documento.getNome().toUpperCase(Locale.ROOT).contains(s.toUpperCase(Locale.ROOT))) {
                filteredList.add(documento);
            }
        }

        documentAdapter.setFilteredList(filteredList); // Passa la lista filtrata all'adapter

        if (filteredList.isEmpty()) {
            Toast.makeText(this, getString(R.string.nessun_documento_trovato), Toast.LENGTH_SHORT).show();
        }

        documentAdapter.notifyDataSetChanged();
    }

    private void viewAllFiles() {
        // Ottieni il riferimento alla cartella nel Firebase Storage
        StorageReference folderRef = storageReference.child("documenti/documenti-" + userId);

        // Recupera tutti i file nella cartella
        folderRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                testo.setText("");
                // Ottieni il nome del file
                String fileName = item.getName();
                item.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                    // Conversione del file in un array di byte
                    // Aggiungi il documento alla lista documentiList
                    Documento documento = new Documento(fileName, bytes);
                    documentiList.add(documento);
                    if(documentiList.isEmpty()){
                        Log.d("Lista vuota", "la lista è vuota");
                    }else{
                        Log.d("Lista piena", String.valueOf(documentiList.size()));
                        Log.d("Nome documento", fileName);
                        Log.d("Documento: ", Arrays.toString(bytes));
                    }
                    // Notifica l'adapter del RecyclerView che i dati sono cambiati
                    documentAdapter.notifyDataSetChanged();
                }).addOnFailureListener(exception -> {
                    // Gestione degli errori durante il recupero dei byte del file
                    Log.e("TAG", "Errore nel recupero dei byte del file", exception);
                });
            }
        }).addOnFailureListener(e -> {
            // Gestisci eventuali errori nel recupero dei file
            Log.e("TAG", "Errore nel recupero dei file", e);
        });
    }

    @Override
    public void onItemClicked(Documento documento) {

    }
}
