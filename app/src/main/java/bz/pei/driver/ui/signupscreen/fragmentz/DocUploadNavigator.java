package bz.pei.driver.ui.signupscreen.fragmentz;

import android.content.Context;
import android.databinding.ObservableField;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 10/10/17.
 */

public interface DocUploadNavigator extends BaseView {
    public void openMainActivity();
    public void openDocUploadDialog(ObservableField<String> imgUrl);
    public void setupImage(String url);
    public Context getAttachedcontext();
}
