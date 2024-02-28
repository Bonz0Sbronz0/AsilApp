package it.uniba.dib.sms232413.Doc.DocAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.dib.sms232413.Doc.DocListener.ListenerUserList;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.Paziente;
import it.uniba.dib.sms232413.Doc.DocActivity.UserProfileDocSideActivity;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.UserSearch> {

    Context context;
    ArrayList<Paziente> userSearchArrayList;
    ListenerUserList listener;

    public ListUserAdapter(Context context, ArrayList<Paziente> userSearchArrayList, ListenerUserList listener) {
        this.context = context;
        this.userSearchArrayList = userSearchArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserSearch onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_list_user, parent, false);
        return new UserSearch(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListUserAdapter.UserSearch holder, final int position) {

        Paziente user = userSearchArrayList.get(position);

        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
        holder.surname.setText(user.getSurname());
        holder.age.setText(String.valueOf(user.getBirthdate()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), UserProfileDocSideActivity.class);
            intent.putExtra("user_selected", user);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userSearchArrayList.size();
    }

    public static class UserSearch extends RecyclerView.ViewHolder {
        TextView email;
        TextView age;
        TextView name;
        TextView surname;
        CircleImageView circleImageView;

        public UserSearch(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.userName);
            email = itemView.findViewById(R.id.emailUser);
            age = itemView.findViewById(R.id.userAge);
            surname = itemView.findViewById(R.id.userSurname);
            circleImageView = itemView.findViewById(R.id.imageView);
        }
    }

    public void setFilteredList(ArrayList<Paziente> filteredList) {
        this.userSearchArrayList = filteredList;
        notifyDataSetChanged();
    }
}
