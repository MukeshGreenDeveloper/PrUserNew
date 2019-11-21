/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package bz.pei.driver.ui.Base;

import android.app.Dialog;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import bz.pei.driver.R;
import bz.pei.driver.utilz.Exception.CustomException;

import dagger.android.support.AndroidSupportInjection;

/**
 * Created by amitshekhar on 10/07/17.
 */

public abstract class ButtomSheetBaseDialog<T extends ViewDataBinding, V> extends DialogFragment implements BaseView {


    private BaseActivity mActivity;
    private V mViewModel;
    private T mBinding;
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mRootView = mBinding.getRoot();
        return mRootView;
    }



    public T getmBinding() {
        return mBinding;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity mActivity = (BaseActivity) context;
            this.mActivity = mActivity;
            mActivity.onFragmentAttached();
        }
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        performDependencyInjection();
        Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    private void performDependencyInjection() {
        AndroidSupportInjection.inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = getViewModel();
        mBinding.setVariable(getBindingVariable(), mViewModel);
        mBinding.executePendingBindings();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showLoading() {
        if (mActivity != null) {
            mActivity.showLoading();
        }
    }

    public void hideLoading() {
        if (mActivity != null) {
            mActivity.hideLoading();
        }
    }

    public boolean isNetworkConnected() {
        boolean result = mActivity.isNetworkConnected();
        if (!result)
            showMessage(mActivity.getString(R.string.txt_NoInternet_title));
        return mActivity != null && mActivity.isNetworkConnected();
    }

    public void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();
        }
    }


    public void show(FragmentManager fragmentManager, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment prevFragment = fragmentManager.findFragmentByTag(tag);
        if (prevFragment != null) {
            transaction.remove(prevFragment);
        }
//        transaction.commitAllowingStateLoss();
        transaction.addToBackStack(null);
        show(transaction, tag);
    }

    private boolean isSavedInstanceState;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    public void dismissDialog(String tag) {
        dismiss();
        getBaseActivity().onFragmentDetached(tag);
    }

    public void showMessage(int resId) {
        Toast.makeText(mActivity, getString(resId), Toast.LENGTH_SHORT).show();
    }

    public void showMessage(CustomException e) {
        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
    }


    public void showMessage(String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }


    public void showSnackBar(String message) {
        if (mBinding == null) {
            mBinding = getDataBinding();
        }
        if (mBinding != null) {
            Snackbar snackbar = Snackbar.make(mBinding.getRoot(), message, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }


    public void showSnackBar(@NonNull View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    public void showNetworkMessage() {
        Toast.makeText(mActivity, getString(R.string.txt_NoInternet), Toast.LENGTH_SHORT).show();
    }

    public abstract V getViewModel();

    public abstract T getDataBinding();


    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * @return layout resource id
     */
    public abstract
    @LayoutRes
    int getLayoutId();

}
