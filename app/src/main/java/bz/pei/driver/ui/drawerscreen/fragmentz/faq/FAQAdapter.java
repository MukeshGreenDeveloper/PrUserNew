package bz.pei.driver.ui.drawerscreen.fragmentz.faq;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import bz.pei.driver.retro.responsemodel.FAQModel;
import bz.pei.driver.databinding.ItemFaqBinding;
import bz.pei.driver.ui.Base.BaseViewHolder;

import java.util.List;


/**
 * Created by root on 1/4/18.
 */

public class FAQAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    List<FAQModel> faqModelList;

    public FAQAdapter(List<FAQModel> faqModelList) {
        this.faqModelList = faqModelList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemFaqBinding fragmentHistoryItemBinding = ItemFaqBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ChildHistoryViewHolder(fragmentHistoryItemBinding);

    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }


    @Override
    public int getItemCount() {
        return faqModelList == null ? 0 : faqModelList.size();
    }


    public void addItem(List<FAQModel> faqModelList) {
        for (FAQModel ff : faqModelList) {
            this.faqModelList.add(ff);
        }
    }


    public FAQModel getItem(int position) {
        return faqModelList.get(position);
    }

    public class ChildHistoryViewHolder extends BaseViewHolder {
        private ItemFaqBinding mBinding;
        private FAQModel model;

        public ChildHistoryViewHolder(ItemFaqBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {

            model = faqModelList.get(position);
            mBinding.setViewModel(model);
            mBinding.executePendingBindings();

        }

    }
}
