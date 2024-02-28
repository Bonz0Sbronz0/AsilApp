package it.uniba.dib.sms232413.Doc.DocActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import it.uniba.dib.sms232413.Database.DBUtenti;
import it.uniba.dib.sms232413.Doc.DocListener.ListenerUserList;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Doc.DocAdapter.ListUserAdapter;
import it.uniba.dib.sms232413.Shared.LoginActivity;
import it.uniba.dib.sms232413.Shared.SessionManagement;
import it.uniba.dib.sms232413.object.Paziente;
import it.uniba.dib.sms232413.object.PersonaleAutorizzato;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ListUserActivity extends AppCompatActivity implements ListenerUserList {
    static ArrayList<Paziente> userArrayList;
    RecyclerView recyclerView;
    SearchView searchView;
    TextView textViewNoData;
    ListUserAdapter listUserAdapter;
    ImageView addUser;

    RecyclerView.LayoutManager layoutManager;
    DBUtenti dbUtenti;

    //date from database
    FirebaseAuth auth;
    BottomNavigationView bottomNavigationViewDoc;
    FloatingActionButton scanQrCode;
    private SwipeRefreshLayout swipeRefreshLayout;
    PersonaleAutorizzato personaleAutorizzato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        dbUtenti = new DBUtenti();
        auth = FirebaseAuth.getInstance();
        searchView = findViewById(R.id.userSearchView);
        searchView.clearFocus();
        textViewNoData = findViewById(R.id.textViewNoData);
        addUser = findViewById(R.id.add_user);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_id);

        personaleAutorizzato = getIntent().getParcelableExtra("user_data");
        addUser.setOnClickListener(v -> getSupportFragmentManager().beginTransaction()
                .replace(R.id.drawer_layout, new AddUserFragment())
                .addToBackStack(null)
                .commit());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return true;
            }
        });
        recyclerView = findViewById(R.id.recyclerListPatientView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadData();
            swipeRefreshLayout.setRefreshing(false);
        });

        userArrayList = new ArrayList<>();
        listUserAdapter = new ListUserAdapter(ListUserActivity.this, userArrayList, this);
        recyclerView.setAdapter(listUserAdapter);
        layoutManager = recyclerView.getLayoutManager();



        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callbackMethod);
        itemTouchHelper.attachToRecyclerView(recyclerView);




        // Ripristina lo stato della lista dopo la rotazione dello schermo, se presente
        if (savedInstanceState != null) {
            ArrayList<Paziente> restoredList = savedInstanceState.getParcelableArrayList("userArrayList");
            if (restoredList != null) {
                userArrayList.addAll(restoredList);
                listUserAdapter.notifyDataSetChanged();
                if (!userArrayList.isEmpty()) {
                    textViewNoData.setVisibility(View.GONE);
                }
            }
        }

    }

    private void loadData() {
        userArrayList.clear();
        eventChangeListener();
    }

    ItemTouchHelper.SimpleCallback callbackMethod = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAbsoluteAdapterPosition();
            switch (direction){
                case ItemTouchHelper.LEFT -> {
                    //remove patient
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListUserActivity.this);
                    builder.setTitle("Avviso")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("Per la rimozione del paziente è OBBLIGATORIO inviare una mail agli enti appositi")
                            .setPositiveButton("Accetta", (dialog, which) -> {
                                // Avvia un'attività per comporre un'email preimpostata
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:")); // Only email apps handle this.
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"destinatario@example.com"});
                                intent.putExtra(Intent.EXTRA_SUBJECT, "RICHIESTA DI RIMOZIONE PER PAZIENTE");
                                startActivity(Intent.createChooser(intent, "Invia email"));
                            })
                            .setNegativeButton("Annulla", (dialog, which) -> {
                                // L'utente ha annullato l'azione
                            })
                            .show();

                    Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(position);
                    return;
                }

                case ItemTouchHelper.RIGHT -> {
                    ListUserAdapter.UserSearch userSearch = (ListUserAdapter.UserSearch) recyclerView.findViewHolderForAdapterPosition(position);
                    TextView emailTV = viewHolder.itemView.findViewById(R.id.emailUser);
                    String email =  emailTV.getText().toString();

                    Intent intent = new Intent(ListUserActivity.this, ModifyPatientProfileActivity.class);
                    intent.putExtra("selected_user_email", email);
                    startActivity(intent);
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(position);
                    finish();
                    return;
                }
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    //delete item
                    .addSwipeLeftLabel(getString(R.string.elimina_item_lista))
                    .setSwipeLeftLabelColor(getResources().getColor(R.color.white))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .setSwipeLeftActionIconTint(getResources().getColor(R.color.white))
                    .addSwipeLeftBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_default_color_error))
                    //edit item
                    .addSwipeRightLabel(getString(R.string.modifica_item_lista))
                    .setSwipeRightLabelColor(getResources().getColor(R.color.white))
                    .addSwipeRightActionIcon(R.drawable.ic_edit)
                    .setSwipeRightActionIconTint(getResources().getColor(R.color.white))
                    .addSwipeRightBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_default_color_secondary_variant))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (userArrayList.isEmpty()) {
            eventChangeListener();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("userArrayList", userArrayList);
    }

    private void filterList(String s) {
        ArrayList<Paziente> filteredList = new ArrayList<>();

        for (Paziente user : userArrayList) {
            if (user.getName().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))
                    || user.getSurname().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) {
                filteredList.add(user);
            }
        }

        listUserAdapter.setFilteredList(filteredList);

        if (filteredList.isEmpty()) {
            Toast.makeText(this, getString(R.string.nousersfound), Toast.LENGTH_SHORT).show();
        } else {
            textViewNoData.setVisibility(View.GONE);
        }
    }

    private void eventChangeListener() {
        dbUtenti.getCollectionInstance()
                .whereEqualTo("type", "user")
                .whereEqualTo("emailDoc", new SessionManagement(this).getUserAuthSession().getEmail())
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        return;
                    }
                    assert value != null;
                    for (QueryDocumentSnapshot doc : value) {
                        userArrayList.add(doc.toObject(Paziente.class));
                    }
                    if (!userArrayList.isEmpty()) {
                        textViewNoData.setVisibility(View.GONE);
                    }
                    listUserAdapter.notifyDataSetChanged();
                });
    }

    private void showLanguageSelectionDialog() {
        // Creazione di un AlertDialog personalizzato
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Ottieni il layout del popup della lingua personalizzato
        View customView = getLayoutInflater().inflate(R.layout.custom_popup_language, null);

        // Imposta la vista personalizzata come vista del dialogo
        builder.setView(customView);

        // Crea e mostra il dialogo
        AlertDialog dialog = builder.create();
        dialog.show();

        // Ottieni i pulsanti della tua vista personalizzata e gestisci il loro clic
        Button btnIta = customView.findViewById(R.id.btnIta);
        Button btnEn = customView.findViewById(R.id.btnEn);
        Button btnFr = customView.findViewById(R.id.btnFr);

        btnIta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("it");
                dialog.dismiss(); // Chiudi il dialogo dopo la selezione
            }
        });

        btnEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
                dialog.dismiss(); // Chiudi il dialogo dopo la selezione
            }
        });

        btnFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("fr");
                dialog.dismiss(); // Chiudi il dialogo dopo la selezione
            }
        });
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        // Salva la lingua selezionata in SharedPreferenze
        saveLocaleToPreferences();

        // Riavvia l'attività per applicare la nuova lingua
        recreate(); // Ricarica l'attività corrente per applicare la nuova lingua
    }

    private void saveLocaleToPreferences() {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", Locale.getDefault().getLanguage());
        editor.apply();
    }
}