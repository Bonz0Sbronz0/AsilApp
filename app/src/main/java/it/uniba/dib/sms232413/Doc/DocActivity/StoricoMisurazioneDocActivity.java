package it.uniba.dib.sms232413.Doc.DocActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import it.uniba.dib.sms232413.object.Paziente;


public class StoricoMisurazioneDocActivity extends AppCompatActivity implements AllMeasurementsListener {

    // dati del database
    FirebaseFirestore db;
    FirebaseAuth auth;

    // dati
    ArrayList<Misurazioni> allMeasurementArrayList;
    RecyclerView recyclerView;
    AllMeasurementsAdapter allMeasurementsAdapter;
    Spinner categorySpinner;
    ImageView selectDate, addmeasurements;
    Calendar selectedDate;
    String userEmail;
    Boolean passwordVisible = false;
    BottomNavigationView bottomNavigationViewDoc;
    FloatingActionButton scanQrCode;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_measurements_doc_side);

        selectDate = findViewById(R.id.selectDate);
        categorySpinner = findViewById(R.id.categorySpinner);
        addmeasurements = findViewById(R.id.addMeasurements);
        Paziente paziente = getIntent().getParcelableExtra("paziente");
        userEmail = paziente.getEmail();
        addmeasurements.setOnClickListener(view -> {

            View customView = LayoutInflater.from(StoricoMisurazioneDocActivity.this).inflate(R.layout.custom_popup_password, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(StoricoMisurazioneDocActivity.this);
            builder.setView(customView);
            AlertDialog dialog = builder.create();
            dialog.show();


            EditText passwordInput = customView.findViewById(R.id.passwordInput);
            Button proseguiButton = customView.findViewById(R.id.btn_close_popup);
            proseguiButton.setOnClickListener(v -> {
                // Ottieni la password inserita dall'utente
                String password = passwordInput.getText().toString();
                String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

                // Verifica la password con Firebase Authentication
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // L'autenticazione ha avuto successo, la password è corretta
                                Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
                                intent.putExtra("paziente", paziente);
                                startActivity(intent);
                            } else {
                                // L'autenticazione ha fallito, la password è errata o non esiste un account con questa email
                                Toast.makeText(this, getString(R.string.checkPassword), Toast.LENGTH_SHORT).show();
                            }
                        });
                // Chiudi il popup
                dialog.dismiss();
            });
            passwordInput.setOnTouchListener((view2, motionEvent) -> {
                final int right = 2;
                final int left = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= passwordInput.getRight() - passwordInput.getCompoundDrawables()[right].getBounds().width()) {
                        int selection = passwordInput.getSelectionEnd();
                        if (passwordVisible) {
                            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_security_24, 0, R.drawable.ic_baseline_visibility_off_24, 0);

                            passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_security_24, 0, R.drawable.ic_baseline_visibility_24, 0);
                            passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        passwordInput.setSelection(selection);
                        return true;
                    }
                }
                return false;
            });
        });

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentDate = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(StoricoMisurazioneDocActivity.this, (view1, year, month, dayOfMonth) -> {
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
        allMeasurementsAdapter = new AllMeasurementsAdapter(StoricoMisurazioneDocActivity.this, allMeasurementArrayList, this);
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

        bottomNavigationViewDoc = findViewById(R.id.bottomNavViewDoc);
        if (bottomNavigationViewDoc != null) {
            // Imposta il background del BottomNavigationView come nullo
            bottomNavigationViewDoc.setBackground(null);
        }
        scanQrCode = findViewById(R.id.fab_qr);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bottomNavigationViewDoc.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.miHome -> {
                    startActivity(new Intent(getApplicationContext(), HomeDocActivity.class));
                    return true;
                }
                case R.id.miLanguage -> {
                    showLanguageSelectionDialog();
                    return true;
                }
                case R.id.miProfile -> {
                    startActivity(new Intent(getApplicationContext(), DocProfileActivity.class));
                    return true;
                }
                case R.id.miLogout -> {
                    Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent2);
                    finish();
                    return true;
                }
            }
            return false;
        });
        scanQrCode.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), QrCodeReaderActivity.class);
            startActivity(intent);
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