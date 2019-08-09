package bz.pei.driver.ui.Settings;

import android.content.Context;

import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by naveen on 13/11/17.
 */

public interface
SettingsNavigator extends BaseView {
    public Context getAttachedContext();

    public void logout();

    public void changeShareState(int share_state);
    void refreshScreen();
}
