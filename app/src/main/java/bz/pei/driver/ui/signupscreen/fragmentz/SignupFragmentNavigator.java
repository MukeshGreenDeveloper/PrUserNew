package bz.pei.driver.ui.signupscreen.fragmentz;

import android.content.Context;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 10/11/17.
 */

public interface SignupFragmentNavigator extends BaseView {

    public Context getAttachedcontext();

    public void galleryIntent();

    public void cameraIntent();

    public void alertSelectCameraGalary();
    public void confromNxt();
    public int spinSelectionPosition();
    public  String getCountryCode();

    public  String getCountryShortName();
    public  void setCurrentCountryCode(String countryCode);

//    public FragmentSignupBinding getViewBinded();


}
