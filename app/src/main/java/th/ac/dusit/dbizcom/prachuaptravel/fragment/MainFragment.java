package th.ac.dusit.dbizcom.prachuaptravel.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import th.ac.dusit.dbizcom.prachuaptravel.R;
import th.ac.dusit.dbizcom.prachuaptravel.adapter.SpinnerWithHintArrayAdapter;

public class MainFragment extends Fragment {

    private MainFragmentListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner placeSpinner = view.findViewById(R.id.place_spinner);

        String[] placeList = getResources().getStringArray(R.array.place_names);
        SpinnerWithHintArrayAdapter adapter = new SpinnerWithHintArrayAdapter<>(
                getContext(),
                R.layout.item_place,
                placeList
        );
        adapter.setDropDownViewResource(R.layout.item_place);
        placeSpinner.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainFragmentListener) {
            mListener = (MainFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MainFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface MainFragmentListener {

    }
}
