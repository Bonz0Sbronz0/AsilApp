package it.uniba.dib.sms232413.Doc.DocActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.uniba.dib.sms232413.Database.DBCentroAccoglienza;
import it.uniba.dib.sms232413.Database.DBUtenti;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.CentroAccoglienza;
import it.uniba.dib.sms232413.object.PersonaleAutorizzato;

public class AddUserFragment extends Fragment {

    private Button btnRegistra;
    private EditText nome, cognome, email, telefono;
    private String password = "N3wUt3nt3";
    private TextInputEditText dataNascita;
    private AutoCompleteTextView luogoNascita;
    private Spinner genere, centroAccoglienza;
    final private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    final private String phonePattern = "^(([+]|00)39)?((3[1-6][0-9]))(\\d{7})$";
    final private String type = "user";
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    String idDoc;
    String emailDoc;
    PersonaleAutorizzato personaleAutorizzato;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle!=null){
            personaleAutorizzato = bundle.getParcelable("user_data");
            assert personaleAutorizzato != null;
            idDoc = personaleAutorizzato.getId();
            emailDoc = personaleAutorizzato.getEmail();
        }
        return inflater.inflate(R.layout.fragment_add_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnRegistra = view.findViewById(R.id.btnRegister);
        nome = view.findViewById(R.id.nome);
        cognome = view.findViewById(R.id.cognome);
        genere = view.findViewById(R.id.genere);
        dataNascita = view.findViewById(R.id.datanascita);
        luogoNascita = view.findViewById(R.id.luogonascita);
        centroAccoglienza = view.findViewById(R.id.centroOspitante);
        email = view.findViewById(R.id.email);
        telefono = view.findViewById(R.id.telefono);
        Log.d("iddottore:", idDoc);

        String[] nationalities = getResources().getStringArray(R.array.nationalities);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_dropdown_item_1line, nationalities);
        luogoNascita.setAdapter(adapter);

        loadCentriAccoglienza();

        dataNascita.setOnClickListener(v -> showDatePickerDialog(v.getContext()));

        btnRegistra.setOnClickListener(v -> {
            String nomeDB = nome.getText().toString();
            String cognomeDB = cognome.getText().toString();
            String genereDB = genere.getSelectedItem().toString();
            String dataNascitaDB = Objects.requireNonNull(dataNascita.getText()).toString();
            String luogoNascitaDB = luogoNascita.getText().toString();
            String centroAccoglienzaDB = ((CentroAccoglienza) centroAccoglienza.getSelectedItem()).getNameId();
            String emailDB = email.getText().toString();
            String telefonoDB = telefono.getText().toString();

            if(nomeDB.isEmpty()){
                nome.setError(getString(R.string.checkName));
                nome.requestFocus();
            } else if(cognomeDB.isEmpty()){
                cognome.setError(getString(R.string.checkSurname));
                cognome.requestFocus();
            } else if(emailDB.isEmpty() || !emailDB.matches(emailPattern)){
                email.setError(getString(R.string.checkEmail));
                email.requestFocus();
            } else if(telefonoDB.isEmpty() || !telefonoDB.matches(phonePattern)){
                telefono.setError(getString(R.string.checkPhone));
                telefono.requestFocus();
            } else if(dataNascitaDB.isEmpty()){
                dataNascita.setError(getString(R.string.checkBirthdate));
                dataNascita.requestFocus();
            } else if(luogoNascitaDB.isEmpty()){
                luogoNascita.setError(getString(R.string.checkPlaceBirth));
                luogoNascita.requestFocus();
            } else {
                // Creazione di un nuovo utente con e-mail e password attraverso Firebase Authentication
                auth.createUserWithEmailAndPassword(emailDB, password)
                        .addOnCompleteListener(task -> {
                            // Verifica se la creazione dell'utente è stata completata con successo
                            if (task.isSuccessful()) {
                                Map<String,String> vet = new HashMap<>();
                                vet.put("name", nomeDB);
                                vet.put("surname", cognomeDB);
                                vet.put("gender", genereDB);
                                vet.put("birthdate", dataNascitaDB);
                                vet.put("placeBirth", luogoNascitaDB);
                                vet.put("receptionceter", centroAccoglienzaDB);
                                vet.put("email", emailDB);
                                vet.put("phone", telefonoDB);
                                vet.put("password", password);
                                vet.put("type",type);
                                vet.put("idDoc", idDoc);
                                vet.put("emailDoc",emailDoc);
                                // Ottieni l'ID univoco dell'utente creato
                                String id = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                                vet.put("id", id);
                                vet.put("docFoldRef", new DBUtenti().createDefaultPatientDocFolder(id));
                                // Salva i dati dell'utente nel documento corrispondente nel database Firestore
                                db.collection("User").document(auth.getCurrentUser().getUid()).set(vet)
                                        .addOnCompleteListener(documentTask -> {
                                            if (documentTask.isSuccessful()) {
                                                auth.sendPasswordResetEmail(emailDB).addOnCompleteListener(emailTask -> {
                                                    if(emailTask.isSuccessful()) {
                                                        Toast.makeText(v.getContext(), "Utente registrato", Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(v.getContext(), ListUserActivity.class);
                                                        intent.putExtra("user_data", personaleAutorizzato);
                                                        startActivity(intent);
                                                        requireActivity().finish();
                                                    } else {
                                                        Toast.makeText(v.getContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(v.getContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(v.getContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }


    private void showDatePickerDialog(Context context) {
        Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    if (selectedDate.after(currentDate)) {
                        Toast.makeText(view.getContext(), "Seleziona una data precedente o uguale alla data corrente", Toast.LENGTH_SHORT).show();
                    } else {
                        String dateOfBirth = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dataNascita.setText(dateOfBirth);
                    }
                }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
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

                ArrayAdapter<CentroAccoglienza> centroAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, centriAccoglienza);
                centroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                centroAccoglienza.setAdapter(centroAdapter);
            } else {
                Toast.makeText(requireContext(), "Errore nel recupero dei centri di accoglienza", Toast.LENGTH_SHORT).show();
            }
        });
    }
}