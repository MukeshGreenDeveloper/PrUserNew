package bz.pei.driver.Retro.ResponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Share_request implements Serializable {

    @SerializedName("inprogress")
    @Expose
    public Boolean inprogress;
    @SerializedName("trip")
    @Expose
    public Boolean trip;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("request_id")
    @Expose
    public String request_id;
    @SerializedName("is_share")
    @Expose
    public String is_share;
    @SerializedName("trip_start_time")
    @Expose
    public String trip_start_time;
    @SerializedName("is_driver_started")
    @Expose
    public String is_driver_started;
    @SerializedName("is_driver_arrived")
    @Expose
    public Integer is_driver_arrived;
    @SerializedName("is_trip_start")
    @Expose
    public Integer is_trip_start;
    @SerializedName("is_completed")
    @Expose
    public Integer is_completed;
    @SerializedName("is_cancelled")
    @Expose
    public String is_cancelled;
    @SerializedName("payment_opt")
    @Expose
    public String payment_opt;
    @SerializedName("is_paid")
    @Expose
    public String is_paid;
    @SerializedName("driver_rated")
    @Expose
    public String driver_rated;
    @SerializedName("pick_latitude")
    @Expose
    public Double pick_latitude;
    @SerializedName("pick_longitude")
    @Expose
    public Double pick_longitude;
    @SerializedName("drop_latitude")
    @Expose
    public Double drop_latitude;
    @SerializedName("drop_longitude")
    @Expose
    public Double drop_longitude;
    @SerializedName("pick_location")
    @Expose
    public String pick_location;
    @SerializedName("drop_location")
    @Expose
    public String drop_location;
    @SerializedName("time")
    @Expose
    public String time;
    @SerializedName("distance")
    @Expose
    public String distance;
    @SerializedName("bill")
    @Expose
    public Bill bill;
    @SerializedName("user")
    @Expose
    public User user;


    public class User {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("firstname")
        @Expose
        public String firstname;
        @SerializedName("lastname")
        @Expose
        public String lastname;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("phone_number")
        @Expose
        public String phone_number;
        @SerializedName("profile_pic")
        @Expose
        public String profile_pic;
        @SerializedName("review")
        @Expose
        public String review;
    }

    public class Driver_status {

        @SerializedName("is_active")
        @Expose
        public String is_active;
        @SerializedName("is_approve")
        @Expose
        public String is_approve;
        @SerializedName("is_available")
        @Expose
        public String is_available;
    }

    public class Bill {

        @SerializedName("base_price")
        @Expose
        public String base_price;
        @SerializedName("base_distance")
        @Expose
        public String base_distance;
        @SerializedName("price_per_distance")
        @Expose
        public String price_per_distance;
        @SerializedName("price_per_time")
        @Expose
        public String price_per_time;
        @SerializedName("distance_price")
        @Expose
        public String distance_price;
        @SerializedName("time_price")
        @Expose
        public String time_price;
        @SerializedName("waiting_price")
        @Expose
        public String waiting_price;
        @SerializedName("tollgate_price")
        @Expose
        public String tollgate_price;
        @SerializedName("service_tax")
        @Expose
        public String service_tax;
        @SerializedName("service_tax_percentage")
        @Expose
        public String service_tax_percentage;
        @SerializedName("promo_amount")
        @Expose
        public String promo_amount;
        @SerializedName("referral_amount")
        @Expose
        public String referral_amount;
        @SerializedName("service_fee")
        @Expose
        public String service_fee;
        @SerializedName("cancellation_fee")
        @Expose
        public String cancellation_fee;
        @SerializedName("driver_amount")
        @Expose
        public String driver_amount;
        @SerializedName("wallet_amount")
        @Expose
        public String wallet_amount;
        @SerializedName("total")
        @Expose
        public String total;
        @SerializedName("show_bill")
        @Expose
        public String show_bill;
        @SerializedName("currency")
        @Expose
        public String currency;
        @SerializedName("unit")
        @Expose
        public String unit;
        @SerializedName("unit_in_words_without_lang")
        @Expose
        public String unit_in_words_without_lang;
        @SerializedName("unit_in_words")
        @Expose
        public String unit_in_words;
    }

}
