package th.ac.dusit.dbizcom.prachuaptravel.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import th.ac.dusit.dbizcom.prachuaptravel.R;
import th.ac.dusit.dbizcom.prachuaptravel.model.Place;

public class PlacePagerFragment extends Fragment {

    private static final String ARG_PLACE_JSON = "place_json";

    private Place mPlace;

    //private NearbyFragmentListener mListener;

    public PlacePagerFragment() {
        // Required empty public constructor
    }

    public static PlacePagerFragment newInstance(Place place) {
        PlacePagerFragment fragment = new PlacePagerFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PlacePagerAdapter adapter = new PlacePagerAdapter(
                getChildFragmentManager(),
                mPlace
        );
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_place_pager, container, false);
    }

    private static class PlacePagerAdapter extends FragmentStatePagerAdapter {

        private Place mPlace;

        public PlacePagerAdapter(@NonNull FragmentManager fm, Place place) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.mPlace = place;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PlaceFragment.newInstance(mPlace);
                case 1:
                    return NearbyFragment.newInstance(mPlace);
                case 2:
                    return NearbyFragment.newInstance(mPlace);
                case 3:
                    return MapsFragment.newInstance(mPlace);
                default:
                    return PlaceFragment.newInstance(mPlace);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            switch (position) {
                case 0:
                    title = "รายละเอียด";
                    break;
                case 1:
                    title = "ร้านอาหารใกล้เคียง";
                    break;
                case 2:
                    title = "ที่พักใกล้เคียง";
                    break;
                case 3:
                    title = "Google Maps";
                    break;
            }
            return title;
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NearbyFragmentListener) {
            mListener = (NearbyFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NearbyFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface NearbyFragmentListener {
    }*/
}
