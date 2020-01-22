package th.ac.dusit.dbizcom.prachuaptravel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.google.gson.Gson;

import java.util.ArrayList;

import th.ac.dusit.dbizcom.prachuaptravel.model.Nearby;

import static th.ac.dusit.dbizcom.prachuaptravel.net.ApiClient.IMAGES_BASE_URL;

public class NearbyDetailsActivity extends AppCompatActivity {

    public static final String KEY_NEARBY_JSON = "nearby_json";

    private Nearby mNearby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_details);

        Intent intent = getIntent();
        String nearbyJson = intent.getStringExtra(KEY_NEARBY_JSON);
        mNearby = new Gson().fromJson(nearbyJson, Nearby.class);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle(mNearby.name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView placeDetailsTextView = findViewById(R.id.details_text_view);
        placeDetailsTextView.setText(mNearby.details);

        TextView addressTextView = findViewById(R.id.address_text_view);
        addressTextView.setText(mNearby.address);

        TextView phoneTextView = findViewById(R.id.phone_text_view);
        phoneTextView.setText(mNearby.phone);

        ImageView openingTimeImageView = findViewById(R.id.opening_time_image_view);
        TextView openingTimeLabelTextView = findViewById(R.id.opening_time_label_text_view);
        TextView openingTimeTextView = findViewById(R.id.opening_time_text_view);

        if ("hotel".equalsIgnoreCase(mNearby.type)) {
            openingTimeImageView.setVisibility(View.GONE);
            openingTimeLabelTextView.setVisibility(View.GONE);
            openingTimeTextView.setVisibility(View.GONE);
        } else {
            openingTimeTextView.setText(mNearby.openingTime);
        }

        setupImageSlider();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupImageSlider() {
        SliderLayout mSlider = findViewById(R.id.slider);

        ArrayList<String> listUrl = new ArrayList<>();
        for (String fileName : mNearby.imageList) {
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
            DefaultSliderView sliderView = new DefaultSliderView(this);
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
}
