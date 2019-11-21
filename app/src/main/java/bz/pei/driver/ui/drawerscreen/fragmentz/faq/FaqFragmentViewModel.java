package bz.pei.driver.ui.drawerscreen.fragmentz.faq;

import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.base.BaseResponse;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.MyDataComponent;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

/**
 * Created by root on 12/21/17.
 */

public class FaqFragmentViewModel extends BaseNetwork<BaseResponse, FaqNavigator> {

    SharedPrefence prefence;
    GitHubService apiService;

    public FaqFragmentViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        DataBindingUtil.setDefaultComponent(new MyDataComponent(this));
        this.prefence = sharedPrefence;
        this.apiService = gitHubService;
    }

    /**
     * Getting FAQ LIst
     */
    public void setUpData() {
        if (getmNavigator().isNetworkConnected()) {
            mIsLoading.set(true);
            getFaq(getMap());
        }
    }


    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.successMessage.equalsIgnoreCase("driver_faq_list")) {
            if (response.faq_list != null && response.faq_list.size() > 0) {
                getmNavigator().loadFaQItems(response.faq_list);
            }
        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED ||
                e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED ||
                e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if (getmNavigator().getAttachedContext() != null)
                getmNavigator().showMessage(getmNavigator().getAttachedContext().getString(R.string.text_already_login));
            getmNavigator().logoutApp();
        } else
            getmNavigator().showMessage(e.getMessage());
        e.printStackTrace();
    }

    @Override
    public HashMap<String, String> getMap() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.NetworkParameters.id, prefence.Getvalue(SharedPrefence.DRIVER_ID));
        hashMap.put(Constants.NetworkParameters.token, prefence.Getvalue(SharedPrefence.DRIVER_TOKEN));
        hashMap.put(Constants.NetworkParameters.type, Constants.DRIVER_LIST_TYPES);
        return hashMap;
    }
}
