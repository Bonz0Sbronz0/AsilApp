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

import java.util.Map;

import it.uniba.dib.sms232413.Database.DBCentroAccoglienza;
import it.uniba.dib.sms232413.object.CentroAccoglienza;
import it.uniba.dib.sms232413.R;

public class WhereFragment extends Fragment {
    public WhereFragment() {
        // Required empty public constructor
    }

    TextView streetTextView, cityTextView, provinciaTextView, regioneTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_where, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        streetTextView = view.findViewById(R.id.street);
        cityTextView = view.findViewById(R.id.city);
        provinciaTextView = view.findViewById(R.id.provincia);
        regioneTextView = view.findViewById(R.id.regione);
    }

    @Override
    public void onStart() {
        super.onStart();
        new DBCentroAccoglienza().read(CENTRO_CORRENTE)
                .addOnSuccessListener(this::getLocation);
    }

    private void getLocation(CentroAccoglienza centroAccoglienza) {
        Map<String, String> localita = centroAccoglienza.getLocalita();

        streetTextView.setText(String.format("%s %s\n",
                getResources().getText(R.string.street_placeholder), localita.get("via")));
        cityTextView.setText(String.format("%s %s\n",
                getResources().getText(R.string.city_placeholder), localita.get("citta")));
        provinciaTextView.setText(String.format("%s %s\n",
                getResources().getText(R.string.provincia_placeholder), localita.get("provincia")));
        regioneTextView.setText(String.format("%s %s\n",
                getResources().getText(R.string.regione_placeholder), localita.get("regione")));
    }
}