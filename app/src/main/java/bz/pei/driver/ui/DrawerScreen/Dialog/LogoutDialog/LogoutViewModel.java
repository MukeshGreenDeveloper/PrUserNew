package bz.pei.driver.ui.DrawerScreen.Dialog.LogoutDialog;

import android.view.View;

import bz.pei.driver.Retro.Base.BaseNetwork;
import bz.pei.driver.Retro.GitHubService;
import bz.pei.driver.Retro.ResponseModel.SignupModel;
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
