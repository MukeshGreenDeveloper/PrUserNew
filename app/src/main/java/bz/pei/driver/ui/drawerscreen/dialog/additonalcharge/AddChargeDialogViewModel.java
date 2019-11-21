package bz.pei.driver.ui.drawerscreen.dialog.additonalcharge;

import androidx.databinding.ObservableField;
import android.view.View;

import com.google.gson.Gson;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

/**
 * Created by root on 12/28/17.
 */

public class AddChargeDialogViewModel extends BaseNetwork<RequestModel, AddChargeDialogNavigator> {
    public ObservableField<String> title = new ObservableField<>("");
    public ObservableField<String> addCharge = new ObservableField<>("");
    public HashMap<String, String> map = new HashMap<>();
    SharedPrefence sharedPrefence;

    public AddChargeDialogViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
    }


    public void onConfirm(View v) {

        if (!getmNavigator().isNetworkConnected())
            getmNavigator().showNetworkMessage();
        else {
            setIsLoading(true);
            getmNavigator().dismissRefreshDialog();
        }
    }

    public void onSkip(View v) {
        getmNavigator().dismissDialog();
    }

    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        setIsLoading(true);
        if(response.success &&response.successMessage.equalsIgnoreCase("additional_charge_added_successfully"))
            getmNavigator().dismissRefreshDialog();
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (!CommonUtils.IsEmpty(e.getMessage()))
            getmNavigator().showMessage(e.getMessage());
    }

    public void onAddAdditionalCharge(View v) {
        if (CommonUtils.IsEmpty(addCharge.get()) || CommonUtils.IsEmpty(title.get()))
            return;
        if (!getmNavigator().isNetworkConnected())
            getmNavigator().showNetworkMessage();
        else {
            setIsLoading(true);
            sendAddionalCharge();
        }
    }

    @Override
    public HashMap<String, String> getMap() {
        map.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        map.put(Constants.NetworkParameters.REQUEST_ID, sharedPrefence.getIntvalue(SharedPrefence.REQUEST_ID) + "");
        map.put(Constants.NetworkParameters.amount, addCharge.get());
        map.put(Constants.NetworkParameters.name, title.get());
        return map;
    }
}
