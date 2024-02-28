package it.uniba.dib.sms232413.Paziente.PazienteAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.uniba.dib.sms232413.Paziente.PazienteFragment.InfoCentroFragment.ServiceRuleFragment;
import it.uniba.dib.sms232413.Paziente.PazienteFragment.InfoCentroFragment.WhereFragment;
import it.uniba.dib.sms232413.Paziente.PazienteFragment.InfoCentroFragment.WhoFragment;

public class InfoFragmentAdapter extends FragmentStateAdapter {
    public InfoFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            return new WhoFragment();
        } else if (position == 1) {
            return new WhereFragment();
        }else{
            return new ServiceRuleFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
