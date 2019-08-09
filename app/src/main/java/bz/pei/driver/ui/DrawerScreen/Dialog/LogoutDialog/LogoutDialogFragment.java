package bz.pei.driver.ui.DrawerScreen.Dialog.LogoutDialog;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.databinding.DialogLogoutBinding;
import bz.pei.driver.ui.Base.BaseDialog;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;

import javax.inject.Inject;

/**
 * Created by root on 1/3/18.
 */

public class LogoutDialogFragment extends BaseDialog<DialogLogoutBinding, LogoutViewModel> implements LogoutNavigator {
    DialogLogoutBinding binding;
    @Inject
    LogoutViewModel viewModel;
    public static String param = "PARAM";
    public static String TAG = "LogoutDialogFragment";

    public static LogoutDialogFragment newInstance(String param) {

        Bundle args = new Bundle();
        args.putString(LogoutDialogFragment.param, param);
        LogoutDialogFragment fragment = new LogoutDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getDataBinding();
        viewModel.setNavigator(this);
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public LogoutViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public DialogLogoutBinding getDataBinding() {
        return binding;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_logout;
    }

    @Override
    public void dismissDialog() {
        getDialog().dismiss();
    }

    @Override
    public void confirmLogout() {
        dismissDialog();
        ((DrawerAct) getContext()).initializeLogout();
    }

}
