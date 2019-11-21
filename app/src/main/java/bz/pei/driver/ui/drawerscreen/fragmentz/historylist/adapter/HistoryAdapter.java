package bz.pei.driver.ui.drawerscreen.fragmentz.historylist.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import bz.pei.driver.retro.responsemodel.HistoryModel;
import bz.pei.driver.databinding.HistoryItemBinding;
import bz.pei.driver.databinding.PaginationLoadingBinding;
import bz.pei.driver.ui.Base.BaseViewHolder;
import bz.pei.driver.ui.drawerscreen.fragmentz.historylist.HistoryListFrag;
import bz.pei.driver.ui.history.HistoryActivity;
import bz.pei.driver.utilz.Constants;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by root on 1/4/18.
 */

public class HistoryAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    List<HistoryModel> histories;
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private HistoryListFrag activity;
    public HistoryAdapter(ArrayList<HistoryModel> histories, HistoryListFrag activity) {
        this.histories = histories;
        this.activity=activity;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM:
                HistoryItemBinding fragmentHistoryItemBinding = HistoryItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new ChildHistoryViewHolder(fragmentHistoryItemBinding);

            case LOADING:
            default:
                PaginationLoadingBinding paginationLoadingBinding = PaginationLoadingBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new PaginationViewHolder(paginationLoadingBinding);

        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }


    @Override
    public int getItemCount() {
        return histories == null ? 0 : histories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == histories.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    private class PaginationViewHolder extends BaseViewHolder {
        public PaginationViewHolder(PaginationLoadingBinding paginationLoadingBinding) {
            super(paginationLoadingBinding.getRoot());
        }

        @Override
        public void onBind(int position) {

        }
    }

    public void addItem(List<HistoryModel> histories) {
        for (HistoryModel ff : histories) {
            add(ff);
        }
    }

    public void add(HistoryModel r) {
        histories.add(r);
        notifyItemInserted(histories.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new HistoryModel());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = histories.size() - 1;
        if (histories.size()==0)
            return;
        HistoryModel result = getItem(position);

        if (result != null) {
            histories.remove(position);
            notifyItemRemoved(position);
        }
    }

    public HistoryModel getItem(int position) {
        return histories.get(position);
    }

    public class ChildHistoryViewHolder extends BaseViewHolder implements ChildHistoryViewModel.ChidItemViewModelListener {

        private HistoryItemBinding mBinding;

        private ChildHistoryViewModel chidlviemodel;

        public ChildHistoryViewHolder(HistoryItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {

            final HistoryModel request = histories.get(position);

            chidlviemodel = new ChildHistoryViewModel(request, this);
            mBinding.setViewModel(chidlviemodel);
            mBinding.executePendingBindings();

        }


        @Override
        public void onItemClick(HistoryModel request) {
            Intent intent = new Intent(itemView.getContext(), HistoryActivity.class);
            intent.putExtra(Constants.IntentExtras.REQUEST_DATA, request.id + "");
            activity.startActivityForResult(intent,Constants.setResult.HISTORY_RESULTS);
        }
    }
}
