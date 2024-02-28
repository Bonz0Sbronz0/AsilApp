package it.uniba.dib.sms232413.object;

import android.util.Log;

import java.util.List;

import it.uniba.dib.sms232413.Database.DBUtenti;

public class CurrentUserFetcher {

    private Profilo currentUser;
    private static CurrentUserFetcher currentUserFetcher;

    private final DBUtenti dbUtenti;
    private OnUserFetchListener listener;

    private CurrentUserFetcher(String currentUserEmail) {
        dbUtenti = new DBUtenti();
        setCurrentUser(currentUserEmail);
    }


    public static synchronized CurrentUserFetcher getInstance(String currentUserEmail){
        if (currentUserFetcher == null){
            currentUserFetcher = new CurrentUserFetcher(currentUserEmail);
        }
        return currentUserFetcher;
    }
    private <T extends Profilo> void setCurrentUser(String currentUserEmail){

        dbUtenti.readAllProfiles().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                List<T> listaProfili = (List<T>) task.getResult();
                for(Profilo profilo : listaProfili){
                    if (profilo.getEmail().equals(currentUserEmail)){
                        this.currentUser = profilo;
                        if (listener!=null){
                            listener.onUserFetched(this.currentUser);
                        }
                        break;
                    }
                }
            } else {
                Log.e("profile section", "Error fetching patients: " + task.getException());
            }
        });
    }
    public Profilo getCurrentUser() {
        return currentUser;
    }
    public void setUserFetchListener(OnUserFetchListener listener){
        this.listener = listener;
    }
    public interface OnUserFetchListener{
        void onUserFetched(Profilo currentUser);
    }
}
