package bz.pei.driver.adapter;

import android.databinding.ViewDataBinding;

public interface RecyclerCallBack<VM extends ViewDataBinding, T> {
    public void bindData(VM binder, T model, int position);
}