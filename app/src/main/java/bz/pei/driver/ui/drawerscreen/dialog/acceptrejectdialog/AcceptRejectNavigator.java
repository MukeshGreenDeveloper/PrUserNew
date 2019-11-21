package bz.pei.driver.ui.drawerscreen.dialog.acceptrejectdialog;

import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 12/16/17.
 */

public interface AcceptRejectNavigator extends BaseView{
    public BaseActivity getAttachedContext();
    public void dismissDialog();
    public void gotToTripFragment(RequestModel model);
    public void logoutAppInvalidToken();
    public void resumeDriverState();
    public void automaticCancelTheTrip();
    public void cancelReasonDialog();
}
