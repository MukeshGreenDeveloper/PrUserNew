package bz.pei.driver.ui.drawerscreen.dialog.billdialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.databinding.BillLayoutNewBinding;
import bz.pei.driver.ui.Base.BaseDialog;
import bz.pei.driver.ui.drawerscreen.adapter.AddChargeBillAdapter;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.utilz.Constants;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by root on 12/28/17.
 */

public class BillDialogFragment extends BaseDialog<BillLayoutNewBinding, BillDialogViewModel> implements BillDialogNavigator {
    public static final String TAG = "BillDialogFragment";
    BillLayoutNewBinding binding;
    @Inject
    BillDialogViewModel viewModel;
    RequestModel model;
    AddChargeBillAdapter adapter;
    LinearLayoutManager mLayoutManager;
    public static BillDialogFragment newInstance(RequestModel param1) {
        BillDialogFragment fragment = new BillDialogFragment();
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
        viewModel.setNavigator(this);
        setDialogFullSCreen();
        viewModel.setBillDetails(model);
        binding = getmBinding();
        binding.setViewModel(viewModel);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        if(model.request!=null&&model.request.bill!=null&&model.request.bill.additionalCharge!=null) {
            binding.recyclerAddCharges.setLayoutManager(mLayoutManager);
            adapter = new AddChargeBillAdapter(model.request.bill.currency,(ArrayList<RequestModel.AdditionalCharge>) model.request.bill.additionalCharge, (DrawerAct) getActivity());
            Log.d("keys","size--"+model.request.bill.additionalCharge.size());
            binding.recyclerAddCharges.setAdapter(adapter);
            viewModel.isAddnlChargeAvailable.set(model.request.bill.additionalCharge.size()>0);
        }else{
            viewModel.isAddnlChargeAvailable.set(false);
        }
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
    public BillDialogViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public BillLayoutNewBinding getDataBinding() {
        return binding;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.bill_layout_new;
    }
}
