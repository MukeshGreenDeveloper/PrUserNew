package bz.pei.driver.ui.DrawerScreen.Dialog.Approval;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.databinding.FragmentApprovalDialogBinding;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

/**
 * Converted to FRagment
 */

public class ApprovalFragment extends BaseFragment<FragmentApprovalDialogBinding, ApprovalViewModel>
        implements View.OnClickListener,ApprovalFragmentNavigator {
    public static final String TAG = "ApprovalFragment";
    public static final String STATE_APPROVAL = "STATE_APPROVAL";
    FragmentApprovalDialogBinding binding;
    @Inject
    ApprovalViewModel dialogViewModel;
    @Inject
    SharedPrefence sharedPrefence;
    int approvalState = 4;

    public static ApprovalFragment newInstance(int prefState) {
        Bundle args = new Bundle();
        args.putInt(STATE_APPROVAL, prefState);
        ApprovalFragment fragment = new ApprovalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        setDialogFullSCreen();
        binding = getViewDataBinding();
        dialogViewModel.setNavigator(this);
        if (getBaseActivity() != null)
            ((DrawerAct) getBaseActivity()).disableToggleStatusIcon(false);
        approvalState = getArguments().getInt(STATE_APPROVAL);
        dialogViewModel.detail_description.set(getBaseActivity().getResources().getString(
                approvalState == 4 ? R.string.text_driver_declined_note
                        : approvalState == 3 ? R.string.text_driver_modifiedWaitingForApproval
                        : approvalState == 2 ? R.string.text_driver_notUploaded
                        : R.string.text_driver_declined_note));
        dialogViewModel.buttonText.set(getBaseActivity().getResources().getString(
                approvalState == 2 ? R.string.text_upload:
                        approvalState == 4 ? R.string.text_call_admin
                                : approvalState == 3 ? R.string.text_call_admin
                                : R.string.text_call_admin));
        dialogViewModel.isManageDocument.set(approvalState == 2);
        getBaseActivity().setTitle(R.string.app_title);
    }

    @Override
    public ApprovalViewModel getViewModel() {
        return dialogViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_approval_dialog;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public void onClick(View view) {
//        setCancelable(true);
//        dismiss();
        if (getBaseActivity() != null)
            getBaseActivity().finish();
    }


    @Override
    public Context getAttachedContext() {
        return getActivity();
    }

    @Override
    public void makeCallCustomerCare(String phoneNumber) {
        getBaseActivity().makeCAll(phoneNumber);
    }

    @Override
    public void openManageDocScreen() {
        getBaseActivity().showManageDocumentFragment();
    }
}
