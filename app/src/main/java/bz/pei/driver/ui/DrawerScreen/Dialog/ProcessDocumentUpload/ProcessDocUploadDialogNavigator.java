package bz.pei.driver.ui.DrawerScreen.Dialog.ProcessDocumentUpload;

import android.content.Context;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 10/10/17.
 */

public interface ProcessDocUploadDialogNavigator extends BaseView {
    public void dismissSuccessResult();
    public Context getAttachedcontext();
    public void galleryIntent();
    public void cameraIntent();
    public void openDatePicker();
}
