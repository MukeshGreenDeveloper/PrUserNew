package bz.pei.driver.ui.drawerscreen.fragmentz.faq;

import android.content.Context;

import bz.pei.driver.retro.responsemodel.FAQModel;
import bz.pei.driver.ui.Base.BaseView;

import java.util.List;

/**
 * Created by naveen on 13/11/17.
 */

public interface FaqNavigator extends BaseView {
    public void loadFaQItems(List<FAQModel> faqModelList);
    public Context getAttachedContext();

    public void logoutApp();
}
