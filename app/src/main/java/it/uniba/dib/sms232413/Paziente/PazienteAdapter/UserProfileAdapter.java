package it.uniba.dib.sms232413.Paziente.PazienteAdapter;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import it.uniba.dib.sms232413.Database.DBCentroAccoglienza;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.CentroAccoglienza;
import it.uniba.dib.sms232413.object.Paziente;

public class UserProfileAdapter {
    DBCentroAccoglienza dbCentroAccoglienza = new DBCentroAccoglienza();
    private final TextView name, surname, gender, birthdate, birthplace, email, phone, centroAccoglienza;
    public UserProfileAdapter(View profileView){
        name = profileView.findViewById(R.id.name);
        surname = profileView.findViewById(R.id.surname);
        gender = profileView.findViewById(R.id.gender);
        birthdate = profileView.findViewById(R.id.birthdate);
        birthplace = profileView.findViewById(R.id.placebirth);
        email = profileView.findViewById(R.id.user_email_profile);
        phone = profileView.findViewById(R.id.phone);
        centroAccoglienza = profileView.findViewById(R.id.centroAccoglienza);
    }

    public void setProfileView(Paziente paziente){
        name.setText(paziente.getName());
        surname.setText(paziente.getSurname());
        gender.setText(paziente.getGender());
        birthdate.setText(paziente.getBirthdate());
        birthplace.setText(paziente.getPlaceBirth());
        email.setText(paziente.getEmail());
        phone.setText(paziente.getPhone());
        findReceptionCenter(paziente);
        if (paziente.getGender().equals("Maschio")) {
            gender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_male_24, 0, 0, 0);
        } else if (paziente.getGender().equals("Femmina")) {
            gender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_female_24, 0, 0, 0);
        } else {
            gender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_question_mark_24, 0, 0, 0);
        }
    }

    private void findReceptionCenter(Paziente paziente){
         dbCentroAccoglienza.readAll()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        List<CentroAccoglienza> centri = task.getResult();
                        for (CentroAccoglienza centro : centri){
                            if (centro.getNameId().equals(paziente.getReceptionceter())){
                                centroAccoglienza.setText(centro.getNome());
                            }
                        }
                    }
                });
    }
}
