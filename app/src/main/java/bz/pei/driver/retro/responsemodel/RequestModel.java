package bz.pei.driver.retro.responsemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import bz.pei.driver.retro.base.BaseResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 12/16/17.
 */


public class RequestModel extends BaseResponse implements Serializable, Parcelable {

    public List<Request> share_request = null;
    @Expose
    public Request request;
    @Expose
    public Request current_request;
    @Expose
    public List<Documents> documents;
    @SerializedName("heatmap")
    public List<HeatMapObject> heatmap_list;

    @Expose
    public DriverModel driver;

    /*RequestIn Progress*/
    @Expose
    public DriverModel driver_status;
    @SerializedName("sos")
    public List<SosModel> sosModelList;
    @Expose
    public List<AreaModel> admins;
    /*RequestIn Progress*/
    @Expose
    public Boolean is_approved;
    /*Received in Push*/
    @Expose
    public int is_cancelled;
    /*Received in Push*/

    public class Request implements Serializable, Parcelable{
        @Expose
        public boolean inprogress;
        @Expose
        public boolean trip;
        @Expose
        public User user;

        @SerializedName("pick_longitude")
        public double pickLongitude;

        @SerializedName("pick_location")
        public String pickLocation;

        @SerializedName("pick_latitude")
        public double pickLatitude;

        @SerializedName("drop_latitude")
        public double dropLatitude;

        @SerializedName("id")
        public int id;
        @SerializedName("is_share")
        public int is_share;

        @SerializedName("drop_location")
        public String dropLocation;

        @SerializedName("request_id")
        public String requestId;
        @Expose
        public String time_left;

        @SerializedName("drop_longitude")
        public double dropLongitude;

        @Expose
        public String trip_start_time;


        @Expose
        public int is_driver_started;
        @Expose
        public int is_driver_arrived;
        @Expose
        public int is_trip_start;
        @Expose
        public int is_completed;
        @Expose
        public int is_cancelled;
        @Expose
        public String message;

        @Expose
        public int payment_opt;
        @Expose
        public String distance;
        @Expose
        public String time;

        @Expose
        public int type;

        @Expose
        public Bill bill;


        protected Request(Parcel in) {
            inprogress = in.readByte() != 0;
            trip = in.readByte() != 0;
            pickLongitude = in.readDouble();
            pickLocation = in.readString();
            pickLatitude = in.readDouble();
            dropLatitude = in.readDouble();
            id = in.readInt();
            is_share = in.readInt();
            dropLocation = in.readString();
            requestId = in.readString();
            time_left = in.readString();
            dropLongitude = in.readDouble();
            trip_start_time = in.readString();
            is_driver_started = in.readInt();
            is_driver_arrived = in.readInt();
            is_trip_start = in.readInt();
            is_completed = in.readInt();
            is_cancelled = in.readInt();
            payment_opt = in.readInt();
            distance = in.readString();
            time = in.readString();
            type = in.readInt();
        }

        public  final Creator<Request> CREATOR = new Creator<Request>() {
            @Override
            public Request createFromParcel(Parcel in) {
                return new Request(in);
            }

            @Override
            public Request[] newArray(int size) {
                return new Request[size];
            }
        };

        public LatLng getPickupLatlng() {
            return new LatLng(pickLatitude, pickLongitude);
        }

        public LatLng getDropLatlng() {
            return new LatLng(dropLatitude, dropLongitude);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte((byte) (inprogress ? 1 : 0));
            dest.writeByte((byte) (trip ? 1 : 0));
            dest.writeDouble(pickLongitude);
            dest.writeString(pickLocation);
            dest.writeDouble(pickLatitude);
            dest.writeDouble(dropLatitude);
            dest.writeInt(id);
            dest.writeInt(is_share);
            dest.writeString(dropLocation);
            dest.writeString(requestId);
            dest.writeString(time_left);
            dest.writeDouble(dropLongitude);
            dest.writeString(trip_start_time);
            dest.writeInt(is_driver_started);
            dest.writeInt(is_driver_arrived);
            dest.writeInt(is_trip_start);
            dest.writeInt(is_completed);
            dest.writeInt(is_cancelled);
            dest.writeInt(payment_opt);
            dest.writeString(distance);
            dest.writeString(time);
            dest.writeInt(type);
        }
    }
    public static class Documents implements Serializable, Parcelable{
        @SerializedName("id")
        public int id;
        @SerializedName("driver_id")
        public int driver_id;

        @SerializedName("document_name")
        public String document_name;

        @SerializedName("document")
        public String document;
        @Expose
        public String document_ex_date;

        @SerializedName("identify_number")
        public String identify_number;

        @Expose
        public String document_id;

        public Documents(String document_name){
            this.document_name=document_name;
        }

        protected Documents(Parcel in) {
            id = in.readInt();
            driver_id = in.readInt();
            document_name = in.readString();
            document = in.readString();
            document_ex_date= in.readString();
            identify_number= in.readString();
            document_id = in.readString();
        }

