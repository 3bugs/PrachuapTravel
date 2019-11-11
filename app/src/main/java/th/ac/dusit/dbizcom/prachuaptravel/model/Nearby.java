package th.ac.dusit.dbizcom.prachuaptravel.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

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
    @SerializedName("cover_image")
    public final String coverImage;
    @SerializedName("type")
    public final String type;

    public Nearby(int id, String name, String details, String address, String phone,
                 String coverImage, String type) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.address = address;
        this.phone = phone;
        this.coverImage = coverImage;
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
