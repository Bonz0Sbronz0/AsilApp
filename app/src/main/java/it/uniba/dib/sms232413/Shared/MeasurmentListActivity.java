package it.uniba.dib.sms232413.Shared;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import it.uniba.dib.sms232413.R;


public class MeasurmentListActivity extends AppCompatActivity {
    private static final String TAG = "MeasurmentList";
    private DatabaseReference mUserReference;

    private String uId;
    private String userClickedId;
    private String parameter;
    private String activityCaller;

    private DatabaseReference mMedicalRecordReference;
    private ImageButton imgBtnLanguage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurment_list);


        //Intent used to get the name of the source activity for this target activity
        Intent intent = getIntent();
        //Get the name of the source activity
        activityCaller = intent.getStringExtra("class_name");




        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);

        if (getIntent().getExtras() != null) {
            userClickedId = getIntent().getExtras().getString("user_clicked");
            parameter = getIntent().getExtras().getString("_parameter");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = getIntent().getExtras();
        String userClickedId = extras.getString("user_clicked");
        String parameter = extras.getString("_parameter");
        Intent refresh = new Intent(this, MeasurmentListActivity.class);
        refresh.putExtra("user_clicked", userClickedId);
        refresh.putExtra("_parameter", parameter);
        startActivity(refresh);
        this.finish();

    }


    @Override
    protected void onStart() {
        super.onStart();

        // Initialize FirebaseUser
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Initialize variable to get extra value from other intent
        if (getIntent().getExtras() != null) {
            userClickedId = getIntent().getExtras().getString("user_clicked");
            parameter = getIntent().getExtras().getString("_parameter");
        }

        //Condition
        if (userClickedId != null) {
            uId = userClickedId;
        } else {
            // Get userId Logged
            uId = user.getUid();
        }
    }


}