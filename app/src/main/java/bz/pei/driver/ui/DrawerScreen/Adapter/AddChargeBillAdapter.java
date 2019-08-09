package bz.pei.driver.ui.DrawerScreen.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import bz.pei.driver.Retro.ResponseModel.RequestModel;
import bz.pei.driver.databinding.ItemAddionalChargesBillBinding;
import bz.pei.driver.ui.Base.BaseViewHolder;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 26/7/18.
 */

public class AddChargeBillAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    List<RequestModel.AdditionalCharge> additionalChargeList;
    Integer selected_id;
    DrawerAct baseActivity;
    String currency;
    public AddChargeBillAdapter(String currency,ArrayList<RequestModel.AdditionalCharge> manageDocModels_,
                                DrawerAct drawerAct) {
        additionalChargeList = manageDocModels_;
        this.currency= currency;
        this.baseActivity = drawerAct;

    }

    public void addServices(ArrayList<RequestModel.AdditionalCharge> manageDocModels_) {
        additionalChargeList = manageDocModels_;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemAddionalChargesBillBinding itemManageDocBinding = ItemAddionalChargesBillBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new AddChargeBillAdapter.ChildViewHolder(itemManageDocBinding);
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

        ItemAddionalChargesBillBinding mBinding;

        private AddChrargeAdapViewModel manageDocAdapViewModel;

        public ChildViewHolder(ItemAddionalChargesBillBinding itm_Bind) {
            super(itm_Bind.getRoot());
            this.mBinding = itm_Bind;
        }

        @Override
        public void onBind(int position) {
            RequestModel.AdditionalCharge manageDocModel = additionalChargeList.get(position);
            manageDocAdapViewModel = new AddChrargeAdapViewModel(currency, manageDocModel, baseActivity);
            mBinding.setViewModel(manageDocAdapViewModel);
            mBinding.executePendingBindings();
        }

    }
    public void selectedItem(Integer s_id) {
        selected_id = s_id;
        notifyDataSetChanged();
    }
}