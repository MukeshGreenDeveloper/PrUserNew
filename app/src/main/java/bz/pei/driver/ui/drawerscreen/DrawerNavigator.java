package bz.pei.driver.ui.drawerscreen;

import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 10/11/17.
 */

public interface DrawerNavigator extends BaseView{

    public void finished();
    public void logoutApp();
    public void passtoFragmentDriverOfflineOnline(int isActive);
    public void ShareStatus(int isShare);
    public void openApprovalDialog(int driverSate);
    public BaseActivity getAttachedBaseActivity();
    public void openFeedbackFragment(RequestModel model);
    public void openTripFragment(RequestModel model);
    public void openAcceptReject(RequestModel model, String requestData);
    public void openMapFragment(int is_active);


    void openShareFragment();

    void sendCancelBroadcast(String cancelDAta);
}
