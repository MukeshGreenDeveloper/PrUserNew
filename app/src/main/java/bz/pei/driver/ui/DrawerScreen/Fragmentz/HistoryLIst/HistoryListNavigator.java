package bz.pei.driver.ui.DrawerScreen.Fragmentz.HistoryLIst;

import android.content.Context;

import bz.pei.driver.Retro.ResponseModel.HistoryModel;
import bz.pei.driver.ui.Base.BaseView;

import java.util.List;

/**
 * Created by root on 1/4/18.
 */

public interface HistoryListNavigator extends BaseView {
    public void addItem(List<HistoryModel> histories);
    public void Dostaff();
    public void MentionLastPage();

    public Context getAttachedcontext();

    public void logoutApp();
}
