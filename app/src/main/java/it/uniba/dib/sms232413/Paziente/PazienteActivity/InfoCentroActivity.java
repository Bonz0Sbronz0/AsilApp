package it.uniba.dib.sms232413.Paziente.PazienteActivity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.Paziente.PazienteAdapter.InfoFragmentAdapter;
import it.uniba.dib.sms232413.Shared.SessionManagement;

public class InfoCentroActivity extends AppCompatActivity {
    private static SessionManagement sessionManagement;
    public static String CENTRO_CORRENTE;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    InfoFragmentAdapter infoFragmentAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_info);

        sessionManagement = new SessionManagement(this);
        CENTRO_CORRENTE = sessionManagement.getUserPatientSession().getReceptionceter();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.pager);

        //utilizzo il SupportFragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        infoFragmentAdapter = new InfoFragmentAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(infoFragmentAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("Chi siamo"));
        tabLayout.addTab(tabLayout.newTab().setText("Dove siamo"));
        tabLayout.addTab(tabLayout.newTab().setText("Regole e Servizi"));

        //questo metodo gestisce il cambio di view
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }

    private static String extractReceptioncenterId(){
        assert sessionManagement != null;
        return sessionManagement.getUserPatientSession().getReceptionceter();
    }
}
