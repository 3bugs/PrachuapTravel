package th.ac.dusit.dbizcom.prachuaptravel.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Nearby {

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
    @SerializedName("opening_time")
    public final String openingTime;
    @SerializedName("cover_image")
    public final String coverImage;
    @SerializedName("type")
    public final String type;
    @SerializedName("image_list")
    public final List<String> imageList;

    public Nearby(int id, String name, String details, String address, String phone,
                 String openingTime, String coverImage, String type, List<String> imageList) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.address = address;
        this.phone = phone;
        this.openingTime = openingTime;
        this.coverImage = coverImage;
        this.type = type;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
