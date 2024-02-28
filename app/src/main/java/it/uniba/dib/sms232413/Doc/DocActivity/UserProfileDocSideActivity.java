package it.uniba.dib.sms232413.Doc.DocActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import it.uniba.dib.sms232413.Paziente.PazienteAdapter.UserProfileAdapter;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.Paziente;

public class UserProfileDocSideActivity extends AppCompatActivity {
    Paziente pazienteSelezionato;
    Button pdfButton, misureButton;
    View currentView;

    UserProfileAdapter userProfileAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_doc_side);

        pdfButton = findViewById(R.id.pdfButton);
        misureButton = findViewById(R.id.misureButton);
        currentView = findViewById(R.id.profile_container_DocSide);
        pazienteSelezionato = getIntent().getParcelableExtra("user_selected");

        userProfileAdapter = new UserProfileAdapter(currentView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        userProfileAdapter.setProfileView(pazienteSelezionato);
        pdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DocumentDocActivity.class);
                intent.putExtra("paziente", pazienteSelezionato);
                startActivity(intent);
            }
        });
        misureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StoricoMisurazioneDocActivity.class);
                intent.putExtra("paziente", pazienteSelezionato);
                startActivity(intent);
            }
        });
    }
}