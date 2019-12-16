package th.ac.dusit.dbizcom.prachuaptravel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import th.ac.dusit.dbizcom.prachuaptravel.model.Place;

import static th.ac.dusit.dbizcom.prachuaptravel.PlaceActivity.KEY_PLACE_JSON;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    static final String KEY_PLACE_LIST_JSON = "place_list_json";

    private List<Place> mPlaceList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        String placeListJson = intent.getStringExtra(KEY_PLACE_LIST_JSON);

        if (placeListJson != null) {
            mPlaceList = new Gson().fromJson(placeListJson, new TypeToken<List<Place>>() {
            }.getType());

            Button[] placeButtonList = new Button[]{
                    findViewById(R.id.place_01_button),
                    findViewById(R.id.place_02_button),
                    findViewById(R.id.place_03_button),
                    findViewById(R.id.place_04_button),
                    findViewById(R.id.place_05_button)
            };

            for (int i = 0; i < mPlaceList.size(); i++) {
                if (i < placeButtonList.length) {
                    placeButtonList[i].setText(mPlaceList.get(i).name);
                    placeButtonList[i].setTag(i);
                    placeButtonList[i].setOnClickListener(this);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        Place place = mPlaceList.get((int) v.getTag());

        Intent intent = new Intent(MenuActivity.this, PlaceActivity.class);
        intent.putExtra(KEY_PLACE_JSON, new Gson().toJson(place));
        startActivity(intent);
    }
}
