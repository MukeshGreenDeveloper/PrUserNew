package bz.pei.driver.ui.splash;

import android.content.Context;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 10/11/17.
 */

public interface SplashNavigator extends BaseView{
    public Context getAttachedContext();
    public void startRequestingPermissions();
}
