package bz.pei.driver.ui.Splash;

import android.databinding.ObservableField;

import com.google.gson.Gson;
import bz.pei.driver.BuildConfig;
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
 * Created by root on 10/11/17.
 */

public class SplashViewModel extends BaseNetwork<BaseResponse, SplashNavigator> {
    SharedPrefence sharedPrefence;
    Gson gson;
    public ObservableField<String> currentAppVersion=new ObservableField<>();

    @Inject
    public SplashViewModel(@Named(Constants.ourApp) GitHubService gitHubService
                           ,SharedPrefence sharedPrefence,Gson gson) {
        super(gitHubService,sharedPrefence,gson);
        this.sharedPrefence=sharedPrefence;
        this.gson=gson;
        currentAppVersion.set("v "+ BuildConfig.VERSION_NAME);
    }


    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.success) {
            if (response.data != null) {
                response.saveLanguageTranslations(sharedPrefence,gson,response.data);
            }
            getmNavigator().startRequestingPermissions();
        }

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e.getMessage());
        getmNavigator().startRequestingPermissions();
    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }


    public void getLanguagees() {

        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            getTranslations();
        } else
            getmNavigator().showNetworkMessage();

    }


}
