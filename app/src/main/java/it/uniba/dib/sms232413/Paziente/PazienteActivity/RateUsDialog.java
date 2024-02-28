package it.uniba.dib.sms232413.Paziente.PazienteActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.uniba.dib.sms232413.R;

public class RateUsDialog extends Dialog {
    private AppCompatButton rateNowBttn;
    private AppCompatButton rateLaterBttn;
    private RatingBar ratingBar;
    private ImageView ratingImage;
    private EditText  sendComment;

    private static final int REMINDER_DELAY_MILLIS = 5000; //5 seconds
    private final Handler handler = new Handler();

    private DatabaseReference rootDatabaseRef;

    public RateUsDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_us_dialog_layout);

        rateNowBttn   = findViewById(R.id.bttn_rateNow);
        rateLaterBttn = findViewById(R.id.bttn_rateLater);
        ratingBar     = findViewById(R.id.ratingBar);
        ratingImage   = findViewById(R.id.ratingImage);
        sendComment   = findViewById(R.id.commentEditText);

        //Il pulsante "INVIA" è inizialmente disattivato
        updateRateNowBttnState();

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if(rating <= 1 && rating > 0) {
                ratingImage.setImageResource(R.drawable.angry_1);
            }
            else if(rating <= 2) {
                ratingImage.setImageResource(R.drawable.sad_2);
            }
            else if(rating <= 3) {
                ratingImage.setImageResource(R.drawable.soaked_3);
            }
            else if(rating <= 4) {
                ratingImage.setImageResource(R.drawable.laughing_4);
            }
            else if(rating <= 5) {
                ratingImage.setImageResource(R.drawable.like_5);
            }
            // Abilita il pulsante "INVIA" quando viene assegnato un voto
            updateRateNowBttnState();
        });

        sendComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                updateRateNowBttnState();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                updateRateNowBttnState();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Abilita il pulsante "INVIA" quando viene inserito del testo
                updateRateNowBttnState();
            }
        });

        rateNowBttn.setOnClickListener(v -> {
            float  ratingValue  = ratingBar.getRating();
            String commentValue = sendComment.getText().toString();

            //Invio dei dati sul database Firebase
            rootDatabaseRef = FirebaseDatabase.getInstance().getReference();
            rootDatabaseRef.child("Votazione").setValue(ratingValue);
            rootDatabaseRef.child("Recensione").setValue(commentValue);

            //Fine del pop-up
            dismiss();
        });


        rateLaterBttn.setOnClickListener(v -> {
            // Chiude il pop-up
            dismiss();

            // Disabilita il pulsante "Ricorda più tardi" per evitare ulteriori clic


            // Avvia un handler con ritardo per riaprire il pop-up dopo un certo tempo
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Riapre il pop-up dopo il ritardo
                    updateRateNowBttnState();
                    show();
                }
            }, REMINDER_DELAY_MILLIS);
        });

    }
    // Metodo per aggiornare lo stato del pulsante "INVIA"
    private void updateRateNowBttnState() {
        //E' inizialmente disattivato
        rateNowBttn.setEnabled(ratingBar.getRating() > 0);
        //Si abilita e diventa visibile quando l'utente vota tramite stelle o tramite commento
    }

}
