package bz.pei.driver.ui.DrawerScreen.Fragmentz.SupportPage;

import android.databinding.ObservableField;
import android.view.View;

import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.Retro.Base.BaseNetwork;
import bz.pei.driver.Retro.GitHubService;
import bz.pei.driver.Retro.ResponseModel.SignupModel;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

/**
 * Created by root on 11/3/17.
 */

public class SupportFragViewModel extends BaseNetwork<SignupModel, SupportFragNavigator> {

    SharedPrefence sharedPrefence;
    HashMap<String, String> hashMap;
    public ObservableField<String> comments = new ObservableField<>("");

    public SupportFragViewModel(GitHubService gitHubService,
                                SharedPrefence sharedPrefence,
                                HashMap<String, String> hashMap, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        this.hashMap = hashMap;
    }


    @Override
    public void onSuccessfulApi(long taskId, SignupModel response) {
        setIsLoading(false);
        if (response.successMessage != null && response.successMessage.equalsIgnoreCase("email_send_to_support_email")) {
            comments.set("");
            if(getmNavigator().getAttachedContext()!=null)
            getmNavigator().showMessage(getmNavigator().getAttachedContext().getString(R.string.txt_support_email_send));
        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED || e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED
                || e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if (getmNavigator().getAttachedContext() != null) {
                getmNavigator().logout();
                getmNavigator().showMessage(getmNavigator().getAttachedContext().getString(R.string.text_already_login));
            }
        } else
            getmNavigator().showMessage(e);
    }

    @Override
    public HashMap<String, String> getMap() {

        return hashMap;
    }

    public void onclickSubmit(View view) {
        if (CommonUtils.IsEmpty(comments.get()))
            return;
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.message, comments.get());
            sendSupportFeedback();
        } else {
            getmNavigator().showNetworkMessage();
        }
    }
}
