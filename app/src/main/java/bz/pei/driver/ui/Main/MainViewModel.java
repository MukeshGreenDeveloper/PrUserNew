package bz.pei.driver.ui.Main;

import android.view.View;

import com.google.gson.Gson;
import bz.pei.driver.Retro.Base.BaseNetwork;
import bz.pei.driver.Retro.Base.BaseResponse;
import bz.pei.driver.Retro.GitHubService;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 10/9/17.
 */

public class MainViewModel extends BaseNetwork<BaseResponse, MainNavigator> {


    SharedPrefence sharedPrefence;

    Gson gson;

    @Inject
    public MainViewModel(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
    }

    public void openLoginScreen(View v) {
        getmNavigator().openLoginActivity();
    }

    public void openSignupScreen(View v) {
        getmNavigator().openSignupActivity();
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }
}
