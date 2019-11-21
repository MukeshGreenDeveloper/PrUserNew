package bz.pei.driver.ui.drawerscreen.dialog.displayaddcharge;

import android.content.Context;

import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.ui.Base.BaseView;

import java.util.ArrayList;

/**
 * Created by root on 12/28/17.
 */

public interface DisplayChargesNavigator extends BaseView{
    public void dismissDialog(String adChage);
    public Context getAttachedContext();
    public void setupList(ArrayList<RequestModel.AdditionalCharge> modelList, String currency);

    public void showAdditionalChargedialog();
}
