package bz.pei.driver.ui.DrawerScreen.Fragmentz.HistoryLIst;

import android.databinding.ObservableBoolean;

import bz.pei.driver.R;
import bz.pei.driver.Retro.Base.BaseNetwork;
import bz.pei.driver.Retro.Base.BaseResponse;
import bz.pei.driver.Retro.GitHubService;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by root on 1/4/18.
 */

public class HistoryListViewModel extends BaseNetwork<BaseResponse, HistoryListNavigator> {

    HashMap<String, String> hashMap;
    int pageno = 0;
    public ObservableBoolean isdata;
    SharedPrefence sharedPrefence;

    @Inject
    public HistoryListViewModel(GitHubService gitHubService, HashMap<String, String> hashMap, SharedPrefence sharedPrefence) {
        super(gitHubService);
        this.hashMap = hashMap;
        this.sharedPrefence = sharedPrefence;
        isdata = new ObservableBoolean();
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.getHistory() != null && response.getHistory().size() != 0) {
            isdata.set(true);
//            if (pageno > 1) {
            getmNavigator().Dostaff();
//            }


            getmNavigator().addItem(response.getHistory());


        } else {
            if (response.successMessage.equalsIgnoreCase("driver_history_not_found")) {
                getmNavigator().MentionLastPage();
            } else
                getmNavigator().showMessage(response.successMessage);


        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        e.printStackTrace();
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED ||
                e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED ||
                e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if(getmNavigator().getAttachedcontext()!=null)
            getmNavigator().showMessage(getmNavigator().getAttachedcontext().getString(R.string.text_already_login));
            getmNavigator().logoutApp();
        }else
            getmNavigator().showMessage(e.getMessage());
    }

    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }


    public void fetchData(int currentPage) {
        if (getmNavigator().isNetworkConnected()) {
            pageno = currentPage;

            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));

            hashMap.put(Constants.NetworkParameters.page, "" + pageno);
            setIsLoading(true);
            GetHistoryNetworkCall();

        } else {
            getmNavigator().showNetworkMessage();
        }
    }
}
