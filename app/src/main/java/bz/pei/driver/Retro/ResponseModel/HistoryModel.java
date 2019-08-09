package bz.pei.driver.Retro.ResponseModel;

import com.google.gson.annotations.SerializedName;

public class HistoryModel {

    @SerializedName("car_model")
    public String carModel;

    @SerializedName("driver_id")
    public int driverId;

    @SerializedName("driver_type")
    public int driverType;

    @SerializedName("user_profile_pic")
    public String usrepic;

    @SerializedName("pick_latitude")
    public double pickLatitude;

    @SerializedName("drop_location")
    public String dropLocation;

    @SerializedName("is_completed")
    public int isCompleted;

    @SerializedName("pick_longitude")
    public double pickLongitude;

    @SerializedName("type_icon")
    public String typeIcon;

    @SerializedName("pick_location")
    public String pickLocation;

    @SerializedName("user_id")
    public int userId;

    @SerializedName("car_number")
    public String carNumber;

    @SerializedName("drop_latitude")
    public double dropLatitude;

    @SerializedName("trip_start_time")
    public String tripStartTime;

    @SerializedName("request_id")
    public String requestId;
    @SerializedName("id")
    public String id;

    @SerializedName("drop_longitude")
    public double dropLongitude;

    @SerializedName("is_cancelled")
    public int isCancelled;

    @SerializedName("total")
    public String total;
    @SerializedName("currency")
    public String currency;

    @SerializedName("is_share")
    public int is_share;


}