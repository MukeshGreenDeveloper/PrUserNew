package bz.pei.driver.ui.DrawerScreen.Fragmentz;

import android.content.Context;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 10/11/17.
 */

public interface MapNavigator extends BaseView {

    public Context getAttachedcontext();
    public void sendLocations(String id, String lat, String lng, String bearing);
    public void startSocket();
    public void stopSocket();
    public void toggleOflineOnline(boolean isactive);

}
