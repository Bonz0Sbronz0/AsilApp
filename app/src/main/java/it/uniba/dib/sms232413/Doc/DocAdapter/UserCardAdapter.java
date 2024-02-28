package it.uniba.dib.sms232413.Doc.DocAdapter;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import it.uniba.dib.sms232413.Database.DBUtenti;
import it.uniba.dib.sms232413.object.Paziente;
import it.uniba.dib.sms232413.object.UserCard;

public class UserCardAdapter {
    private static final DBUtenti dbUtenti = new DBUtenti();


    private static Task<Paziente> findCurrentPatientById(String id){
       return dbUtenti.read()
                .continueWith(task->{
                    List<Paziente> listaPazienti = task.getResult();
                    Paziente patient = new Paziente();
                    for(Paziente paziente : listaPazienti){
                        if (paziente.getId().equals(id)){
                            Log.d("profile section", "gli id sono uguali");
                            patient = paziente;
                            break;
                        }
                    }
                    return patient;
                });
    }

    private static Task<Paziente> findCurrentPatientByEmail(String email){
        return dbUtenti.read()
                .continueWith(task->{
                    List<Paziente> listaPazienti = task.getResult();
                    Paziente patient = new Paziente();
                    for(Paziente paziente : listaPazienti){
                        if (paziente.getEmail().equals(email)){
                            Log.d("profile section", "le email sono uguali");
                            patient = paziente;
                            break;
                        }
                    }
                    return patient;
                });
    }

    private static void getCurrentPatientByID(String id, OnCompleteListener<Paziente> onCompleteListener){
         findCurrentPatientById(id).addOnCompleteListener(onCompleteListener);
    }

    public static void getCurrentPatientByEmail(String email, OnCompleteListener<Paziente> onCompleteListener){
         findCurrentPatientByEmail(email).addOnCompleteListener(onCompleteListener);
    }

    public static void setCurrentPatienceCard(UserCard currentPatienceCard,String id, Context context){
        TextView name = currentPatienceCard.getNomeTextView();
        TextView cognome = currentPatienceCard.getCognomeTextView();
        getCurrentPatientByID(id, task->{
            if (task.isSuccessful()){
                Paziente paziente = task.getResult();
                currentPatienceCard.setCurrentPatient(paziente);
                name.setText(paziente.getName());
                cognome.setText(paziente.getSurname());
                currentPatienceCard.showPopup();
            }else{
                Toast.makeText(context, "PAZIENTE NON TROVATO", Toast.LENGTH_SHORT ).show();
            }
        });
    }


}
