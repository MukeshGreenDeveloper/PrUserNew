package bz.pei.driver.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import bz.pei.driver.utilz.CommonUtils;

import java.util.List;

public class RecyclerAdapter<T, VM extends ViewDataBinding> extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private Context context;
    private List<T> items;
    private int layoutId;
    private RecyclerCallBack<VM, T> bindingInterface;

    public RecyclerAdapter(Context context, List<T> items, int layoutId, RecyclerCallBack<VM, T> bindingInterface) {
        this.context = context;
        this.items = items;
        this.layoutId = layoutId;
        this.bindingInterface = bindingInterface;
    }

    public void filterList(List<T> filterdCareer) {

    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        VM binding;

        public RecyclerViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        public void bindData(T model) {
            bindingInterface.bindData(binding, model, getAdapterPosition());
        }

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.RecyclerViewHolder holder, int position) {
        T item = items.get(position);
        holder.bindData(item);
    }

    @Override
    public int getItemCount() {
        return CommonUtils.getListSize(items);
    }
}