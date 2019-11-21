package bz.pei.driver.ui.acceptreject;

import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 10/9/17.
 */

public interface AcceptRejectActivityNavigator extends BaseView{
    public BaseActivity getAttachedContext();
    public void dismissDialog();
    public void gotToTripFragment(RequestModel model);
    public void logoutAppInvalidToken();
}
