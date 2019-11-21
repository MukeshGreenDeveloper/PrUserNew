package bz.pei.driver.ui.drawerscreen.dialog.canceldialog;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.SignupModel;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by root on 12/28/17.
 */

public class CancelDialogViewModel extends BaseNetwork<SignupModel, CancelDialogNavigator> implements AdapterView.OnItemSelectedListener {
    public ObservableField<String> cancelReasonObserval = new ObservableField<>();
    public ObservableBoolean cancelReasonOthers = new ObservableBoolean(false);
    public ObservableArrayList arrayCancelReason = new ObservableArrayList();
    public String selectedReason = "";
    private static Context context;

    public CancelDialogViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService,sharedPrefence,gson);
    }

    @Override
    public void onSuccessfulApi(long taskId, SignupModel response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    @BindingAdapter("spinArray")
    public static void setSpinAdapter(Spinner spinner, ArrayList<String> ar) {

        if (ar != null)
            if (ar.size() > 0 && context != null)
                spinner.setAdapter(new ArrayAdapter(context, android.R.layout.simple_list_item_1, ar));
    }

    public void setSpinnerAdapter() {
        context = getmNavigator().getAttachedContedt();
        if (context != null)
            Collections.addAll(arrayCancelReason, context.getResources().getStringArray(R.array.array_cancel_reason));
    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }

    public void cancelConfirm(View view) {
        if (CommonUtils.IsEmpty(cancelReasonOthers.get() ? cancelReasonObserval.get() : selectedReason))
            getmNavigator().showMessage(translationModel.text_error_empty_reason/*R.string.text_error_empty_reason*/);
        else
            getmNavigator().confirmCancelation(cancelReasonOthers.get() ? cancelReasonObserval.get() : selectedReason);
    }

    public void canceldialog(View view) {
        getmNavigator().dismissDialog();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        cancelReasonOthers.set(position == 4);
        if (arrayCancelReason != null && arrayCancelReason.size() > 0) {
            selectedReason = arrayCancelReason.get(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
