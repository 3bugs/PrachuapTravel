package th.ac.dusit.dbizcom.prachuaptravel.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import th.ac.dusit.dbizcom.prachuaptravel.R;
import th.ac.dusit.dbizcom.prachuaptravel.model.Nearby;
import th.ac.dusit.dbizcom.prachuaptravel.model.Place;
import th.ac.dusit.dbizcom.prachuaptravel.net.ApiClient;

public class NearbyFragment extends Fragment {

    public static final String ARG_PLACE_JSON = "place_json";
    public static final String ARG_NEARBY_TYPE = "nearby_type";

    private Place mPlace;
    private String mNearbyType;
    private List<Nearby> mNearbyList;

    private RecyclerView mRecyclerView;
    private TextView mEmptyTextView;

    private NearbyFragmentListener mListener;

    public NearbyFragment() {
        // Required empty public constructor
    }

    static NearbyFragment newInstance(Place place, String nearbyType) {
        NearbyFragment fragment = new NearbyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLACE_JSON, new Gson().toJson(place));
        args.putString(ARG_NEARBY_TYPE, nearbyType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String placeJson = getArguments().getString(ARG_PLACE_JSON);
            mPlace = new Gson().fromJson(placeJson, Place.class);

            mNearbyType = getArguments().getString(ARG_NEARBY_TYPE);

            mNearbyList = new ArrayList<>();
            for (Nearby nearby : mPlace.nearbyList) {
                if (nearby.type.equalsIgnoreCase(mNearbyType)) {
                    mNearbyList.add(nearby);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nearby, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.nearby_recycler_view);
        mEmptyTextView = view.findViewById(R.id.empty_text_view);

        if (mNearbyList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyTextView.setVisibility(View.GONE);
            setupRecyclerView();
        }
    }

    private void setupRecyclerView() {
        if (getContext() != null) {
            NearbyListAdapter adapter = new NearbyListAdapter(
                    getContext(),
                    mNearbyList,
                    mListener
            );
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.addItemDecoration(new SpacingDecoration(getContext()));
            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
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
        void onClickNearbyItem(Nearby nearby);
    }

    private static class NearbyListAdapter extends RecyclerView.Adapter<NearbyListAdapter.ViewHolder> {

        private final Context mContext;
        private final List<Nearby> mNearbyList;
        private final NearbyFragmentListener mListener;

        NearbyListAdapter(Context context, List<Nearby> nearbyList, NearbyFragmentListener listener) {
            mContext = context;
            mNearbyList = nearbyList;
            mListener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_nearby_2, parent, false
            );
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Nearby nearby = mNearbyList.get(position);

            holder.mNearby = nearby;

            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();

            Glide.with(mContext)
                    .load(ApiClient.IMAGES_BASE_URL.concat(nearby.coverImage))
                    .placeholder(circularProgressDrawable)
                    .into(holder.mImageView);

            holder.mNameTextView.setText(nearby.name);
        }

        @Override
        public int getItemCount() {
            return mNearbyList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final View mRootView;
            private final ImageView mImageView;
            private final TextView mNameTextView;
            private Nearby mNearby;

            ViewHolder(View itemView) {
                super(itemView);

                mRootView = itemView;
                mImageView = itemView.findViewById(R.id.image_view);
                mNameTextView = itemView.findViewById(R.id.name_text_view);

                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mListener != null) {
                            mListener.onClickNearbyItem(mNearby);
                        }
                    }
                });
            }
        }
    }

    public class SpacingDecoration extends RecyclerView.ItemDecoration {

        private final static int MARGIN_TOP_IN_DP = 0;
        private final static int MARGIN_BOTTOM_IN_DP = 16;
        private final int mMarginTop, mMarginBottom;

        SpacingDecoration(@NonNull Context context) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            mMarginTop = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    MARGIN_TOP_IN_DP,
                    metrics
            );
            mMarginBottom = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    MARGIN_BOTTOM_IN_DP,
                    metrics
            );
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            final int itemPosition = parent.getChildAdapterPosition(view);
            if (itemPosition == RecyclerView.NO_POSITION) {
                return;
            }
            if (itemPosition == 0) {
                outRect.top = mMarginTop;
                //outRect.top = 0;
            }
            final RecyclerView.Adapter adapter = parent.getAdapter();
            if ((adapter != null) && (itemPosition == adapter.getItemCount() - 1)) {
                outRect.bottom = mMarginBottom;
            }
        }
    }
}
