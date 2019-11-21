package bz.pei.driver.ui.drawerscreen.dialog.approval;

import android.content.Context;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 12/28/17.
 */

public interface ApprovalFragmentNavigator extends BaseView{
    public Context getAttachedContext();
    public void makeCallCustomerCare(String phoneNumber);
    public void openManageDocScreen();
}
