package bz.pei.driver.ui.Login.LoginViaOTP;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 10/7/17.
 */

public interface LoginOTPNavigator extends BaseView{

    public  void openOtpActivity(String country_code, String phonenumber);
    public  void openSocialActivity();
    public  void HideKeyBoard();
    public  String getCountryCode();
    public  String getCountryShortName();
    public  void setCountryCode(String flat);

}
