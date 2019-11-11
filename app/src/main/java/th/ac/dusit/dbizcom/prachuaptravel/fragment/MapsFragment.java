package th.ac.dusit.dbizcom.prachuaptravel.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.Locale;

import th.ac.dusit.dbizcom.prachuaptravel.R;
import th.ac.dusit.dbizcom.prachuaptravel.model.Place;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PLACE_JSON = "place_json";

    private GoogleMap mMap;
    private Place mPlace;

    private MapsFragmentListener mListener;

    public MapsFragment() {
        // Required empty public constructor
    }

    public static MapsFragment newInstance(Place place) {
        MapsFragment fragment = new MapsFragment();
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
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        TextView placeNameTextView = view.findViewById(R.id.place_name_text_view);
        placeNameTextView.setText(mPlace.name);

        TextView addressTextView = view.findViewById(R.id.address_text_view);
        addressTextView.setText(mPlace.address);

        Button directionButton = view.findViewById(R.id.direction_button);
        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Uri intentUri = Uri.parse("geo:" + mPlace.latitude + "," + mPlace.longitude + "?q=" + mPlace.name);
                String url = String.format(
                        Locale.getDefault(),
                        "http://maps.google.com/maps?daddr=%f,%f",
                        mPlace.latitude, mPlace.longitude
                );
                Uri intentUri = Uri.parse(url);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latLng = new LatLng(mPlace.latitude, mPlace.longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title(mPlace.name));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapsFragmentListener) {
            mListener = (MapsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MapsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface MapsFragmentListener {
    }
}
