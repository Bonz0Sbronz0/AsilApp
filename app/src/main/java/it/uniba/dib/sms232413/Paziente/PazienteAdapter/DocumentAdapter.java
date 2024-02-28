package it.uniba.dib.sms232413.Paziente.PazienteAdapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import it.uniba.dib.sms232413.Paziente.PazienteListener.AllDocumentsSelectListener;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.Documento;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    static ArrayList<Documento> documentiList;
    Context context;
    AllDocumentsSelectListener allDocumentSelectListener;
    StorageReference storageReference;
    String userId;

    public DocumentAdapter(ArrayList<Documento> documentiList, Context context, AllDocumentsSelectListener allDocumentSelectListener, String userId) {
        this.documentiList = documentiList;
        this.context = context;
        this.allDocumentSelectListener = allDocumentSelectListener;
        this.userId = userId; // Aggiunta dell'userId
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_document_activity, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Documento documento = documentiList.get(position);
        storageReference = FirebaseStorage.getInstance().getReference();
        holder.nomeDocumento.setText(documento.getNome().toUpperCase());

        // Gestione del click sul pulsante "Download"
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 23);
                } else {
                    StorageReference documentRef = storageReference.child("documenti/documenti-" + userId + "/" + documento.getNome());
                    File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), documento.getNome());
                    documentRef.getFile(localFile).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, context.getResources().getString(R.string.download_successful), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.download_failed), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Gestione del click sul pulsante "View"
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference documentRef = storageReference.child("documenti/documenti-" + userId + "/" + documento.getNome());
                File localFile = new File(context.getFilesDir(), documento.getNome());
                documentRef.getFile(localFile).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Apri il documento
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(FileProvider.getUriForFile(context, "it.uniba.dib.sms232413.provider", localFile), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(context, context.getResources().getString(R.string.unable_to_open_document), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.document_not_found), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Gestione del click sul pulsante "Share"
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference documentRef = storageReference.child("documenti/documenti-" + userId + "/" + documento.getNome());
                File localFile = new File(context.getFilesDir(), documento.getNome());
                documentRef.getFile(localFile).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Condividi il documento
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("application/pdf");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "it.uniba.dib.sms232413.provider", localFile));
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.share_document_using)));
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.document_not_found), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Gestione del click sul pulsante "Delete"
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creazione del Dialog personalizzato
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_popup_confirm_delete); // Imposta il layout XML del popup
                dialog.setCancelable(true); // Consentire la chiusura del Dialog cliccando al di fuori

                // Recupera i pulsanti dal layout del Dialog
                Button btnAccept = dialog.findViewById(R.id.btn_accept);
                Button btnDecline = dialog.findViewById(R.id.btn_decline);

                // Gestione del click sul pulsante "Yes"
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Eliminazione del documento
                        StorageReference documentRef = storageReference.child("documenti/documenti-" + userId + "/" + documento.getNome());
                        documentRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Rimuovi il documento dalla lista
                                documentiList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, documentiList.size());
                                Toast.makeText(context, context.getResources().getString(R.string.document_deleted), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, context.getResources().getString(R.string.document_deletion_failed), Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Chiudi il Dialog
                        dialog.dismiss();
                    }
                });

                // Gestione del click sul pulsante "No"
                btnDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Chiudi il Dialog senza effettuare alcuna azione
                        dialog.dismiss();
                    }
                });

                // Mostra il Dialog
                dialog.show();
            }
        });



    }


    @Override
    public int getItemCount() {
        return documentiList.size(); // Usa la dimensione della lista filtrata
    }

    public class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView nomeDocumento;
        ImageView update, view, share, delete;
        CardView cardView;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewDocument);
            nomeDocumento = itemView.findViewById(R.id.singleDocTextView);
            update = itemView.findViewById(R.id.downloadDocumentButton);
            view = itemView.findViewById(R.id.readDocumentButton);
            share = itemView.findViewById(R.id.shareDocumentButton);
            delete = itemView.findViewById(R.id.deleteDocumentButton);
        }
    }

    // Metodo per filtrare la lista
    public void setFilteredList(ArrayList<Documento> filteredList){
        this.documentiList = filteredList;
        notifyDataSetChanged();
    }
}