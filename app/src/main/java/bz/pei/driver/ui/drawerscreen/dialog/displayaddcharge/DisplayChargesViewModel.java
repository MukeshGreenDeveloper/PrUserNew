package bz.pei.driver.ui.drawerscreen.dialog.displayaddcharge;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;

import com.google.gson.Gson;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 12/28/17.
 */

public class DisplayChargesViewModel extends BaseNetwork<RequestModel, DisplayChargesNavigator> {
    public ObservableField<String> title = new ObservableField<>("");
    public ObservableField<String> addCharge = new ObservableField<>("");
    public ObservableBoolean isAddChargeAvailable= new ObservableBoolean(true);
    SharedPrefence sharedPrefence;

    HashMap<String, String> map = new HashMap<>();

    public DisplayChargesViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
    }


    public void deleteItem(String additional_charge_id) {
        if (!getmNavigator().isNetworkConnected())
            getmNavigator().showNetworkMessage();
        else {
            setIsLoading(true);
            map.put(Constants.NetworkParameters.additional_charge_id, additional_charge_id);
            deleteAddionalCharge();
        }

    }

    public void getCharges() {
        if (!getmNavigator().isNetworkConnected())
            getmNavigator().showNetworkMessage();
        else {
            setIsLoading(true);
            getRequestInProgress();
        }
    }

    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        setIsLoading(false);
        if (response.success && response.successMessage.equalsIgnoreCase(Constants.SuccessMessages.REQUEST_IN_PROGRESS_Message)) {
            if (response.request != null && response.request.bill != null && response.request.bill.additionalCharge != null) {
                getmNavigator().setupList((ArrayList<RequestModel.AdditionalCharge>) response.request.bill.additionalCharge, response.request.bill.currency);
                isAddChargeAvailable.set(response.request.bill.additionalCharge.size()>0);
            }
        } else if (response.success && response.successMessage.equalsIgnoreCase(Constants.SuccessMessages.ADDITIONAL_CHARGE_DELETED_SUCCESSFULLY)) {
            getCharges();
        }

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (!CommonUtils.IsEmpty(e.getMessage()))
            getmNavigator().showMessage(e.getMessage());

    }

    public void onAddAdditionalCharge(View v) {
        getmNavigator().showAdditionalChargedialog();
    }
    public void closeDialog(View view){
        getmNavigator().dismissDialog("");
    }

    @Override
    public HashMap<String, String> getMap() {
        map.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        map.put(Constants.NetworkParameters.REQUEST_ID, sharedPrefence.getIntvalue(SharedPrefence.REQUEST_ID) + "");
        return map;
    }
}
