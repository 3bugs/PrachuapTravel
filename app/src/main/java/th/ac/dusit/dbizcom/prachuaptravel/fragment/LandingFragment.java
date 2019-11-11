package th.ac.dusit.dbizcom.prachuaptravel.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import th.ac.dusit.dbizcom.prachuaptravel.R;

public class LandingFragment extends Fragment {

    public LandingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_landing, container, false);
    }
}
