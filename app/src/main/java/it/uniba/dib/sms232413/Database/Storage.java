package it.uniba.dib.sms232413.Database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

sealed interface Storage permits DBCentroAccoglienza, DBUtenti {

    default StorageReference getStorageReference(String nameRootPath) {
        return FirebaseStorage.getInstance().getReference(nameRootPath);
    }

    Task<byte[]> downloadFromStorage(String path, String nomeFile);


}
