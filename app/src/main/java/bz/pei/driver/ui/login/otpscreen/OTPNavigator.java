package bz.pei.driver.ui.login.otpscreen;


import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 10/9/17.
 */

public interface OTPNavigator extends BaseView {

    public String getOpt();

    public void FinishAct();

    public void startTimer(int sec);

    public void OpenDrawerActivity();

    public void openSinupuActivity(String phoneNumber);
}
