package bz.pei.driver.ui.DrawerScreen.Dialog.AcceptRejectDialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.Retro.ResponseModel.RequestModel;
import bz.pei.driver.databinding.LayoutAcceptRejectBewBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.Base.BaseDialog;
import bz.pei.driver.ui.DrawerScreen.AcceptRejectRespondListener;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;
import bz.pei.driver.utilz.Constants;

import javax.inject.Inject;

/**
 * Created by root on 12/15/17.
 */

public class AcceptRejectDialogFragment extends BaseDialog<LayoutAcceptRejectBewBinding, AcceptRejectDialogViweModel> implements AcceptRejectNavigator {
    public static final String TAG = "AcceptRejectFragment";
    @Inject
    AcceptRejectDialogViweModel viewmodel;
    RequestModel requestModel;
    LayoutAcceptRejectBewBinding acceptRejectBinding;
    AcceptRejectRespondListener listner;

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
////        return inflater.inflate(R.layout.layout_accept_reject_bew, container);
//        acceptRejectBinding = DataBindingUtil.inflate(inflater, R.layout.layout_accept_reject_bew, container, false);
//        acceptRejectBinding.setVariable(BR.viewModels, getViewModel());
//        acceptRejectBinding.executePendingBindings();
//        return acceptRejectBinding.getRoot();
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDialogFullSCreen();
        setCancelable(false);
        requestModel = (RequestModel) getArguments().getParcelable(Constants.IntentExtras.REQUEST_DATA);
        if (requestModel != null)
            setUpTimerDetails();
        listner = (BaseActivity) getActivity();
//        setCancelable(false);
    }

    @Override
    public AcceptRejectDialogViweModel getViewModel() {
        return viewmodel;
    }

    @Override
    public LayoutAcceptRejectBewBinding getDataBinding() {
        return acceptRejectBinding;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModels;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_accept_reject_bew;
    }

    private void setUpTimerDetails() {
        if (viewmodel != null) {
            viewmodel.setRequestDetails(requestModel, getActivity());
            viewmodel.setNavigator(this);
        }
    }

    @Override
    public BaseActivity getAttachedContext() {
        return (BaseActivity) getActivity();
    }

    @Override
    public void dismissDialog() {
        setCancelable(true);
        if (getDialog() != null)
            getDialog().cancel();
//        else
//            dismissAllowingStateLoss();
        if (getAttachedContext() != null)
            getAttachedContext().restartLocationUpdate();
        if (getFragmentManager() != null)
            if (getFragmentManager().findFragmentByTag(TAG) != null)
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag(TAG));
        if (getActivity() != null)
            ((DrawerAct) getActivity()).dismissAcceptRejectDialog();
    }

    @Override
    public void gotToTripFragment(RequestModel model) {
        if (getAttachedContext() != null) {
            if (model.request.is_share == 1)
                getAttachedContext().showShareFragment();
            else
                getAttachedContext().showTripFragment(model);
        } else if (listner != null)
            listner.navigatetoTripFrament(model);
    }

    @Override
    public void logoutAppInvalidToken() {
        setCancelable(true);
        if (getDialog() != null)
            getDialog().cancel();
        else
            dismissAllowingStateLoss();
        ((DrawerAct) getContext()).logoutApp();
    }

    @Override
    public void resumeDriverState() {

    }

    @Override
    public void automaticCancelTheTrip() {

    }

    @Override
    public void cancelReasonDialog() {

    }


    public void finishTimer() {
        if (viewmodel != null)
            viewmodel.stopTimer();
    }
}
