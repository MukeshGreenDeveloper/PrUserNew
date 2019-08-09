package bz.pei.driver.ui.SignupScreen.Fragmentz;


import android.content.Context;
import android.support.v4.view.ViewPager;

import bz.pei.driver.Retro.ResponseModel.VehicleTypeModel;
import bz.pei.driver.ui.Base.BaseView;

import java.util.List;

/**
 * Created by root on 10/10/17.
 */

public interface VehicleInfoNavigator extends BaseView {
    public void openDocUploadActivity();

    public void setUpPagerAdapter(List<VehicleTypeModel.Type> model);

    public void setUpPagerPosition(boolean isRight);

    public Context getAttachedcontext();
    public ViewPager getPagerAdapter();
}
