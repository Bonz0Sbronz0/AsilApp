package it.uniba.dib.sms232413.Database;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms232413.object.CentroAccoglienza;

public final class DBCentroAccoglienza implements Database, Storage {
    private static final String COLLECTION_NAME = "centri_di_accoglienza";
    private final CollectionReference collectionReference;
    private final StorageReference storageReference;
    private final List<CentroAccoglienza> centri = new ArrayList<>();
    public CollectionReference getCollectionInstance(){
        return collectionReference;
    }

    public StorageReference getStorageInstance() {
        return storageReference;
    }

    public DBCentroAccoglienza() {
        this.collectionReference = getInstance(COLLECTION_NAME);
        this.storageReference = getStorageReference(COLLECTION_NAME);
    }

    @Override
    public void write() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Task<CentroAccoglienza> read(String name) {
        return this.getCollectionInstance()
                .document(name)
                .get()
                .continueWith(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                   if (documentSnapshot.exists()){
                       return documentSnapshot.toObject(CentroAccoglienza.class);
                   }else{
                       return null;
                   }
                });
    }

    public Task<List<CentroAccoglienza>> readAll() {
        return this.getCollectionInstance()
                .get()
                .continueWith(task -> {
                   if (task.isSuccessful()){
                        QuerySnapshot documentSnapshot = task.getResult();
                        for (QueryDocumentSnapshot document : documentSnapshot){
                            CentroAccoglienza centroAccoglienza = document.toObject(CentroAccoglienza.class);
                            centri.add(centroAccoglienza);
                        }
                        return centri;
                   }else{
                       return null;
                   }
                });
    }

    @Override
    public Task<?> read() {
        return null;
    }

    @Override
    public Task<byte[]> downloadFromStorage(String path, String fileName) {
        final long ONE_MEGABYTE = 1_024L*1_024;
        return this.getStorageInstance()
                .child(path)
                .child(fileName)
                .getBytes(ONE_MEGABYTE);
    }
}
