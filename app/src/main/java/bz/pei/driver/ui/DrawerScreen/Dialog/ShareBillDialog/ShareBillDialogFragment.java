package bz.pei.driver.ui.DrawerScreen.Dialog.ShareBillDialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import bz.pei.driver.R;
import bz.pei.driver.Retro.ResponseModel.RequestModel;
import bz.pei.driver.databinding.FragmentShareBillDialogBinding;
import bz.pei.driver.ui.Base.BaseDialog;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;
import bz.pei.driver.utilz.Constants;

import javax.inject.Inject;

public class ShareBillDialogFragment extends BaseDialog<FragmentShareBillDialogBinding, ShareBillDialogViewModel> implements ShareBillDialogNavigator {
    @Inject
    ShareBillDialogViewModel viewModel;
    FragmentShareBillDialogBinding binding;

    RequestModel model;

    public static ShareBillDialogFragment newInstance(RequestModel param1) {
        ShareBillDialogFragment fragment = new ShareBillDialogFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewModel != null) {
            viewModel.setNavigator(this);
            binding = getmBinding();

            setCancelable(false);
            viewModel.setDetails(model);

//            binding.tvName.setText(model.request.user.firstname+" "+model.request.user.lastname);
//            binding.tvPrice.setText(model.request.bill.currency + model.request.bill.total + "");
//binding.setViewModels(viewModel);
//            Glide.with(getActivity()).load(model.request.user.profilePic).
//                    apply(RequestOptions.circleCropTransform().error(R.drawable.ic_user).
//                            placeholder(R.drawable.ic_user)).into(binding.imgProfile);
        }
    }

    @Override
    public ShareBillDialogViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public FragmentShareBillDialogBinding getDataBinding() {
        return binding;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_share_bill_dialog;
    }

    @Override
    public void showShareListScrn() {
        dismiss();

        ((DrawerAct) getActivity()).showShareFragment();
        ((DrawerAct) getBaseActivity()).disableToggleStatusIcon(false);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}