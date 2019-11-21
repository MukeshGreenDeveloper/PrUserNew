package bz.pei.driver.retro.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import bz.pei.driver.retro.base.BaseResponse;

public class LoginModel extends BaseResponse {

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

    @SerializedName("is_presented")
    @Expose
    public Boolean Ispresented;



}