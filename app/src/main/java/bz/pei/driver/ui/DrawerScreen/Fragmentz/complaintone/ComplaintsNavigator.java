package bz.pei.driver.ui.DrawerScreen.Fragmentz.complaintone;

import android.content.Context;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by naveen on 13/11/17.
 */

public interface ComplaintsNavigator extends BaseView {
    public void backpressActivity();
////    public Spinner getSpinner();
////    public Button getButonSend();
    public Context getAttachedContext();

    public void logoutApp();
}
