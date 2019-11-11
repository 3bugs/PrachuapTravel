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

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.google.gson.Gson;

import java.util.ArrayList;

import th.ac.dusit.dbizcom.prachuaptravel.R;
import th.ac.dusit.dbizcom.prachuaptravel.model.Place;

import static th.ac.dusit.dbizcom.prachuaptravel.net.ApiClient.IMAGES_BASE_URL;

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

        setupImageSlider(view);
    }

    private void setupImageSlider(View view) {
        SliderLayout mSlider = view.findViewById(R.id.slider);

        ArrayList<String> listUrl = new ArrayList<>();
        for (String fileName : mPlace.imageList) {
            listUrl.add(IMAGES_BASE_URL + fileName);
        }
        /*listUrl.add("http://5911011802058.msci.dusit.ac.th/chainat_tourism/images/โฆษณา.png");
        listUrl.add("http://5911011802058.msci.dusit.ac.th/chainat_tourism/images/สวนนก.png");
        listUrl.add("http://5911011802058.msci.dusit.ac.th/chainat_tourism/images/สวนส้มโอ.png");*/

        RequestOptions requestOptions = new RequestOptions().centerCrop();

        //.diskCacheStrategy(DiskCacheStrategy.NONE)
        //.placeholder(R.drawable.placeholder)
        //.error(R.drawable.placeholder);

        for (int i = 0; i < listUrl.size(); i++) {
            DefaultSliderView sliderView = new DefaultSliderView(getActivity());
            sliderView
                    .image(listUrl.get(i))
                    .setRequestOption(requestOptions)
                    //.setBackgroundColor(Color.WHITE)
                    .setProgressBarVisible(true)
                    .setOnSliderClickListener(null);

            //add your extra information
            sliderView.bundle(new Bundle());
            //sliderView.getBundle().putString("extra", listName.get(i));
            mSlider.addSlider(sliderView);
        }

        // set Slider Transition Animation
        // mSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mSlider.setPresetTransformer(SliderLayout.Transformer.Default);

        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(3000);
        mSlider.addOnPageChangeListener(null);
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
