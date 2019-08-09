package bz.pei.driver.ui.DrawerScreen.Fragmentz.Profile;

import android.content.Context;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by naveen on 13/11/17.
 */

public interface ProfileNavigator extends BaseView {
    public void alertSelectCameraGalary();
    public void galleryIntent();
    public void cameraIntent();
    public void refreshDrawerActivity();

    public Context getAttachedContext();
    public void logoutApp();
}
