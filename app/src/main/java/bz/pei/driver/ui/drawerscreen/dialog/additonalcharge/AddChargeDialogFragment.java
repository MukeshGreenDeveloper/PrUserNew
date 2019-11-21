package bz.pei.driver.ui.drawerscreen.dialog.additonalcharge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.databinding.DialogAddChageBinding;
import bz.pei.driver.ui.Base.BaseDialog;
import bz.pei.driver.ui.drawerscreen.dialog.displayaddcharge.DisplayChargesDialog;
import bz.pei.driver.utilz.Constants;

import javax.inject.Inject;

/**
 * Created by root on 12/28/17.
 */

public class AddChargeDialogFragment extends BaseDialog<DialogAddChageBinding, AddChargeDialogViewModel> implements AddChargeDialogNavigator {
    public static final String TAG = "AddChargeDialogFragme";
    DialogAddChageBinding binding;
    @Inject
    AddChargeDialogViewModel viewModel;
    RequestModel model;

    public static AddChargeDialogFragment newInstance(String param1) {
        AddChargeDialogFragment fragment = new AddChargeDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.IntentExtras.REQUEST_DATA, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = getArguments().getParcelable(Constants.IntentExtras.REQUEST_DATA);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.setNavigator(this);
//        setDialogFullSCreen();
        binding = getmBinding();
        binding.setViewModel(viewModel);
    }

    @Override
    public void dismissRefreshDialog() {
        getDialog().dismiss();
        Intent intent = getActivity().getIntent();
        if (getActivity() != null && getActivity().getSupportFragmentManager() != null)
            if (getActivity().getSupportFragmentManager().findFragmentByTag(DisplayChargesDialog.TAG) != null)
                getActivity().getSupportFragmentManager().findFragmentByTag(DisplayChargesDialog.TAG).onActivityResult(Constants.ADD_CHARGE_VALUE, Activity.RESULT_OK, intent);
    }

    @Override
    public void dismissDialog() {
        getDialog().dismiss();
    }

    @Override
    public Context getAttachedContext() {
        return getBaseActivity();
    }

    @Override
    public AddChargeDialogViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public DialogAddChageBinding getDataBinding() {
        return binding;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_add_chage;
    }
}
