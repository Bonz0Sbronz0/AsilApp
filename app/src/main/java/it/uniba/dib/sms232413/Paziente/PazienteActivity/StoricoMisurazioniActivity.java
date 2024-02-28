package it.uniba.dib.sms232413.Paziente.PazienteActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import it.uniba.dib.sms232413.Paziente.PazienteAdapter.AllMeasurementsAdapter;
import it.uniba.dib.sms232413.Paziente.PazienteListener.AllMeasurementsListener;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Shared.LoginActivity;
import it.uniba.dib.sms232413.object.Misurazioni;

public class StoricoMisurazioniActivity extends AppCompatActivity implements AllMeasurementsListener {

    // dati del database
    FirebaseFirestore db;
    FirebaseAuth auth;

    // dati
    ArrayList<Misurazioni> allMeasurementArrayList;
    RecyclerView recyclerView;
    AllMeasurementsAdapter allMeasurementsAdapter;
    Spinner categorySpinner;
    ImageView selectDate;
    Calendar selectedDate;
    String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_measurements);

        selectDate = findViewById(R.id.selectDate);
        categorySpinner = findViewById(R.id.categorySpinner);

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentDate = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(StoricoMisurazioniActivity.this, (view1, year, month, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);

                    // aggiorna la lista degli elementi
                    filterListByDate(selectedDate);
                },
                        currentDate.get(Calendar.YEAR),
                        currentDate.get(Calendar.MONTH),
                        currentDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // inizializza il database
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        allMeasurementArrayList = new ArrayList<>();
        allMeasurementsAdapter = new AllMeasurementsAdapter(StoricoMisurazioniActivity.this, allMeasurementArrayList, this);
        recyclerView.setAdapter(allMeasurementsAdapter);

        // Crea un ArrayAdapter usando l'array di stringhe e un layout di spinner predefinito
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_measurements, android.R.layout.simple_spinner_item);
        // Specifica il layout da utilizzare quando appare l'elenco delle scelte
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Applica l'adapter allo spinner
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String category = adapterView.getItemAtPosition(position).toString();
                filterListByCategory(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Non fare nulla
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        allMeasurementArrayList.clear();
        eventChangeListener();
    }

    private void eventChangeListener() {
        db.collection("misurazioni")
                .whereEqualTo("userEmail", userEmail)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                allMeasurementArrayList.add(dc.getDocument().toObject(Misurazioni.class));
                            }
                            allMeasurementsAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void filterListByDate(Calendar date) {
        ArrayList<Misurazioni> filteredListDate = new ArrayList<>();
        for (Misurazioni misurazioni : allMeasurementArrayList) {
            if (misurazioni.getData() != null && misurazioni.getData().equals(getFormattedDate(date))) {
                filteredListDate.add(misurazioni);
            }else {
                Log.d("Formato data errato: ", misurazioni.getData() + " invece di: " + getFormattedDate(date));
            }
        }
        if (filteredListDate.isEmpty()) {
            allMeasurementsAdapter.setFilteredList(filteredListDate);
            Toast.makeText(this, getString(R.string.noDataFound), Toast.LENGTH_SHORT).show();
        } else {
            allMeasurementsAdapter.setFilteredList(filteredListDate);
        }
    }

    private void filterListByCategory(String category) {
        if (category.equals(getString(R.string.all_categories))) {
            // Mostra tutti i costi
            allMeasurementsAdapter.setFilteredList(allMeasurementArrayList);
        } else {
            // Filtra i costi per la categoria selezionata
            ArrayList<Misurazioni> filteredListCategory = new ArrayList<>();
            for (Misurazioni misurazioni : allMeasurementArrayList) {
                if (misurazioni.getCategoria().equalsIgnoreCase(category)) {
                    filteredListCategory.add(misurazioni);
                }
            }
            if (filteredListCategory.isEmpty()) {
                allMeasurementsAdapter.setFilteredList(filteredListCategory);
                Toast.makeText(this, getString(R.string.noDataFound), Toast.LENGTH_SHORT).show();
            } else {
                allMeasurementsAdapter.setFilteredList(filteredListCategory);
            }
        }
    }

    private String getFormattedDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault());
        return dateFormat.format(date.getTime());
    }

    @Override
    public void onItemClicked(Misurazioni misurazioni) {

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