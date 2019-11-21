package bz.pei.driver.ui.forgot;

import android.content.Context;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 10/9/17.
 */

public interface ForgotNavigator extends BaseView {

    public void openLoginActivity();

    public void OpenCountrycodeINvisible();

    public void OpenCountrycodevisible();

    public void setCurrentCountryCode(String flag);

    public Context getAttachedContext();
}
