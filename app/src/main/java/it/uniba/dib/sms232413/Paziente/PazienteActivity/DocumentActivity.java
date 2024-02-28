package it.uniba.dib.sms232413.Paziente.PazienteActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import it.uniba.dib.sms232413.Paziente.PazienteAdapter.DocumentAdapter;
import it.uniba.dib.sms232413.Paziente.PazienteListener.AllDocumentsSelectListener;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Shared.LoginActivity;
import it.uniba.dib.sms232413.object.Documento;

public class DocumentActivity extends AppCompatActivity implements AllDocumentsSelectListener {

    ArrayList<Documento> documentiList;
    RecyclerView recyclerView;
    SearchView searchView;
    DocumentAdapter documentAdapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    TextView testo;
    ImageView addDocument;
    StorageReference storageReference;
    String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_doc_side);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        addDocument = findViewById(R.id.add_document);
        documentiList = new ArrayList<>();
        documentAdapter = new DocumentAdapter(documentiList, DocumentActivity.this, this, userId);

        testo  = findViewById(R.id.textViewNoData);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
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
                Intent intent = new Intent(getApplicationContext(), AddDocumentUserActivity.class);
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
        StorageReference folderRef = storageReference.child("documenti/documenti-" + userId + "/");

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

    private void saveLocaleToPreferences() {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", Locale.getDefault().getLanguage());
        editor.apply();
    }
}
