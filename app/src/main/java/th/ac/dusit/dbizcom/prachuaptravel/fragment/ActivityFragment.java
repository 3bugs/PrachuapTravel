package th.ac.dusit.dbizcom.prachuaptravel.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import th.ac.dusit.dbizcom.prachuaptravel.R;
import th.ac.dusit.dbizcom.prachuaptravel.model.Place;

import static th.ac.dusit.dbizcom.prachuaptravel.net.ApiClient.IMAGES_BASE_URL;

public class ActivityFragment extends Fragment {

    private static final String ARG_PLACE_JSON = "place_json";

    private Place mPlace;

    private ActivityFragmentListener mListener;

    public ActivityFragment() {
        // Required empty public constructor
    }

    public static ActivityFragment newInstance(Place place) {
        ActivityFragment fragment = new ActivityFragment();
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
        return inflater.inflate(R.layout.fragment_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView activityTextView = view.findViewById(R.id.activity_details_text_view);
        activityTextView.setText(mPlace.activityDetails);

        if (getActivity() != null) {
            if (mPlace.activityImage != null && mPlace.activityImage.trim().length() > 0) {
                ImageView activityImageView = view.findViewById(R.id.activity_image_view);
                Glide.with(getActivity())
                        .load(IMAGES_BASE_URL + mPlace.activityImage)
                        .into(activityImageView);
            } else {
                //todo:
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActivityFragmentListener) {
            mListener = (ActivityFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ActivityFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ActivityFragmentListener {
    }
}
