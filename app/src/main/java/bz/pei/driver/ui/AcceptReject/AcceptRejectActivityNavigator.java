package bz.pei.driver.ui.AcceptReject;

import bz.pei.driver.Retro.ResponseModel.RequestModel;
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
