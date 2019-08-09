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

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import bz.pei.driver.R;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

import dagger.android.support.AndroidSupportInjection;

/**
 * Created by amitshekhar on 09/07/17.
 */

public abstract class BaseFragment<T extends ViewDataBinding, V> extends Fragment implements BaseView {

    private BaseActivity mActivity;
    private Context mcontext;
    private T mViewDataBinding;
    private V mViewModel;
    private View mRootView;
    private SharedPrefence sharedPrefence;
    public HashMap<String, String> Bindabledata = new HashMap<>();

/*
    @Override
    public void onAttachFragment(Fragment childFragment) {
        performDependencyInjection();
        super.onAttachFragment(childFragment);
    }
*/

    @Override
    public void onAttach(Activity activity) {
        performDependencyInjection();
        super.onAttach(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mRootView = mViewDataBinding.getRoot();

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPrefence = getSharedPrefence();
        mViewModel = getViewModel();
        mViewDataBinding.setVariable(getBindingVariable(), mViewModel);
        mViewDataBinding.executePendingBindings();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext = context;
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
            activity.onFragmentAttached();
        }
    }

    @Override
    public void onDetach() {
        mActivity = null;
        mcontext = null;
        super.onDetach();
    }

    @Override
    public Context getContext() {
        return mcontext;
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    public T getViewDataBinding() {
        return mViewDataBinding;
    }


    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    public boolean isNetworkConnected() {
        if (mActivity == null || getActivity() == null)
            mActivity = (BaseActivity) getActivity();
        if (mActivity == null)
            return false;
        return mActivity.isNetworkConnected();
    }

    public void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();

        }
    }


    public void openActivityOnTokenExpire() {
        if (mActivity != null) {
            //   mActivity.openActivityOnTokenExpire();
        }
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

    @Override
    public void showSnackBar(String message) {
        if (getViewModel() != null) {
            Snackbar snackbar = Snackbar.make((View) getViewModel(), message, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    @Override
    public void showSnackBar(@NonNull View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    @Override
    public void showNetworkMessage() {
        Toast.makeText(mActivity, getString(R.string.txt_NoInternet), Toast.LENGTH_SHORT).show();
    }

    public boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(getBaseActivity());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(getBaseActivity(), resultCode,
                        Constants.PLAY_SERVICES_REQUEST).show();
            } else {


                showMessage(getString(R.string.DeviceNotSupport));


            }
            return false;
        }
        return true;
    }

   public boolean isGoogleMapsInstalled(Activity activity) {
        if(mActivity==null)
            mActivity= (BaseActivity) activity;
        try {
            ApplicationInfo info = mActivity.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void performDependencyInjection() {
        AndroidSupportInjection.inject(this);
    }

    public interface Callback {

        void onFragmentAttached();

        void onFragmentDetached(String tag);
    }

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract V getViewModel();

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

    /**
     * @return screen Sharedpreference
     */
    public abstract SharedPrefence getSharedPrefence();

}
