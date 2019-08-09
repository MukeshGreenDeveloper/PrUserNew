package bz.pei.driver.Retro.ResponseModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import bz.pei.driver.Retro.Base.BaseResponse;

public class DriverModel extends BaseResponse implements Parcelable {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("firstname")
    @Expose
    public String firstname;
    @SerializedName("lastname")
    @Expose
    public String lastname;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("phone")
    @Expose
    public String phone;
    @SerializedName("login_by")
    @Expose
    public String loginBy;
    @SerializedName("login_method")
    @Expose
    public String loginMethod;
    @SerializedName("profile_pic")
    @Expose
    public String profilepic;


    @SerializedName("is_presented")
    @Expose
    public Boolean Ispresented;
    @Expose
    public String car_model;
    @Expose
    public String car_number;
    @Expose
    public String type_name;
    @Expose
    public String type_icon;

    @Expose
    public int share_state;
    @Expose
    public int is_active;
    @Expose
    public int is_approve;
    @Expose
    public int document_upload_status;
    @Expose
    public int is_available;
    @SuppressWarnings("unchecked")
    protected DriverModel(Parcel in) {
        firstname = in.readString();
        lastname = in.readString();
        email = in.readString();
        phone = in.readString();
        loginBy = in.readString();
        loginMethod = in.readString();
        car_model= in.readString();
        car_number = in.readString();
        type_icon = in.readString();
        type_name = in.readString();
        profilepic = in.readString();
        is_active = in.readInt();
        is_approve = in.readInt();
        is_available = in.readInt();
    }
    @SuppressWarnings("rawtypes")
    public static final Creator<DriverModel> CREATOR = new Creator<DriverModel>() {
        @Override
        public DriverModel createFromParcel(Parcel in) {
            return new DriverModel(in);
        }

        @Override
        public DriverModel[] newArray(int size) {
            return new DriverModel[size];
        }
    };

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firstname);
        parcel.writeString(lastname);
        parcel.writeString(email);
        parcel.writeString(phone);
        parcel.writeString(loginBy);
        parcel.writeString(loginMethod);
        parcel.writeString(profilepic);
        parcel.writeString(car_model);
        parcel.writeString(car_number);
        parcel.writeString(type_name);
        parcel.writeString(type_icon);
        parcel.writeInt(is_active);
        parcel.writeInt(is_approve);
        parcel.writeInt(is_available);
    }
}