package bz.pei.driver.ui.drawerscreen.fragmentz.complaints;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.ComplaintsModel;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.MyDataComponent;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 12/21/17.
 */

public class ComplaintFragmentViewModel extends BaseNetwork<ComplaintsModel, ComplaintsNavigator> {

    public ObservableField<String> text_cmts = new ObservableField<>("");
    public ObservableList<ComplaintsModel.CompLaint> compLaintObservableList = new ObservableArrayList<>();
    Context context;
    SharedPrefence prefence;
    GitHubService apiService;
    public static Spinner spinnerItem;

    public ComplaintFragmentViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence,Gson gson) {
        super(gitHubService,sharedPrefence,gson);
        DataBindingUtil.setDefaultComponent(new MyDataComponent(this));
        this.prefence = sharedPrefence;
        this.apiService = gitHubService;
    }

    public void setUpData() {
        context = getmNavigator().getAttachedContext();
//        Getting Complaint List
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.NetworkParameters.id, prefence.Getvalue(SharedPrefence.DRIVER_ID));
        hashMap.put(Constants.NetworkParameters.token, prefence.Getvalue(SharedPrefence.DRIVER_TOKEN));
        hashMap.put(Constants.NetworkParameters.type, Constants.DRIVER_LIST_TYPES);
        if (getmNavigator().isNetworkConnected()) {
            mIsLoading.set(true);
            getComplaints(hashMap);
        }
    }

    public void onClickSendButon(View v) {
//        Toast.makeText(v.getContext(), "clicked", Toast.LENGTH_SHORT).show();
    }

    @BindingAdapter("spinList")
    public  void setImageUrl(Spinner spinner, List<ComplaintsModel.CompLaint> compLaintList) {
        Context context = spinner.getContext();
        spinnerItem = spinner;
        ArrayAdapter<ComplaintsModel.CompLaint> adapter = new ArrayAdapter<ComplaintsModel.CompLaint>(context, android.R.layout.simple_list_item_1, compLaintList);
        spinner.setAdapter(adapter);
    }

    public void reportComplaint(View view) {
        if (CommonUtils.IsEmpty(text_cmts.get())) {
            getmNavigator().showMessage(translationModel.text_error_comments/*view.getContext().getString(R.string.text_error_comments)*/);
            return;
        } else if (spinnerItem.getSelectedItemPosition() < 0 || spinnerItem.getSelectedItem() == null) {
            getmNavigator().showMessage(translationModel.text_error_title_complaint/*context.getString(R.string.text_error_title_complaint)*/);
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        String driverID = prefence.Getvalue(SharedPrefence.DRIVER_ID);
        String driverToken = prefence.Getvalue(SharedPrefence.DRIVER_TOKEN);
        if (!TextUtils.isEmpty(driverID) && !TextUtils.isEmpty(driverToken)) {
            map.put(Constants.NetworkParameters.id, driverID);
            map.put(Constants.NetworkParameters.token, driverToken);
            map.put(Constants.NetworkParameters.TITLE, compLaintObservableList.get(spinnerItem.getSelectedItemPosition()).id);
            map.put(Constants.NetworkParameters.DESCRIPTION, text_cmts.get());
            Log.i("Complaints params",driverID+" _ "+driverToken+" _ .. ");
            if (getmNavigator().isNetworkConnected()) {
                setIsLoading(true);
                reportComplaint(map);
            }
        }

    }

    @Override
    public void onSuccessfulApi(long taskId, ComplaintsModel response) {
        setIsLoading(false);
        switch ((int) taskId) {
            case Constants.TaskID.COMPLAINT_LIST: {
                if (response == null)
                    getmNavigator().showMessage(translationModel.text_error_contact_server/*context.getString(R.string.text_error_contact_server)*/);
                else if (response.success) {
                    if (response.complaint_list != null)
                        if (response.complaint_list.size() > 0) {
                            compLaintObservableList.clear();
                            compLaintObservableList.addAll(response.complaint_list);
                        }
                } else {
                    getmNavigator().showMessage(response.errorMessage);
                }
                break;
            }
            case Constants.TaskID.REPORT_COMPLAINT: {
                if (context == null)
                    context = getmNavigator().getAttachedContext();
                if (response == null)
                    getmNavigator().showMessage(translationModel.text_error_contact_server/*context.getString(R.string.text_error_contact_server)*/);
                else if (response.success) {
                    getmNavigator().showMessage(response.successMessage);
                    text_cmts.set("");
//                    getmNavigator().backpressActivity();
                } else {
                    getmNavigator().showMessage(response.errorMessage);
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED ||
                e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED ||
                e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if(getmNavigator().getAttachedContext()!=null)
                getmNavigator().showMessage(getmNavigator().getAttachedContext().getString(R.string.text_already_login));
            getmNavigator().logoutApp();
        }else
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }
}
