package th.ac.dusit.dbizcom.prachuaptravel.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import th.ac.dusit.dbizcom.prachuaptravel.R;
import th.ac.dusit.dbizcom.prachuaptravel.model.Place;

public class PlaceFragment extends Fragment {

    private static final String ARG_PLACE_JSON = "place_json";

    private Place mPlace;

    private PlaceFragmentListener mListener;

    public PlaceFragment() {
        // Required empty public constructor
    }

    public static PlaceFragment newInstance(Place place) {
        PlaceFragment fragment = new PlaceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLACE_JSON, new Gson().toJson(place));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String placeJson = getArguments().getString(ARG_PLACE_JSON);
            mPlace = new Gson().fromJson(placeJson, Place.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_place, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView placeNameTextView = view.findViewById(R.id.place_name_text_view);
        placeNameTextView.setText(mPlace.name);

        TextView placeDetailsTextView = view.findViewById(R.id.details_text_view);
        placeDetailsTextView.setText(mPlace.details);

        TextView addressTextView = view.findViewById(R.id.address_text_view);
        addressTextView.setText(mPlace.address);

        TextView phoneTextView = view.findViewById(R.id.phone_text_view);
        phoneTextView.setText(mPlace.phone);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlaceFragmentListener) {
            mListener = (PlaceFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PlaceFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface PlaceFragmentListener {
    }
}
