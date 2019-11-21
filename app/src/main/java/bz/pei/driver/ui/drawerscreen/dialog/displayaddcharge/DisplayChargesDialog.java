package bz.pei.driver.ui.drawerscreen.dialog.displayaddcharge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.databinding.DialogListChageBinding;
import bz.pei.driver.ui.Base.BaseDialog;
import bz.pei.driver.ui.drawerscreen.adapter.AddChargeAdapter;
import bz.pei.driver.ui.drawerscreen.dialog.additonalcharge.AddChargeDialogFragment;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.utilz.Constants;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by root on 12/28/17.
 */

public class DisplayChargesDialog extends BaseDialog<DialogListChageBinding, DisplayChargesViewModel>
        implements DisplayChargesNavigator {
    public static final String TAG = "DisplayChargesDialog";
    DialogListChageBinding binding;
    @Inject
    DisplayChargesViewModel viewModel;
    RequestModel model;
    AddChargeAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;

    public static DisplayChargesDialog newInstance(String param1) {
        DisplayChargesDialog fragment = new DisplayChargesDialog();
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
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new AddChargeAdapter("0", new ArrayList<RequestModel.AdditionalCharge>(), this, (DrawerAct) getActivity());
        binding.recyclerViewList.setAdapter(adapter);
        binding.recyclerViewList.setLayoutManager(mLayoutManager);
        viewModel.getCharges();
    }

    @Override
    public void dismissDialog(String addChage) {
        getDialog().dismiss();
    }

    public void onDeleteClick(RequestModel.AdditionalCharge model) {
        if (model != null && model.id != null)
            viewModel.deleteItem(model.id);
    }

    public void setupList(ArrayList<RequestModel.AdditionalCharge> modelList, String currency) {
        adapter = new AddChargeAdapter(currency,modelList, this, (DrawerAct) getActivity());
        binding.recyclerViewList.setAdapter(adapter);
    }

    @Override
    public Context getAttachedContext() {
        return getBaseActivity();
    }

    @Override
    public DisplayChargesViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void showAdditionalChargedialog() {
        AddChargeDialogFragment.newInstance("").show(getActivity().getSupportFragmentManager(), AddChargeDialogFragment.TAG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.ADD_CHARGE_VALUE:
                if (resultCode == Activity.RESULT_OK) {
                    if (viewModel != null)
                        viewModel.getCharges();
                }
                break;
        }
    }

    @Override
    public DialogListChageBinding getDataBinding() {
        return binding;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_list_chage;
    }
}
