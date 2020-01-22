package th.ac.dusit.dbizcom.prachuaptravel;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import th.ac.dusit.dbizcom.prachuaptravel.fragment.MainFragment;
import th.ac.dusit.dbizcom.prachuaptravel.fragment.MapsFragment;
import th.ac.dusit.dbizcom.prachuaptravel.fragment.NearbyFragment;
import th.ac.dusit.dbizcom.prachuaptravel.fragment.PlaceFragment;
import th.ac.dusit.dbizcom.prachuaptravel.model.Nearby;

public class MainActivity extends AppCompatActivity implements
        MainFragment.MainFragmentListener,
        PlaceFragment.PlaceFragmentListener,
        NearbyFragment.NearbyFragmentListener,
        MapsFragment.MapsFragmentListener {

    public static final String TAG_FRAGMENT_MAIN = "main_fragment";

    @Override
    public void onClickNearbyItem(Nearby nearby) {

    }

    protected enum FragmentTransitionType {
        NONE,
        SLIDE,
        FADE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        MainFragment fragment = new MainFragment();
        loadFragment(fragment, TAG_FRAGMENT_MAIN, false, FragmentTransitionType.NONE);
    }

    protected void loadFragment(Fragment fragment, String tag, boolean addToBackStack,
                                FragmentTransitionType transitionType) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (transitionType == FragmentTransitionType.SLIDE) {
            transaction.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
            );
        } else if (transitionType == FragmentTransitionType.FADE) {
            transaction.setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
            );
        }
        transaction.replace(
                R.id.fragment_container,
                fragment,
                tag
        );
        if (addToBackStack) {
            transaction.addToBackStack(null).commit();
        } else {
            transaction.commit();
        }
    }

    protected void popAllBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    protected void popBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }
}
