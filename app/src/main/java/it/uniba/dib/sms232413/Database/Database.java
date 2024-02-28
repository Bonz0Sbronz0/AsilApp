package it.uniba.dib.sms232413.Database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

sealed interface Database permits DBCentroAccoglienza, DBUtenti {

    default CollectionReference getInstance(String collectionName) {
        return FirebaseFirestore.getInstance().collection(collectionName);
    }

    void write();

    Task<?> read(String collectionName);
    Task<?> read();

}

