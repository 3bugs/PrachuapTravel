package th.ac.dusit.dbizcom.prachuaptravel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import th.ac.dusit.dbizcom.prachuaptravel.fragment.MapsFragment;
import th.ac.dusit.dbizcom.prachuaptravel.fragment.NearbyFragment;
import th.ac.dusit.dbizcom.prachuaptravel.fragment.PlaceFragment;
import th.ac.dusit.dbizcom.prachuaptravel.fragment.PlacePagerFragment;
import th.ac.dusit.dbizcom.prachuaptravel.model.Place;

public class PlaceActivity extends AppCompatActivity implements
        PlaceFragment.PlaceFragmentListener,
        NearbyFragment.NearbyFragmentListener,
        MapsFragment.MapsFragmentListener {

    static final String KEY_PLACE_JSON = "place_json";

    private Place mPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        Intent intent = getIntent();
        String placeJson = intent.getStringExtra(KEY_PLACE_JSON);

        if (placeJson != null) {
            mPlace = new Gson().fromJson(placeJson, Place.class);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                getSupportActionBar().setTitle(mPlace.name);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment_container,
                    PlacePagerFragment.newInstance(mPlace)
            ).commit();
        }
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
}
