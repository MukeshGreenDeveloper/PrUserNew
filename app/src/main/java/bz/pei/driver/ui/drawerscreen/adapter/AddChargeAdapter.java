package bz.pei.driver.ui.drawerscreen.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.databinding.ItemAddChargesBinding;
import bz.pei.driver.ui.Base.BaseViewHolder;
import bz.pei.driver.ui.drawerscreen.dialog.displayaddcharge.DisplayChargesDialog;
import bz.pei.driver.ui.drawerscreen.DrawerAct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 26/7/18.
 */

public class AddChargeAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    List<RequestModel.AdditionalCharge> additionalChargeList;
    DisplayChargesDialog manageDocFragment;
    Integer selected_id;
    String currency="";
    DrawerAct baseActivity;

    public AddChargeAdapter(String currency, ArrayList<RequestModel.AdditionalCharge> manageDocModels_,
                            DisplayChargesDialog displayChargesDialog,
                            DrawerAct drawerAct) {
        additionalChargeList = manageDocModels_;
        this.manageDocFragment = displayChargesDialog;
        this.currency= currency;
        this.baseActivity = drawerAct;

    }

    public void addServices(ArrayList<RequestModel.AdditionalCharge> manageDocModels_) {
        additionalChargeList = manageDocModels_;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemAddChargesBinding itemManageDocBinding = ItemAddChargesBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new AddChargeAdapter.ChildViewHolder(itemManageDocBinding);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return additionalChargeList.size();
    }

    public void addList(List<RequestModel.AdditionalCharge> manageDocModels) {
//        itemManageDocListener = manageDocFragment;
        additionalChargeList.clear();
        additionalChargeList.addAll(manageDocModels);
        notifyDataSetChanged();
    }


    public class ChildViewHolder extends BaseViewHolder {

        ItemAddChargesBinding mBinding;

        private AddChrargeAdapViewModel manageDocAdapViewModel;

        public ChildViewHolder(ItemAddChargesBinding itm_Bind) {
            super(itm_Bind.getRoot());
            this.mBinding = itm_Bind;
        }

        @Override
        public void onBind(int position) {
            RequestModel.AdditionalCharge manageDocModel = additionalChargeList.get(position);
            manageDocAdapViewModel = new AddChrargeAdapViewModel(currency,manageDocModel, manageDocFragment, baseActivity);
            mBinding.setViewModel(manageDocAdapViewModel);
            mBinding.executePendingBindings();
        }

    }

    public interface ItemManageDocListener {
        public void ManageDocChoose(RequestModel.AdditionalCharge manageDocModel);
    }

    public void selectedItem(Integer s_id) {
        selected_id = s_id;
        notifyDataSetChanged();
    }
}