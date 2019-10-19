package th.ac.dusit.dbizcom.prachuaptravel.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Place {

    @SerializedName("id")
    public final int id;
    @SerializedName("name")
    public final String name;
    @SerializedName("details")
    public final String details;
    @SerializedName("address")
    public final String address;
    @SerializedName("phone")
    public final String phone;
    @SerializedName("latitude")
    public final double latitude;
    @SerializedName("longitude")
    public final double longitude;
    @SerializedName("image_list")
    public final List<String> imageList;

    public Place(int id, String name, String details, String address, String phone,
                 double latitude, double longitude, List<String> imageList) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
