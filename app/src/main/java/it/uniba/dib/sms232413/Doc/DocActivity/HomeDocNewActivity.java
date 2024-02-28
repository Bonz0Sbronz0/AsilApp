package it.uniba.dib.sms232413.Doc.DocActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

import it.uniba.dib.sms232413.R;

public class HomeDocNewActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationViewDoc;
    ImageButton imageButtonUserList, imageButtonAddUser;
    TextView add_user_textview, list_user_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_doc_new);

        bottomNavigationViewDoc = findViewById(R.id.bottomNavViewDoc);
        imageButtonUserList = findViewById(R.id.imageButtonUserList);
        imageButtonAddUser = findViewById(R.id.imageButtonAddUser);
        add_user_textview = findViewById(R.id.add_user_textview);
        list_user_textview = findViewById(R.id.list_user_textview);

        bottomNavigationViewDoc.setSelectedItemId(R.id.miHome);

        bottomNavigationViewDoc.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.miHome:
                    return true;
                case R.id.miLanguage:
                    showLanguageSelectionDialog();
                    return true;
                case R.id.miProfile:
                    startActivity(new Intent(getApplicationContext(), DocProfileActivity.class));
                    overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_in_left_activity);
                    finish();
                    return true;
                case R.id.miLogout:
                    // TODO: Handle logout
                    return true;
            }
            return false;
        });

        imageButtonUserList.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ListUserActivity.class);
            startActivity(intent);
        });

        imageButtonAddUser.setOnClickListener(v -> {
            // TODO aprire fragment adduser
            Toast.makeText(getApplicationContext(), "Add User Button Clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void showLanguageSelectionDialog() {
        String[] languages = {"English", "Italiano", "Français"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Select Language");
        builder.setItems(languages, (dialog, which) -> {
            String languageCode = "";
            switch (which) {
                case 0:
                    languageCode = "en";
                    break;
                case 1:
                    languageCode = "it";
                    break;
                case 2:
                    languageCode = "fr";
                    break;
            }
            setLocale(languageCode);
        });
        builder.show();
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        recreate();
    }
}
