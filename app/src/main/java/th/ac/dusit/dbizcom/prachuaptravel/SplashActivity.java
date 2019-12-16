package th.ac.dusit.dbizcom.prachuaptravel;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.prachuaptravel.etc.Utils;
import th.ac.dusit.dbizcom.prachuaptravel.model.Place;
import th.ac.dusit.dbizcom.prachuaptravel.net.ApiClient;
import th.ac.dusit.dbizcom.prachuaptravel.net.GetPlaceResponse;
import th.ac.dusit.dbizcom.prachuaptravel.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.prachuaptravel.net.WebServices;

import static th.ac.dusit.dbizcom.prachuaptravel.MenuActivity.KEY_PLACE_LIST_JSON;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        doGetPlace();
    }

    private void doGetPlace() {
        //mProgressView.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<GetPlaceResponse> call = services.getPlace();
        call.enqueue(new MyRetrofitCallback<>(
                SplashActivity.this,
                null,
                null,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetPlaceResponse>() {
                    @Override
                    public void onSuccess(final GetPlaceResponse responseBody) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                List<Place> placeList = responseBody.placeList;

                                Intent intent = new Intent(SplashActivity.this, MenuActivity.class);
                                intent.putExtra(KEY_PLACE_LIST_JSON, new Gson().toJson(placeList));
                                startActivity(intent);
                                finish();
                            }
                        }, 1000);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Utils.showOkDialog(SplashActivity.this, "Error", errorMessage, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                    }
                }
        ));
    }
}