        public  final Creator<Documents> CREATOR = new Creator<Documents>() {
            @Override
            public Documents createFromParcel(Parcel in) {
                return new Documents(in);
            }

            @Override
            public Documents[] newArray(int size) {
                return new Documents[size];
            }
        };



        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeInt(driver_id);
            dest.writeString(document_name);
            dest.writeString(document);
            dest.writeString(document_ex_date);
            dest.writeString(identify_number);
            dest.writeString(document_id);
        }
    }

    /**
     * Not Received IN API
     ***/
    public RequestModel(Request request, User user) {
        this.request = request;
//        this.user = user;
    }

    public class Bill implements Serializable, Parcelable {
        @Expose
        public int show_bill;
        @Expose
        public Double base_price;
        @Expose
        public Double ride_fare;
        @Expose
        public String base_distance;
        @Expose
        public String price_per_distance;
        @Expose
        public Double price_per_time;
        @Expose
        public Double distance_price;
        @Expose
        public Double time_price;
        @Expose
        public Double wallet_amount;
        @Expose
        public Double waiting_price;
        @Expose
        public Double service_tax;
        @Expose
        public String service_tax_percentage;
        @Expose
        public Double promo_amount;
        @Expose
        public Double referral_amount;
        @Expose
        public String service_fee;
        @Expose
        public String driver_amount;
        @Expose
        public Double total;
        @Expose
        public String currency;
        @Expose
        public String unit_in_words;
        @SerializedName("totalAdditionalCharge")
        public Double totalAdditionalCharge;
        @SerializedName("round_up_minimum_price")
        public Double round_up_minimum_price;
        @SerializedName("AdditionalCharge")
        public List<AdditionalCharge> additionalCharge;


        protected Bill(Parcel in) {
            show_bill = in.readInt();
            if (in.readByte() == 0) {
                base_price = null;
            } else {
                base_price = in.readDouble();
            }
            if (in.readByte() == 0) {
                ride_fare = null;
            } else {
                ride_fare = in.readDouble();
            }
            base_distance = in.readString();
            price_per_distance = in.readString();
            if (in.readByte() == 0) {
                price_per_time = null;
            } else {
                price_per_time = in.readDouble();
            }
            if (in.readByte() == 0) {
                distance_price = null;
            } else {
                distance_price = in.readDouble();
            }
            if (in.readByte() == 0) {
                time_price = null;
            } else {
                time_price = in.readDouble();
            }
            if (in.readByte() == 0) {
                wallet_amount = null;
            } else {
                wallet_amount = in.readDouble();
            }
            if (in.readByte() == 0) {
                waiting_price = null;
            } else {
                waiting_price = in.readDouble();
            }
            if (in.readByte() == 0) {
                service_tax = null;
            } else {
                service_tax = in.readDouble();
            }
            if (in.readByte() == 0) {
                totalAdditionalCharge= null;
            } else {
                totalAdditionalCharge = in.readDouble();
            }
            service_tax_percentage = in.readString();
            if (in.readByte() == 0) {
                promo_amount = null;
            } else {
                promo_amount = in.readDouble();
            }
            if (in.readByte() == 0) {
                referral_amount = null;
            } else {
                referral_amount = in.readDouble();
            }
            service_fee = in.readString();
            driver_amount = in.readString();

            if (in.readByte() == 0) {
                total = null;
            } else {
                total = in.readDouble();
            }
            currency = in.readString();
            unit_in_words = in.readString();
        }

        public  final Creator<Bill> CREATOR = new Creator<Bill>() {
            @Override
            public Bill createFromParcel(Parcel in) {
                return new Bill(in);
            }

            @Override
            public Bill[] newArray(int size) {
                return new Bill[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(show_bill);
            if (base_price == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(base_price);
            }  if (ride_fare == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(ride_fare);
            }
            dest.writeString(base_distance);
            dest.writeString(price_per_distance);
            if (price_per_time == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(price_per_time);
            }
            if (distance_price == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(distance_price);
            }
            if (time_price == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(time_price);
            }
            if (wallet_amount == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(wallet_amount);
            }
            if (waiting_price == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(waiting_price);
            }
            if (service_tax == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(service_tax);
            }

            if (totalAdditionalCharge== null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(totalAdditionalCharge);
            }

            dest.writeString(service_tax_percentage);
            if (promo_amount == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(promo_amount);
            }
            if (referral_amount == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(referral_amount);
            }
            dest.writeString(service_fee);
            dest.writeString(driver_amount);
            if (total == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(total);
            }
            dest.writeString(currency);
            dest.writeString(unit_in_words);
        }
    }
    public class AdditionalCharge{
        @SerializedName("name")
        public String name;
        @SerializedName("id")
        public String id;
        @SerializedName("amount")
        public String amount;
    }
    /**
     * Not Received IN API
     ***/
    public class User implements Serializable, Parcelable{

        @SerializedName("firstname")
        public String firstname;

        @SerializedName("review")
        public float review;

        @SerializedName("profile_pic")
        public String profilePic;

        @SerializedName("phone_number")
        public String phoneNumber;

        @SerializedName("id")
        public int id;

        @SerializedName("email")
        public String email;

        @SerializedName("lastname")
        public String lastname;

        @SerializedName("is_presented")
        @Expose
        public Boolean Ispresented;
        protected User(Parcel in) {
            firstname = in.readString();
            review = in.readFloat();
            profilePic = in.readString();
            phoneNumber = in.readString();
            id = in.readInt();
            email = in.readString();
            lastname = in.readString();
        }

        public  final Creator<User> CREATOR = new Creator<User>() {
            @Override
            public User createFromParcel(Parcel in) {
                return new User(in);
            }

            @Override
            public User[] newArray(int size) {
                return new User[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(firstname);
            dest.writeFloat(review);
            dest.writeString(profilePic);
            dest.writeString(phoneNumber);
            dest.writeInt(id);
            dest.writeString(email);
            dest.writeString(lastname);
        }
    }

    protected RequestModel(Parcel in) {
        success = in.readByte() != 0;
    }

    public RequestModel() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RequestModel> CREATOR = new Creator<RequestModel>() {
        @Override
        public RequestModel createFromParcel(Parcel in) {
            return new RequestModel(in);
        }

        @Override
        public RequestModel[] newArray(int size) {
            return new RequestModel[size];
        }
    };
}
