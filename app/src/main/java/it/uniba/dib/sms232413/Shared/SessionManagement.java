package it.uniba.dib.sms232413.Shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import it.uniba.dib.sms232413.object.CurrentUserFetcher;
import it.uniba.dib.sms232413.object.Paziente;
import it.uniba.dib.sms232413.object.PersonaleAutorizzato;
import it.uniba.dib.sms232413.object.Profilo;

public class SessionManagement{
    private static final String SHARED_NAME = "it.uniba.dib.sms232413.MY_PROFILE";
    int PRIVATE_MODE = Context.MODE_PRIVATE;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private CurrentUserFetcher currentUserFetcher;



    public SessionManagement(Context context, String email){
        sharedPreferences = context.getSharedPreferences(SHARED_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
        gson = new Gson();
        currentUserFetcher = CurrentUserFetcher.getInstance(email);
        currentUserFetcher.setUserFetchListener(this::saveSession);
    }

    public void updateUserProfile(String name, String surname, String gender, String receptionceter, String phone) {
        // Recupera il profilo dell'utente paziente attualmente memorizzato
        Paziente currentUser = getUserPatientSession();
        if(currentUser != null) {
            // Aggiorna i dati del profilo con i nuovi valori
            currentUser.setName(name);
            currentUser.setSurname(surname);
            currentUser.setGender(gender);
            currentUser.setReceptionceter(receptionceter);
            currentUser.setPhone(phone);
            // Salva il profilo aggiornato nelle preferenze condivise
            saveSession(currentUser);
        }
    }


    public SessionManagement(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }
    private void saveSession(Profilo utente){
        if (utente instanceof Paziente paziente){
            String utenteGson = gson.toJson(paziente);
            editor.putString("utente_paziente", utenteGson);
        }else if(utente instanceof  PersonaleAutorizzato personaleAutorizzato){
            String utenteGson = gson.toJson(personaleAutorizzato);
            editor.putString("utente_personaleAutorizzato", utenteGson);
        }
        editor.apply();
    }

    public Paziente getUserPatientSession(){
            String utenteJson = sharedPreferences.getString("utente_paziente", null);
            if (utenteJson!=null){
                return gson.fromJson(utenteJson, Paziente.class);
            }

        return null;
    }public PersonaleAutorizzato getUserAuthSession(){
        String utenteJson = sharedPreferences.getString("utente_personaleAutorizzato", null);
        if (utenteJson!=null){
            return gson.fromJson(utenteJson, PersonaleAutorizzato.class);
        }
        return null;
    }


}
