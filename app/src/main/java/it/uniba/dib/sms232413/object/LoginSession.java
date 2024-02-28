package it.uniba.dib.sms232413.object;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.Objects;

import it.uniba.dib.sms232413.Doc.DocActivity.HomeDocActivity;
import it.uniba.dib.sms232413.Paziente.PazienteActivity.HomeUserActivity;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Shared.ChoseLoginActivity;
import it.uniba.dib.sms232413.Shared.SessionManagement;

public class LoginSession {
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    private static EditText inputEmail;
    private static EditText inputPassword;

    public static void login(Activity activity, String email, String password) {
        View view = activity.findViewById(android.R.id.content);
        setEditTextView(view);
        if (!isNetworkAvailable(view.getContext())) {
            showConnectionDialog(activity, email, password);
        } else if (email.isEmpty() && password.isEmpty()) {
            Intent intent = new Intent(view.getContext(), ChoseLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
        }else {
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            if (!email.matches(emailPattern)) {
                inputEmail.setError(view.getContext().getString(R.string.checkEmail));
            } else if (password.isEmpty() || password.length() < 6) {
                inputPassword.setError(view.getContext().getString(R.string.checkPassword));
            } else {
                // Tentativo di accesso mediante e-mail e password attraverso Firebase Authentication
                mAuth.signInWithEmailAndPassword(email.toLowerCase(Locale.ROOT), password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Se l'accesso è riuscito, ottieni informazioni aggiuntive dell'utente dal database Firestore
                        fstore.collection("User").document(Objects.requireNonNull(task.getResult().getUser()).getUid()).get().addOnCompleteListener(_task -> {
                            if (_task.isSuccessful()) {
                                new SessionManagement(view.getContext(), email);
                                // Invia il prametro "type" alla funzione sendUserToNextActivity
                                sendUserToNextActivity(view.getContext(), Objects.requireNonNull(_task.getResult().getString("type")));
                                Toast.makeText(view.getContext(), view.getContext().getString(R.string.loginSucc), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(view.getContext(), view.getContext().getString(R.string.loginFailed), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(view.getContext(), view.getContext().getString(R.string.loginFailed), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private static void setEditTextView(View view){
        inputEmail = view.findViewById(R.id.inputEmail);
        inputPassword = view.findViewById(R.id.passwordInput);
    }

    // Metodo che indirizza l'utente alla propria pagina home in base al tipo di utente che è
    private static void sendUserToNextActivity(Context context, String type) {
        Intent intent;
        if(type.equals("doc")){
            intent = new Intent(context, HomeDocActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else if(type.equals("user")){
            intent = new Intent(context, HomeUserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else{
            Toast.makeText(context, context.getString(R.string.loginFailed), Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo per mostrare un dialogo in caso di assenza di connessione di rete
    private static void showConnectionDialog(Activity activity, String email, String password) {
        View view = activity.findViewById(android.R.id.content);
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage(view.getContext().getString(R.string.noConnMess))
                .setTitle(view.getContext().getString(R.string.nocConn))
                .setCancelable(false)
                .setPositiveButton(view.getContext().getString(R.string.trytry), (dialog, id) -> {
                    dialog.dismiss();
                    login(activity,email, password);
                })
                .setNegativeButton(view.getContext().getString(R.string.exit), (dialog, id) -> {
                    dialog.dismiss();
                    activity.finish();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    // Metodo per verificare la disponibilità della connessione di rete
    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
}
