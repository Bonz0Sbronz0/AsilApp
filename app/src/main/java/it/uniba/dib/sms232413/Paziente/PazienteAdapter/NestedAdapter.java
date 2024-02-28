package it.uniba.dib.sms232413.Paziente.PazienteAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232413.R;

public class NestedAdapter extends RecyclerView.Adapter<NestedAdapter.NestedViewHolder> {
    private final List<String> mList;

    public NestedAdapter(List<String> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public NestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nested_rules_service, parent, false);
        return new NestedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NestedViewHolder holder, int position) {
        holder.nestedTextView.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class NestedViewHolder extends RecyclerView.ViewHolder{
        private final TextView nestedTextView;
        public NestedViewHolder(@NonNull View itemView) {
            super(itemView);
            nestedTextView = itemView.findViewById(R.id.nested_TextView);
        }
    }
}
