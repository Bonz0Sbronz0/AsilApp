package it.uniba.dib.sms232413.Paziente.PazienteFragment.InfoCentroFragment;

import static it.uniba.dib.sms232413.Paziente.PazienteActivity.InfoCentroActivity.CENTRO_CORRENTE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.uniba.dib.sms232413.Database.DBCentroAccoglienza;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Paziente.PazienteFragment.InfoCentroFragment.utility.XMLUtility;
import it.uniba.dib.sms232413.Paziente.PazienteAdapter.ItemAdapter;
import it.uniba.dib.sms232413.object.DataModel;

public class ServiceRuleFragment extends Fragment {
    //TextView rulesTextView,servicesTextView;
    RecyclerView recyclerView;
    ItemAdapter adapter;
    List<DataModel> modelList;
    DBCentroAccoglienza db = new DBCentroAccoglienza();
    public ServiceRuleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_rule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rules_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        modelList=new ArrayList<>();
        //costruisco le due liste principali
        List<String> rulesList = new ArrayList<>();
        List<String> servicesList = new ArrayList<>();

        showList(rulesList, "REGOLE","rules", "rules.xml");
        showList(servicesList, "SERVIZI","services", "services.xml");

    }

    private void showList(List<String> list, String listName, String typeOfList, String fileName) {
        getXMLFile(typeOfList, fileName).thenAccept(result->{
            list.addAll(result);
            modelList.add(new DataModel(list, listName));
            adapter = new ItemAdapter(modelList);
            recyclerView.setAdapter(adapter);
        }).exceptionally(e->{
            Toast.makeText(requireContext(), "Impossibile caricare le informazioni", Toast.LENGTH_SHORT)
                    .show();
            return null;
        });
    }

    //metodo utile per far ritornare in maniera asincrona i risultati del parser
    private CompletableFuture<List<String>> getXMLFile(String type, String nomeFile) {
        CompletableFuture<List<String>> resultFuture  = new CompletableFuture<>();
        db.downloadFromStorage(CENTRO_CORRENTE, nomeFile).addOnSuccessListener(task -> {
            try {
                resultFuture.complete(XMLUtility.XMLParsing(new String(task, StandardCharsets.UTF_8),type));
            } catch (XmlPullParserException | IOException e) {
                throw new RuntimeException(e);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed Download", Toast.LENGTH_SHORT).show();
        });
        return resultFuture;
    }
}