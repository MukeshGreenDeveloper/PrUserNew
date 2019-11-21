package bz.pei.driver.ui.drawerscreen.fragmentz.sos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bz.pei.driver.retro.responsemodel.SosModel;
import bz.pei.driver.databinding.ItemSosBinding;
import bz.pei.driver.ui.Base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 12/12/17.
 */

public class SosRecylerAdapter extends RecyclerView.Adapter<SosRecylerAdapter.ChildViewHolder> {
    List<SosModel> types = new ArrayList<>();
    Context context;

    public SosRecylerAdapter(Context context, List<SosModel> types) {
        this.types = types;
        this.context = context;
    }

    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemSosBinding sosBinding = ItemSosBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChildViewHolder(sosBinding);
    }

    @Override
    public void onBindViewHolder(ChildViewHolder holder, int position) {
        SosModel model = types.get(position);
        if (model != null) {
            holder.bind(model);
        }
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        private final ItemSosBinding binding;

        public ChildViewHolder(ItemSosBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SosModel item) {
            binding.setEmergencyName(item.name);
            binding.setEmergencyNumber(item.number);
            binding.cardItemSos.setTag(item.number);
            binding.cardItemSos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getTag() != null) {
                        ((BaseActivity) context).makeCAll((String) view.getTag());
                    }
                }
            });
            binding.executePendingBindings();
        }

    }
}
