package bz.pei.driver.ui.Splash;

import android.content.Context;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 10/11/17.
 */

public interface SplashNavigator extends BaseView{
    public Context getAttachedContext();
    public void startRequestingPermissions();
}
