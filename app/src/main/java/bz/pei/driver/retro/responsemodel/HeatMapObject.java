package bz.pei.driver.retro.responsemodel;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class HeatMapObject {

    @SerializedName("pick_latitude")
    public String pick_latitude;

    @SerializedName("pick_longitude")
    public String pick_longitude;

    @SerializedName("distance")
    public String distance;

    @SerializedName("id")
    public String id;
    public LatLng getLatLng() {
        if (TextUtils.isEmpty(pick_latitude) || TextUtils.isEmpty(pick_longitude))
            return null;
        else
            return new LatLng(Double.parseDouble(pick_latitude), Double.parseDouble(pick_longitude));
    }
}
