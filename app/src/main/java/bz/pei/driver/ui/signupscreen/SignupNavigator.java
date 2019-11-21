package bz.pei.driver.ui.signupscreen;

import android.content.Context;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 10/11/17.
 */

public interface SignupNavigator extends BaseView{
    public void sendBroadtoFragment();
    public void changePagerPosition();
    public void openMainActivity();
    public Context getAttachedContext();
    public void openDrawrActivity();
//    public boolean validatefields();
}
