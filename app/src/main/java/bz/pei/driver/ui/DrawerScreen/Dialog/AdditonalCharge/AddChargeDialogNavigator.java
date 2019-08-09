package bz.pei.driver.ui.DrawerScreen.Dialog.AdditonalCharge;

import android.content.Context;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 12/28/17.
 */

public interface AddChargeDialogNavigator extends BaseView{
    public void dismissRefreshDialog();
    public void dismissDialog();
    public Context getAttachedContext();
}
