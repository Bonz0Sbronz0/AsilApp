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

import it.uniba.dib.sms232413.Paziente.PazienteListener.AllCostsSelectListener;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.Costs;

public class AllCostsAdapter extends RecyclerView.Adapter<AllCostsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Costs> allCostsArrayList;
    AllCostsSelectListener allCostsSelectListener;

    public AllCostsAdapter(Context context, ArrayList<Costs> allCostsArrayList, AllCostsSelectListener allCostsSelectListener) {
        this.context = context;
        this.allCostsArrayList = allCostsArrayList;
        this.allCostsSelectListener = allCostsSelectListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_view_all_costs,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        Costs costs = allCostsArrayList.get(position);
        holder.productName.setText(costs.name);
        holder.quantity.setText(costs.quantity);
        holder.price.setText(costs.price + " €");
        holder.category.setText(costs.category);

    }

    @Override
    public int getItemCount() {
        return allCostsArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView productName;
        TextView quantity;
        TextView category;
        TextView price;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.inputNomeProdotto);
            quantity = itemView.findViewById(R.id.inputQuantity);
            category = itemView.findViewById(R.id.inputCategory2);
            price = itemView.findViewById(R.id.inputPrice);
            cardView = itemView.findViewById(R.id.cardView2);
        }
    }

    public void setFilteredList(ArrayList<Costs> filteredList){
        this.allCostsArrayList = filteredList;
        notifyDataSetChanged();
    }

}
