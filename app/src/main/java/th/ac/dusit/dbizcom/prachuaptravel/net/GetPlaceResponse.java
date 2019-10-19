package th.ac.dusit.dbizcom.prachuaptravel.net;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import th.ac.dusit.dbizcom.prachuaptravel.model.Place;

public class GetPlaceResponse extends BaseResponse {

    @SerializedName("data_list")
    public List<Place> placeList;

}