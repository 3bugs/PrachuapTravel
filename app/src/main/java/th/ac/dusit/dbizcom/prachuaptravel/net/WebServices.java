package th.ac.dusit.dbizcom.prachuaptravel.net;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WebServices {

    @GET("get_place")
    Call<GetPlaceResponse> getPlace(
    );

    /*@FormUrlEncoded
    @POST("add_rating")
    Call<AddRatingResponse> addRating(
            @Field("id") int itemId,
            @Field("type") String itemType,
            @Field("rate") int rate
    );*/

}