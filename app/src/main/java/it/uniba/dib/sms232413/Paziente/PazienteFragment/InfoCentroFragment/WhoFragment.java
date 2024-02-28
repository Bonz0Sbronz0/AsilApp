package it.uniba.dib.sms232413.Paziente.PazienteFragment.InfoCentroFragment;

import static it.uniba.dib.sms232413.Paziente.PazienteActivity.InfoCentroActivity.CENTRO_CORRENTE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232413.Database.DBCentroAccoglienza;
import it.uniba.dib.sms232413.R;

public class WhoFragment extends Fragment {


    public WhoFragment() {
        // Required empty public constructor
    }


    TextView textViewNome, textViewEmail, textViewTelefono;
    ImageSlider imageSlider;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_who, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageSlider = view.findViewById(R.id.imageSlider);
        textViewNome = view.findViewById(R.id.nome);
        textViewEmail = view.findViewById(R.id.email);
        textViewTelefono = view.findViewById(R.id.telefono);
    }

    @Override
    public void onStart() {
        super.onStart();
        createCentroAccoglienza();
    }

    private void createCentroAccoglienza(){
        DBCentroAccoglienza db = new DBCentroAccoglienza();
        db.read(CENTRO_CORRENTE).addOnSuccessListener(documentSnapshot->{
            String name = documentSnapshot.getNome().toUpperCase();
            Map<String, String> contatti = documentSnapshot.getContatti();
            List<String> imgReference = documentSnapshot.getImgReference();
            createImgSlider(imgReference);

            textViewNome.setText(name);
            textViewEmail.setText(String.format("%s %s\n",getResources().getString(R.string.email_placeholder),
                    contatti.get("email") ));

            textViewTelefono.setText(String.format("%s %s\n",getResources().getString(R.string.telephone_placeholder),
                    contatti.get("telefono") ));


        });
    }

    private void createImgSlider(List<String> imgReferences){
        List<SlideModel> slideModels = new ArrayList<>();
        for (String imgReference : imgReferences){
            slideModels.add(new SlideModel(imgReference, ScaleTypes.FIT));
        }
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
    }

}
