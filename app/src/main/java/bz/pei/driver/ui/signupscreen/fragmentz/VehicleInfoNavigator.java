package bz.pei.driver.ui.signupscreen.fragmentz;


import android.content.Context;
import androidx.viewpager.widget.ViewPager;

import bz.pei.driver.retro.responsemodel.VehicleTypeModel;
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
