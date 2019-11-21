package bz.pei.driver.ui.drawerscreen.fragmentz.feedback;

import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 12/28/17.
 */

public interface FeedbackNavigator extends BaseView {
//    public FeedbackLayoutBinding getBindingFromFragment();
    public BaseActivity getAttachedContext();
    public void navigateToHomeFragment();

    public void loginAgain();
}
