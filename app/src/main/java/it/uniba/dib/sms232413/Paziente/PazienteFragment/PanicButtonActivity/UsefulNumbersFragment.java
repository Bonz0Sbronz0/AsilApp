package it.uniba.dib.sms232413.Paziente.PazienteFragment.PanicButtonActivity;

import static it.uniba.dib.sms232413.Paziente.PazienteActivity.PanicButtonActivity.REQUEST_CALL_PHONE_PERMISSION;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import it.uniba.dib.sms232413.Database.DBUtenti;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Shared.SessionManagement;


/**
 * A fragment representing a list of Items.
 */
public class UsefulNumbersFragment extends Fragment {

    private ImageButton callDocButton;
    private ImageButton callAmbulanceButton;
    private ImageButton callPoliceButton;
    private ImageButton callFirefighterButton;
    private TextView docPhoneNumber;
    String docPhone;



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UsefulNumbersFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_useful_numbers, container, false);
        docPhoneNumber = view.findViewById(R.id.docNumber);
        callDocButton = view.findViewById(R.id.callDocButton);
        callAmbulanceButton = view.findViewById(R.id.callAmbulanceButton);
        callPoliceButton = view.findViewById(R.id.callPoliceButton);
        callFirefighterButton = view.findViewById(R.id.callFirefighterButton);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new DBUtenti().findDocByEmail(new SessionManagement(view.getContext()).getUserPatientSession().getEmailDoc())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        docPhone = task.getResult().getPhone();
                        docPhoneNumber.setText(docPhone);
                    }
                });

        callDocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallDocButtonClick(v);
            }
        });

        callAmbulanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallAmbulanceButtonClick(v);
            }
        });

        callPoliceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallPoliceButtonClick(v);
            }
        });

        callFirefighterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallFirefighterButtonClick(v);
            }
        });
    }

    public void onCallDocButtonClick(View view) {
        Context context = view.getContext();
        String phoneNumber = "tel:" + docPhone;
        // Verifica se hai il permesso CALL_PHONE
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            // Hai il permesso, avvia l'intent per effettuare la chiamata
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(phoneNumber));
            startActivity(callIntent);
        } else {
            // Non hai il permesso, richiedilo all'utente
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
        }
        /*
        new DBUtenti().findDocByEmail(new SessionManagement(context).getUserPatientSession().getEmailDoc())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        String docPhoneNumber = task.getResult().getPhone();
                        String phoneNumber = "tel:" + docPhoneNumber;
                        // Verifica se hai il permesso CALL_PHONE
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            // Hai il permesso, avvia l'intent per effettuare la chiamata
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse(phoneNumber));
                            startActivity(callIntent);
                        } else {
                            // Non hai il permesso, richiedilo all'utente
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
                        }
                    }
                });
         */

    }
    public void onCallAmbulanceButtonClick(View view) {
        Context context = view.getContext();

        String phoneNumber = "tel:" + "118";

        // Verifica se hai il permesso CALL_PHONE
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            // Hai il permesso, avvia l'intent per effettuare la chiamata
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(phoneNumber));
            startActivity(callIntent);
        } else {
            // Non hai il permesso, richiedilo all'utente
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
        }
    }
    public void onCallPoliceButtonClick(View view) {
        Context context = view.getContext();
        String phoneNumber = "tel:" + "113";

        // Verifica se hai il permesso CALL_PHONE
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            // Hai il permesso, avvia l'intent per effettuare la chiamata
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(phoneNumber));
            startActivity(callIntent);
        } else {
            // Non hai il permesso, richiedilo all'utente
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
        }
    }
    public void onCallFirefighterButtonClick(View view) {
        Context context = view.getContext();

        String phoneNumber = "tel:" + "115";

        // Verifica se hai il permesso CALL_PHONE
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            // Hai il permesso, avvia l'intent per effettuare la chiamata
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(phoneNumber));
            startActivity(callIntent);
        } else {
            // Non hai il permesso, richiedilo all'utente
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
        }
    }


}
