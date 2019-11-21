package bz.pei.driver.retro.responsemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by root on 12/12/17.
 */

public class SosModel implements Parcelable{
    @Expose
    public String  id;
    @Expose
    public String  name;
    @Expose
    public String  number;

    @SuppressWarnings("unchecked")
    protected SosModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        number = in.readString();
    }
    @SuppressWarnings("rawtypes")
    public static final Creator<SosModel> CREATOR = new Creator<SosModel>() {
        @Override
        public SosModel createFromParcel(Parcel in) {
            return new SosModel(in);
        }

        @Override
        public SosModel[] newArray(int size) {
            return new SosModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(number);
    }
}
