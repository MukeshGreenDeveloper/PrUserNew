package bz.pei.driver.ui.DrawerScreen.Dialog.CancelDialog;

import android.content.Context;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 12/28/17.
 */

public interface CancelDialogNavigator extends BaseView {
    public void dismissDialog();
    public void confirmCancelation(String message);
    public Context getAttachedContedt();
}
