package it.uniba.dib.sms232413.Paziente.PazienteActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import it.uniba.dib.sms232413.R;

public class AddCostsFragment extends Fragment {

    Spinner costCategory;
    EditText costPrice;
    EditText costName;
    EditText costQuantity;
    Button btnSave;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_costs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        db = FirebaseFirestore.getInstance();
        String randomId = UUID.randomUUID().toString();

        costCategory = view.findViewById(R.id.category);
        costPrice = view.findViewById(R.id.costPrice);
        costName = view.findViewById(R.id.costName);
        costQuantity = view.findViewById(R.id.costQuantity);

        btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {

            String name = costName.getText().toString();
            String price = costPrice.getText().toString();
            String quantity = costQuantity.getText().toString();
            String category = costCategory.getSelectedItem().toString();

            SimpleDateFormat date = new SimpleDateFormat("dd_MM_yyyy", Locale.ITALY);
            Date now = new Date();
            String date_DD_MM_YYYY = date.format(now);

            if(price.isEmpty()){
                costPrice.setError(getString(R.string.checkPrice));
                costPrice.requestFocus();
            } else if(name.isEmpty()){
                costName.setError(getString(R.string.checkName));
                costName.requestFocus();
            }else if(quantity.isEmpty() || Integer.parseInt(quantity) <= 0){
                costQuantity.setError(getString(R.string.checkQuantity));
                costQuantity.requestFocus();
            }else if(category.equals(getString(R.string.all_categories))){
                Toast.makeText(requireActivity(), getString(R.string.invCategory), Toast.LENGTH_SHORT).show();
                costCategory.requestFocus();
            }else {

                Map<String,Object> costs = new HashMap<>();
                costs.put("id", randomId);
                costs.put("name", name);
                costs.put("price", price);
                costs.put("quantity", quantity);
                costs.put("category", category);
                costs.put("date", date_DD_MM_YYYY);
                costs.put("userEmail", userEmail);
                costs.put("userId", userID);

                db.collection("costs").document(randomId).set(costs);
                Toast.makeText(requireContext(), R.string.succAddCost, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireContext(), CostActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }

        });
    }
}