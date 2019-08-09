package bz.pei.driver.Retro.ResponseModel;

import com.google.gson.annotations.SerializedName;

public class CountryCodeModel {

    @SerializedName("countryCode")
    private String countryCode;

    @SerializedName("metro_code")
    private int metroCode;

    @SerializedName("city")
    private String city;

    @SerializedName("ip")
    private String ip;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("country_name")
    private String countryName;

    @SerializedName("region_name")
    private String regionName;

    @SerializedName("time_zone")
    private String timeZone;

    @SerializedName("zip_code")
    private String zipCode;

    @SerializedName("region_code")
    private String regionCode;

    @SerializedName("longitude")
    private double longitude;

    @Override
    public String toString() {
        return countryCode;
    }
}