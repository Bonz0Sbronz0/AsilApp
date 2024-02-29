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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import it.uniba.dib.sms232413.Paziente.PazienteAdapter.AllCostsAdapter;
import it.uniba.dib.sms232413.Paziente.PazienteListener.AllCostsSelectListener;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Shared.LoginActivity;
import it.uniba.dib.sms232413.object.Costs;

public class CostActivity extends AppCompatActivity implements AllCostsSelectListener {

    // dati del database
    FirebaseFirestore db;
    FirebaseAuth auth;

    // dati
    ArrayList<Costs> allCostsArrayList;
    RecyclerView recyclerView;
    AllCostsAdapter allCostsAdapter;
    Spinner categorySpinner;
    ImageView selectDate;
    ImageView addCost;
    Calendar selectedDate;
    String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_costs);

        selectDate = findViewById(R.id.selectDate);
        categorySpinner = findViewById(R.id.categorySpinner);
        addCost = findViewById(R.id.addCost);
        addCost.setOnClickListener(v -> {
           getSupportFragmentManager()
                   .beginTransaction()
                   .replace(R.id.drawer_layout, new AddCostsFragment())
                   .addToBackStack(null)
                   .commit();
        });
        selectDate.setOnClickListener(view -> {
            Calendar currentDate = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(CostActivity.this, (view1, year, month, dayOfMonth) -> {
                selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                // aggiorna la lista degli elementi
                filterListByDate(selectedDate);
            },
                    currentDate.get(Calendar.YEAR),
                    currentDate.get(Calendar.MONTH),
                    currentDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // inizializza il database
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        allCostsArrayList = new ArrayList<>();
        allCostsAdapter = new AllCostsAdapter(CostActivity.this, allCostsArrayList, this);
        recyclerView.setAdapter(allCostsAdapter);

        // Crea un ArrayAdapter usando l'array di stringhe e un layout di spinner predefinito
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.category, android.R.layout.simple_spinner_item);
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
        allCostsArrayList.clear();
        eventChangeListener();
    }

    private void eventChangeListener() {
        db.collection("costs")
                .whereEqualTo("userEmail", userEmail)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    assert value != null;
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            allCostsArrayList.add(dc.getDocument().toObject(Costs.class));
                        }
                        allCostsAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void filterListByDate(Calendar date) {
        ArrayList<Costs> filteredListDate = new ArrayList<>();
        for (Costs costs : allCostsArrayList) {
            if (costs.getDate() != null && costs.getDate().equals(getFormattedDate(date))) {
                filteredListDate.add(costs);
            }else {
                Log.d("Formato data errato: ", costs.getDate() + " invece di: " + getFormattedDate(date));
            }
        }
        if (filteredListDate.isEmpty()) {
            allCostsAdapter.setFilteredList(filteredListDate);
            Toast.makeText(this, getString(R.string.noDataFound), Toast.LENGTH_SHORT).show();
        } else {
            allCostsAdapter.setFilteredList(filteredListDate);
        }
    }

    private void filterListByCategory(String category) {
        if (category.equals(getString(R.string.all_categories))) {
            // Mostra tutti i costi
            allCostsAdapter.setFilteredList(allCostsArrayList);
        } else {
            // Filtra i costi per la categoria selezionata
            ArrayList<Costs> filteredListCategory = new ArrayList<>();
            for (Costs costs : allCostsArrayList) {
                if (costs.getCategory().equalsIgnoreCase(category)) {
                    filteredListCategory.add(costs);
                }
            }
            if (filteredListCategory.isEmpty()) {
                allCostsAdapter.setFilteredList(filteredListCategory);
                Toast.makeText(this, getString(R.string.noDataFound), Toast.LENGTH_SHORT).show();
            } else {
                allCostsAdapter.setFilteredList(filteredListCategory);
            }
        }
    }

    private String getFormattedDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault());
        return dateFormat.format(date.getTime());
    }

    @Override
    public void onItemClicked(Costs costs) {

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