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
    @SerializedName("activity_details")
    public final String activityDetails;
    @SerializedName("activity_image")
    public final String activityImage;
    @SerializedName("address")
    public final String address;
    @SerializedName("phone")
    public final String phone;
    @SerializedName("opening_time")
    public final String openingTime;
    @SerializedName("latitude")
    public final double latitude;
    @SerializedName("longitude")
    public final double longitude;
    @SerializedName("image_list")
    public final List<String> imageList;
    @SerializedName("nearby_list")
    public final List<Nearby> nearbyList;

    public Place(int id, String name, String details, String activityDetails, String activityImage,
                 String address, String phone, String openingTime,
                 double latitude, double longitude, List<String> imageList, List<Nearby> nearbyList) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.activityDetails = activityDetails;
        this.activityImage = activityImage;
        this.address = address;
        this.phone = phone;
        this.openingTime = openingTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageList = imageList;
        this.nearbyList = nearbyList;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}