package bz.pei.driver.ui.DrawerScreen.Dialog.CancelDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.databinding.DialogCancelBinding;
import bz.pei.driver.ui.Base.BaseDialog;
import bz.pei.driver.utilz.Constants;

import javax.inject.Inject;

/**
 * Created by root on 12/15/17.
 */

public class CancelDialogFragment extends BaseDialog<DialogCancelBinding, CancelDialogViewModel> implements CancelDialogNavigator {
    public static final String TAG = "CancelDialogFragment";
    public static String Param = "param";
    @Inject
    CancelDialogViewModel viewmodel;
    DialogCancelBinding cancelBinding;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        cancelBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_cancel, container, false);
//        cancelBinding.setVariable(BR.viewModelss, getViewModel());
//        cancelBinding.executePendingBindings();
//        return cancelBinding.getRoot();
//    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewmodel != null) {
            viewmodel.setNavigator(this);
            cancelBinding = getmBinding();
            cancelBinding.spinCancelReason.setOnItemSelectedListener(viewmodel);
            viewmodel.setSpinnerAdapter();
//            if (getDialog().getWindow() != null) {
//                getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                getDialog().getWindow().setLayout(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT);
//            }
        }
    }

    @Override
    public CancelDialogViewModel getViewModel() {
        return viewmodel;
    }

    @Override
    public DialogCancelBinding getDataBinding() {
        return cancelBinding;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModelss;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_cancel;
    }

    @Override
    public void dismissDialog() {
        if (getDialog() != null)
            getDialog().dismiss();
        else
            dismissAllowingStateLoss();
    }

    @Override
    public void confirmCancelation(String message) {
        getDialog().dismiss();
        Intent intent = getActivity().getIntent();
        intent.putExtra(Constants.IntentExtras.CANCEL_REASON, message);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }

    @Override
    public Context getAttachedContedt() {
        return getContext();
    }


    public static CancelDialogFragment newInstance(String tag) {
        CancelDialogFragment fragment = new CancelDialogFragment();
        Bundle args = new Bundle();
        args.putString(Param, tag);
        fragment.setArguments(args);
        return fragment;
    }
}
