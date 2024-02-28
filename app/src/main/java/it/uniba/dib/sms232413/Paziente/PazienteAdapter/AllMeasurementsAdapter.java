package it.uniba.dib.sms232413.Paziente.PazienteAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms232413.Paziente.PazienteListener.AllMeasurementsListener;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.Misurazioni;

public class AllMeasurementsAdapter extends RecyclerView.Adapter<AllMeasurementsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Misurazioni> allMeasurementArrayList;
    AllMeasurementsListener allMeasurementsListener;

    public AllMeasurementsAdapter(Context context, ArrayList<Misurazioni> allMeasurementArrayList, AllMeasurementsListener allMeasurementsListener) {
        this.context = context;
        this.allMeasurementArrayList = allMeasurementArrayList;
        this.allMeasurementsListener = allMeasurementsListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_view_all_measurements,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        Misurazioni misurazioni = allMeasurementArrayList.get(position);
        holder.emailUser.setText("Email: " + misurazioni.userEmail);
        holder.data.setText("Data: " + misurazioni.data);
        holder.categoria.setText(misurazioni.categoria + ": " + "\"" + misurazioni.risultato + "\"");

    }

    @Override
    public int getItemCount() {
        return allMeasurementArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView emailUser;
        TextView data;
        TextView categoria;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            emailUser = itemView.findViewById(R.id.emailUser);
            data = itemView.findViewById(R.id.data);
            categoria = itemView.findViewById(R.id.categoria);
            cardView = itemView.findViewById(R.id.cardViewMeasurements);
        }
    }

    public void setFilteredList(ArrayList<Misurazioni> filteredList){
        this.allMeasurementArrayList = filteredList;
        notifyDataSetChanged();
    }

}
