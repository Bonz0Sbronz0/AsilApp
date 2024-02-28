package it.uniba.dib.sms232413.Database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.uniba.dib.sms232413.object.Paziente;
import it.uniba.dib.sms232413.object.PersonaleAutorizzato;
import it.uniba.dib.sms232413.object.Profilo;

public final class DBUtenti implements Database, Storage {
    private static final String COLLECTION_NAME = "User";
    private static final String STORAGE_ROOT = "documenti";
    private final CollectionReference collectionReference;

    private final StorageReference storageReference;

    public CollectionReference getCollectionInstance() {
        return collectionReference;
    }


    public DBUtenti() {
        this.collectionReference = getInstance(COLLECTION_NAME);
        this.storageReference = getStorageReference(STORAGE_ROOT);
    }

    @Override
    public CollectionReference getInstance(String collectionName) {
        return Database.super.getInstance(collectionName);
    }

    @Override
    public void write() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Task<?> read(String collectionName) {
        return null;
    }

    @Override
    public Task<List<Paziente>> read() {
        String emailCurrentDoc = Objects.requireNonNull(getCurrentUser()).getEmail();
        return getCollectionInstance()
                .whereEqualTo("emailDoc", emailCurrentDoc)
                .whereEqualTo("type", "user")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return task.getResult().toObjects(Paziente.class);
                    } else {
                        return null;
                    }
                });
    }


    public Task<List<Profilo>> readAllProfiles() {
        return getCollectionInstance()
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<Profilo> profiles = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                if (document.contains("type") &&
                                        Objects.equals(document.getString("type"), "user")) {
                                    profiles.add(document.toObject(Paziente.class));
                                } else {
                                    profiles.add(document.toObject(PersonaleAutorizzato.class));
                                }
                            }
                        }
                        return profiles;
                    } else {
                        return null;
                    }
                });
    }

    public Task<PersonaleAutorizzato> findDocByEmail(String docEmail){
        return getCollectionInstance()
                .whereEqualTo("type", "doc")
                .whereEqualTo("email", docEmail)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot doc:task.getResult()){
                            if (doc.toObject(PersonaleAutorizzato.class).getEmail().equals(docEmail)){
                                return doc.toObject(PersonaleAutorizzato.class);
                            }
                        }

                    }
                    return null;
                });
    }

    public Task<Paziente> findUserByEmail(String email){
        return getCollectionInstance()
                .whereEqualTo("type", "user")
                .whereEqualTo("email", email)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot doc:task.getResult()){
                            if (doc.toObject(Paziente.class).getEmail().equals(email)){
                                return doc.toObject(Paziente.class);
                            }
                        }

                    }
                    return null;
                });
    }

    @NonNull
    public FirebaseUser getCurrentUser() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
    }

    public StorageReference getStorageInstance() {
        return storageReference;
    }

    @Override
    public StorageReference getStorageReference(String nameRootPath) {
        return Storage.super.getStorageReference(nameRootPath);
    }

    @Override
    public Task<byte[]> downloadFromStorage(String path, String nomeFile) {
        return null;
    }

    public String createDefaultPatientDocFolder(String id){
        String path = "documenti-" + id + "/";
        StorageReference docFoldRef = getStorageInstance().child(path);

        //recupero del documento di default
        StorageReference defaultExistingPdfReference = getStorageInstance().child("default_document/la_guida_in_italiano.pdf");

        StorageReference defaultPdfCopyReference = docFoldRef.child("la_guida_in_italiano.pdf");

        //copia di tale documento nella cartella del paziente
        defaultExistingPdfReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            defaultPdfCopyReference.putBytes(bytes)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("pdf creato", "PDF copiato con successo nella cartella del paziente");
                    })
                    .addOnFailureListener(exception -> {
                        // Gestire eventuali errori durante la copia del PDF
                        Log.e("pdf creato", "Errore durante la copia del PDF nella cartella del paziente", exception);
                    });
        }).addOnFailureListener(exception -> {
            // Gestire eventuali errori durante il recupero del PDF
            Log.e("pdf creato", "Errore durante il recupero del PDF", exception);
        });
        return path;
    }
}