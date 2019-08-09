package bz.pei.driver.ui.Login;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 10/9/17.
 */

public interface LoginNavigator extends BaseView{

    public void OpenDrawerActivity();
    public void OpenForgotActivity();
    public void OpenCountrycodevisible();
    public void OpenCountrycodeINvisible();
    public String getCountryCode();
    public void setCurrentCountryCode(String flag);

}
