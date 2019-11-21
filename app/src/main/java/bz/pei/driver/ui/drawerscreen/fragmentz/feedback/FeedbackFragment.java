package bz.pei.driver.ui.drawerscreen.fragmentz.feedback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.databinding.FeedbackLayoutBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.drawerscreen.dialog.billdialog.BillDialogFragment;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

/**
 * Created by root on 12/28/17.
 */

public class FeedbackFragment extends BaseFragment<FeedbackLayoutBinding, FeedbackViewModel> implements FeedbackNavigator {
    public static final String TAG = "FeedbackFragment";
    @Inject
    FeedbackViewModel viewModel;
    FeedbackLayoutBinding layoutBinding;
    RequestModel model;
    @Inject
    SharedPrefence sharedPrefence;
    public static FeedbackFragment newInstance(RequestModel param1) {
        FeedbackFragment fragment = new FeedbackFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.IntentExtras.REQUEST_DATA, param1);
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
        layoutBinding = getViewDataBinding();
        viewModel.setNavigator(this);
        viewModel.setUserDetails(model);

        if (model != null)
            if (model.request != null)
                if (model.request.bill != null)
                    if (model.request.bill.show_bill == 1)
                        BillDialogFragment.newInstance(model).show(this.getChildFragmentManager(), BillDialogFragment.TAG);
        ((DrawerAct)getBaseActivity()).disableToggleStatusIcon(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBaseActivity().setTitle(R.string.app_title);
    }

    @Override
    public FeedbackViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.feedback_layout;
    }


    @Override
    public BaseActivity getAttachedContext() {
        return (BaseActivity) getContext();
    }

    @Override
    public void navigateToHomeFragment() {
        if (getAttachedContext()!=null)
//        getAttachedContext().showRefreshedHOme();
        ((DrawerAct)getActivity()).resumeDriverState();
    }
    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }
    @Override
    public void loginAgain() {
        ((DrawerAct) getContext()).logoutApp();
    }
}
