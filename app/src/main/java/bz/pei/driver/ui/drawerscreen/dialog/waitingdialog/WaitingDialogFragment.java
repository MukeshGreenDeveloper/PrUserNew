package bz.pei.driver.ui.drawerscreen.dialog.waitingdialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.databinding.FragmentWaitingDialogBinding;
import bz.pei.driver.ui.Base.BaseDialog;
import bz.pei.driver.utilz.Constants;

import javax.inject.Inject;

/**
 * Created by root on 12/29/17.
 */

public class WaitingDialogFragment extends BaseDialog<FragmentWaitingDialogBinding, WaitingViewModel> implements WaitingDialogNavigator {
    public static String TAG = "WaitingDialogFragment";
    public static String Param = "param";
    @Inject
    WaitingViewModel viewModel;
    FragmentWaitingDialogBinding binding;
    public int currentWaitingTime = 0, currentWaitingSec = 0;
    WebView wv_bgWave;
    public static WaitingDialogFragment newInstance(Integer waitingSec, Integer waitingTime) {
        WaitingDialogFragment fragment = new WaitingDialogFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.IntentExtras.PREVAILING_WAITING_SEC, waitingSec);
        args.putInt(Constants.IntentExtras.PREVAILING_WAITING_TIME, waitingTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentWaitingTime = getArguments().getInt(Constants.IntentExtras.PREVAILING_WAITING_TIME);
            if (getArguments().get(Constants.IntentExtras.PREVAILING_WAITING_SEC) != null)
                currentWaitingSec = getArguments().getInt(Constants.IntentExtras.PREVAILING_WAITING_SEC);
        }


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setLayout(width, height);
        wv_bgWave = (WebView) view.findViewById(R.id.wv_bgWave);
        wv_bgWave.loadUrl("file:///android_asset/html_assets/wave.html");
        wv_bgWave.setVerticalScrollBarEnabled(false);
        wv_bgWave.setHorizontalScrollBarEnabled(false);
        setDialogFullSCreen();
        binding = getmBinding();
        viewModel.setNavigator(this);
        viewModel.startTimer(currentWaitingTime,currentWaitingSec);
        setCancelable(false);
    }

    @Override
    public void dismissDialog(int time, int seconds) {
        getDialog().dismiss();
        Intent intent = getActivity().getIntent();
        intent.putExtra(Constants.IntentExtras.WAITING_TIME, time);
        intent.putExtra(Constants.IntentExtras.PREVAILING_WAITING_SEC, seconds);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }

    @Override
    public WaitingViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public FragmentWaitingDialogBinding getDataBinding() {
        return binding;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_waiting_dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"keys---Waiting:Totaly Destroyed");
    }
}
