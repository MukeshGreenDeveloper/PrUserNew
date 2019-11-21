package bz.pei.driver.ui.drawerscreen.dialog.logoutdialog;

import android.view.View;

import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.SignupModel;
import bz.pei.driver.utilz.Exception.CustomException;

import java.util.HashMap;

/**
 * Created by root on 1/3/18.
 */

public class LogoutViewModel extends BaseNetwork<SignupModel, LogoutNavigator> {


    public LogoutViewModel(GitHubService gitHubService) {
        super(gitHubService);
    }

    public void cancelLogout(View v) {
        getmNavigator().dismissDialog();
    }

    public void confirmLogout(View v) {
        getmNavigator().confirmLogout();
    }

    @Override
    public void onSuccessfulApi(long taskId, SignupModel response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }
}
