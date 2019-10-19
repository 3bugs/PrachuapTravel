package th.ac.dusit.dbizcom.prachuaptravel.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.prachuaptravel.R;
import th.ac.dusit.dbizcom.prachuaptravel.adapter.SpinnerWithHintArrayAdapter;
import th.ac.dusit.dbizcom.prachuaptravel.etc.Utils;
import th.ac.dusit.dbizcom.prachuaptravel.model.Place;
import th.ac.dusit.dbizcom.prachuaptravel.net.ApiClient;
import th.ac.dusit.dbizcom.prachuaptravel.net.GetPlaceResponse;
import th.ac.dusit.dbizcom.prachuaptravel.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.prachuaptravel.net.WebServices;

public class MainFragment extends Fragment {

    private List<Place> mPlaceList = null;

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

        if (mPlaceList == null) {
            doGetPlace();
        } else {
            setupSpinner(view);
        }
    }

    private void doGetPlace() {
        //mProgressView.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<GetPlaceResponse> call = services.getPlace();
        call.enqueue(new MyRetrofitCallback<>(
                getActivity(),
                null,
                null,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetPlaceResponse>() {
                    @Override
                    public void onSuccess(GetPlaceResponse responseBody) {
                        mPlaceList = responseBody.placeList;

                        if (getView() != null) {
                            setupSpinner(getView());
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if (getActivity() != null) {
                            Utils.showOkDialog(getActivity(), "Error", errorMessage, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                        }
                    }
                }
        ));
    }

    private void setupSpinner(@NonNull View view) {
        Spinner placeSpinner = view.findViewById(R.id.place_spinner);

        //String[] placeList = getResources().getStringArray(R.array.place_names);
        SpinnerWithHintArrayAdapter<Place> adapter = new SpinnerWithHintArrayAdapter<>(
                getContext(),
                R.layout.item_spinner_place,
                mPlaceList
        );
        adapter.setDropDownViewResource(R.layout.item_spinner_drop_down_place);
        placeSpinner.setAdapter(adapter);
        placeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getChildFragmentManager().beginTransaction().replace(
                        R.id.place_fragment_container,
                        PlaceFragment.newInstance(mPlaceList.get(position))
                ).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
