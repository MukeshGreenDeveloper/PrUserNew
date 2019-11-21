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

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import bz.pei.driver.App.MyApp;
import bz.pei.driver.FCM.MyFirebaseMessagingService;
import bz.pei.driver.R;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.ui.acceptreject.AcceptRejectActivity;
import bz.pei.driver.ui.drawerscreen.AcceptRejectRespondListener;
import bz.pei.driver.ui.drawerscreen.dialog.acceptrejectdialog.AcceptRejectDialogFragment;
import bz.pei.driver.ui.drawerscreen.dialog.approval.ApprovalFragment;
import bz.pei.driver.ui.drawerscreen.dialog.ridelistdialog.RideListDialogFragment;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.ui.drawerscreen.fragmentz.feedback.FeedbackFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.managedocument.ManageDocumentFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.MapFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.sharetrip.ShareTripFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.trip.TripFragment;
import bz.pei.driver.ui.history.HistoryActivity;
import bz.pei.driver.ui.login.loginviaotp.LoginOTPActivity;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.CustomResources;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.Language.LanguageUtil;
import bz.pei.driver.utilz.Language.MyContextWrapper;
import bz.pei.driver.utilz.NetworkUtils;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjection;


/**
 * Created by amitshekhar on 07/07/17.
 */

public abstract class BaseActivity<T extends ViewDataBinding, V> extends AppCompatActivity implements
        BaseFragment.Callback, BaseView, AcceptRejectRespondListener {
    private ProgressDialog mProgressDialog;
    private T mViewDataBinding;
    private V mViewModel;
    public String check;
    public String currentActivty;
    public String currentFragment;
    public RequestModel currentRequestModel;
    public boolean isVisible = false;
    public final int REQUEST_ENABEL_INTERNET = 199, REQUEST_ENABEL_LOCATION = 200;
    public Dialog dialog_location, dialog_internet;
    FragmentManager fragmentManager;
    private CustomResources customResources;
    public DrawerAct.LocationReceiver locatoinReceiverListener;

    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    Gson gson;
    public HashMap<String, String> Bindabledata = new HashMap<>();
    private RequestModel.Request currentShareRequestModel;
    private ShareCancelRide shareCancelRide;
    private AlertDialog dialogMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        if (getIntent().hasExtra(Constants.EXTRA_Data)) {
            Bindabledata = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.EXTRA_Data);
        }
        performDependencyInjection();
        performDataBinding();

    }

    private void performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        this.mViewModel = mViewModel == null ? getViewModel() : mViewModel;
        mViewDataBinding.setVariable(getBindingVariable(), mViewModel);
        mViewDataBinding.executePendingBindings();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkGranted(String[] permissions) {

        for (String per : permissions) {

            if (checkSelfPermission(per) != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkGranted(int[] permissions) {

        for (int per : permissions) {

            if (per != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    /**
     * to reflect changes of Language in All screens if Arabic RTL
     */
    public void changeDiretionLanguage(Context context, Window window) {
//        configureLanguage();
        if (sharedPrefence.Getvalue(SharedPrefence.LANGUANGE).equalsIgnoreCase("ar")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                window.getDecorView().setTextDirection(View.TEXT_DIRECTION_ANY_RTL);

            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                window.getDecorView().setTextDirection(View.TEXT_DIRECTION_LTR);
            }
        }
    }

    public void configureLanguage() {
        if (CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE)))
            sharedPrefence.savevalue(SharedPrefence.LANGUANGE, "en");
        Locale myLocale = new Locale(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE));
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        String localLanguageType = sharedPrefence.Getvalue(SharedPrefence.LANGUANGE);
        if ("ar".equalsIgnoreCase(localLanguageType)) {
            Locale locale = Locale.getDefault();
            locale.setDefault(new Locale("ar"));
            LanguageUtil.changeLanguageType(this, locale);
        } else if (!TextUtils.isEmpty(localLanguageType)) {
            Locale locale = Locale.getDefault();
            locale.setDefault(new Locale(localLanguageType));
            LanguageUtil.changeLanguageType(this, locale);
        } else {
            LanguageUtil.changeLanguageType(this, Locale.ENGLISH);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Log.d("======", "attachBaseContext");
        Locale languageType = LanguageUtil.getLanguageType(this);
        super.attachBaseContext(MyContextWrapper.wrap(newBase, languageType));
    }


    @Override
    protected void onDestroy() {
        if (requestReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(requestReceiver);
        super.onDestroy();
    }

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showMessage(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    public void showMessage(CustomException e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public boolean isNetworkConnected() {
        boolean result = NetworkUtils.isNetworkConnected(getApplicationContext());
        if (!result)
            showMessage(R.string.txt_NoInternet_title);
        return result;
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showLoading() {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(this);
    }

    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    public T getViewDataBinding() {
        return mViewDataBinding;
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
     * @return SharedPrefereance
     */
    public abstract SharedPrefence getSharedPrefence();

    public void performDependencyInjection() {
        AndroidInjection.inject(this);
    }

    @Override
    public void showSnackBar(String message) {
        if (getViewModel() != null && getViewModel() instanceof View) {
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
        Toast.makeText(this, getString(R.string.txt_NoInternet), Toast.LENGTH_SHORT).show();
    }

    public boolean checkPlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode,
                        Constants.PLAY_SERVICES_REQUEST).show();
            } else {
                showMessage(getString(R.string.DeviceNotSupport));
            }
            return false;
        }
        return true;
    }

    public void makeCAll(String phone) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phone.trim()));
        startActivity(callIntent);
    }

    public void makeSMS(String phone) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:" + phone));
        startActivity(sendIntent);
    }

    /**
     * @param shareText of type String if empty the share content is sent as ap download link from play
     *                  else shateText is sent as share content
     */
    public void shareContent(String shareText) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, CommonUtils.IsEmpty(shareText) ? getString(R.string.text_share_content) + getPackageName() : shareText);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.text_share) + " " + getString(R.string.app_name)));
    }


    public void restartLocationUpdate() {
        MyFirebaseMessagingService.cancelNotification(this);
        isAvailable = true;
        MapFragment fragment = (MapFragment) fragmentManager.findFragmentByTag(MapFragment.TAG);
        if (fragment != null)
            if (fragment.isVisible()) {
                fragment.reInitiateLocationListener();
            }
    }

    private AcceptRejectDialogFragment dialogAcceptReject;
//    private ApprovalFragment dialogApproval;


    public boolean isAvailable = true;

    public void openDialog(RequestModel message) {
        if (!isVisible)
            return;
        if (!isAvailable)
            return;
        isAvailable = false;
        if (dialogAcceptReject == null)
            dialogAcceptReject = new AcceptRejectDialogFragment();
        if (!dialogAcceptReject.isVisible()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.IntentExtras.REQUEST_DATA, message);
            dialogAcceptReject.setArguments(bundle);
            if (!dialogAcceptReject.isVisible())
//                fragmentManager.beginTransaction().add(dialogAcceptReject, AcceptRejectDialogFragment.TAG);
                dialogAcceptReject.show(getSupportFragmentManager(), AcceptRejectDialogFragment.TAG);
        }
    }

    public void dismissAcceptRejectDialog() {
        if (!isVisible)
            return;
        if (dialogAcceptReject != null) {
            if (!dialogAcceptReject.isVisible())
                dialogAcceptReject.dismiss();
            if (!dialogAcceptReject.isDetached())
                dialogAcceptReject.onDetach();
            dialogAcceptReject = null;
        }

    }

    public void showApprovalDialog(int stateDrive) {
        currentFragment = ApprovalFragment.TAG;
        if (fragmentManager != null) {
            fragmentManager
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .replace(R.id.Container, ApprovalFragment.newInstance(stateDrive), ApprovalFragment.TAG)
                    .commitAllowingStateLoss();
        }
    }

    public void removeApprovalDialog() {
        if (fragmentManager != null && fragmentManager.findFragmentByTag(ApprovalFragment.TAG) != null)
            fragmentManager.beginTransaction().
                    remove(fragmentManager.findFragmentByTag(ApprovalFragment.TAG))
                    .commitAllowingStateLoss();
    }


    public void showTripFragment(RequestModel model) {
        currentFragment = TripFragment.TAG;
        currentRequestModel = model;
        TripFragment tripFragment = TripFragment.newInstance(model);
        locatoinReceiverListener = tripFragment;
        if (fragmentManager.findFragmentByTag(TripFragment.TAG) != null) {
            for (Fragment fragment : fragmentManager.getFragments()) {
                if (fragment.getTag().equalsIgnoreCase(TripFragment.TAG)) {
                    fragmentManager
                            .beginTransaction()
                            .disallowAddToBackStack()
                            .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                            .replace(R.id.Container, tripFragment, TripFragment.TAG)
                            .commitAllowingStateLoss();
                } else
                    fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
            }
        } else {

            getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.Container, tripFragment, TripFragment.TAG)
                    .commitAllowingStateLoss();
        }
    }


    public void showShareTripFragment(RequestModel.Request model) {

        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .replace(R.id.Container, ShareTripFragment.newInstance(model), ShareTripFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void showManageDocumentFragment() {
        ManageDocumentFragment fragment = null;
        if (getSupportFragmentManager().findFragmentByTag(ManageDocumentFragment.TAG) != null)
            fragment = (ManageDocumentFragment) getSupportFragmentManager().findFragmentByTag(ManageDocumentFragment.TAG);
        if (fragment != null) {
            if (!fragment.isVisible())
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .replace(R.id.Container, ManageDocumentFragment.newInstance(ManageDocumentFragment.TAG, ""), ManageDocumentFragment.TAG)
                        .commit();
            else {
                getSupportFragmentManager().beginTransaction().remove(fragment);
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .add(R.id.Container, ManageDocumentFragment.newInstance(ManageDocumentFragment.TAG, ""), ManageDocumentFragment.TAG)
                        .commit();
            }
        } else
            getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.Container, ManageDocumentFragment.newInstance(ManageDocumentFragment.TAG, ""), ManageDocumentFragment.TAG)
                    .commit();
    }

    public void showMapFragment() {
        boolean isavailable = false;
        currentFragment = MapFragment.TAG;
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.getTag().equalsIgnoreCase(MapFragment.TAG)) {
                isavailable = true;
            } else if (fragment.getTag().equalsIgnoreCase(TripFragment.TAG)) {
                isavailable = true;
            } else if (fragment.getTag().equalsIgnoreCase(FeedbackFragment.TAG)) {
                isavailable = true;
            } else
                fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();//commit();
//            }
        }
        if (!isavailable) {
            sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
            sharedPrefence.saveIntValue(SharedPrefence.USER_ID, Constants.NO_ID);
            sharedPrefence.saveIntValue(SharedPrefence.IS_SHARE, Constants.NO_ID);
            sharedPrefence.saveIntValue(SharedPrefence.TRIP_START, Constants.NO_ID);
            fragmentManager
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.Container, MapFragment.newInstance("", ""), MapFragment.TAG)
                    .commitAllowingStateLoss();
            setTitle(getString(R.string.app_title));

        }

    }

    public void showMapFragment(int activeDriver) {

        boolean isavailable = false;
        currentFragment = MapFragment.TAG;
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.getTag().equalsIgnoreCase(MapFragment.TAG)) {
                isavailable = true;
            }/* else if (fragment.getTag().equalsIgnoreCase(TripFragment.TAG)) {
                isavailable = true;
            } else if (fragment.getTag().equalsIgnoreCase(FeedbackFragment.TAG)) {
                isavailable = true;
            } */ else
                fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();//commit();
//            }
        }
        if (!isavailable) {
            sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
            sharedPrefence.saveIntValue(SharedPrefence.USER_ID, Constants.NO_ID);
            sharedPrefence.saveIntValue(SharedPrefence.IS_SHARE, Constants.NO_ID);
            sharedPrefence.saveIntValue(SharedPrefence.TRIP_START, Constants.NO_ID);
            fragmentManager
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .add(R.id.Container, MapFragment.newInstance(activeDriver), MapFragment.TAG)
                    .commitAllowingStateLoss();
            setTitle(getString(R.string.app_title));

        }

    }

    public void showFeedbackFragment(RequestModel model) {
        currentFragment = FeedbackFragment.TAG;
        for (Fragment fragment : fragmentManager.getFragments()) {
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
        fragmentManager
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, FeedbackFragment.newInstance(model), FeedbackFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void showRefreshedHOme() {
        for (Fragment fragment : fragmentManager.getFragments()) {
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
        fragmentManager
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, MapFragment.newInstance(1), MapFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void showHistoryActivity(String requstID) {
        Intent intent = new Intent(this, HistoryActivity.class);
        intent.putExtra(Constants.IntentExtras.REQUEST_DATA, requstID);
        startActivity(intent);
    }

    public void redirectToNavigation(double destinationLatitude, double destinationLongitude) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + destinationLatitude + "," + destinationLongitude));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }


    public BroadcastReceiver requestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (currentActivty == HistoryActivity.class.getCanonicalName())
                onBackPressed();
            else if (currentActivty == AcceptRejectActivity.class.getCanonicalName()) {
                String intentDAta = intent.getStringExtra(Constants.BroadcastsActions.NEW_REQUEST);
                RequestModel model = null;
                if (intentDAta != null)
                    model = CommonUtils.getSingleObject(intentDAta, RequestModel.class);
                Intent intentBroadcast = new Intent();
                intentBroadcast.setAction(Constants.BroadcastsActions.CANCEL_RECEIVER);
                intentBroadcast.putExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA, model != null ? intent.getStringExtra(Constants.BroadcastsActions.NEW_REQUEST) : getString(R.string.text_trip_canceled));
                LocalBroadcastManager.getInstance(BaseActivity.this).sendBroadcast(intentBroadcast);
            } else if (currentActivty == DrawerAct.class.getCanonicalName())
                if (intent != null) {
                    String intentDAta = intent.getStringExtra(Constants.BroadcastsActions.NEW_REQUEST);
                    if (!CommonUtils.IsEmpty(intentDAta)) {
                        Log.d("keys", "showingMessage:" + intentDAta);
                        RequestModel model = CommonUtils.getSingleObject(intentDAta, RequestModel.class);
                        if (model != null)
                            if (!CommonUtils.IsEmpty(model.successMessage) && (model.successMessage.equalsIgnoreCase("collect_by_cash")
                                    || model.successMessage.equalsIgnoreCase("collect_balance_amount"))) {
                                Toast.makeText(BaseActivity.this, model.message, Toast.LENGTH_SHORT).show();
                            } else if (model.is_cancelled == 1) {
                                if (MyApp.isIsAcceptRejectActivityVisible()) {
                                    Intent intentBroadcast = new Intent();
                                    intentBroadcast.setAction(Constants.BroadcastsActions.CANCEL_RECEIVER);
                                    intentBroadcast.putExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA, intent.getStringExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA));
                                    LocalBroadcastManager.getInstance(BaseActivity.this).sendBroadcast(intentBroadcast);
                                } else {
                                    if (model.is_share == 1) {
                                        if (getSupportFragmentManager().findFragmentByTag(AcceptRejectDialogFragment.TAG) != null) {
                                            AcceptRejectDialogFragment fragment = (AcceptRejectDialogFragment) getSupportFragmentManager().findFragmentByTag(AcceptRejectDialogFragment.TAG);
                                            fragment.finishTimer();
                                        }
                                        if (currentFragment == MapFragment.TAG) {
                                            showRefreshedHOme();
                                            Intent intentBroadcast = new Intent();
                                            intentBroadcast.setAction(Constants.BroadcastsActions.RideFromAdmin);
                                            LocalBroadcastManager.getInstance(BaseActivity.this).sendBroadcast(intentBroadcast);
                                        } else if (currentFragment.equalsIgnoreCase(RideListDialogFragment.TAG))
                                            showShareFragment();
                                    } else {
                                        Intent intentBroadcasts = new Intent();
                                        intentBroadcasts.setAction(Constants.BroadcastsActions.CANCEL_RECEIVER);
                                        intentBroadcasts.putExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA, intent.getStringExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA));
                                        LocalBroadcastManager.getInstance(BaseActivity.this).sendBroadcast(intentBroadcasts);
                                        sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
                                        sharedPrefence.saveIntValue(SharedPrefence.USER_ID, Constants.NO_ID);
                                        sharedPrefence.saveIntValue(SharedPrefence.IS_SHARE, Constants.NO_ID);
                                        sharedPrefence.saveIntValue(SharedPrefence.TRIP_START, Constants.NO_ID);
                                        if (!MyFirebaseMessagingService.isActivityOnForeground(BaseActivity.this))
                                            return;
                                        if (getSupportFragmentManager().findFragmentByTag(AcceptRejectDialogFragment.TAG) != null) {
                                            AcceptRejectDialogFragment fragment = (AcceptRejectDialogFragment) getSupportFragmentManager().findFragmentByTag(AcceptRejectDialogFragment.TAG);
                                            fragment.finishTimer();
                                        }
                                        showRefreshedHOme();
                                        Intent intentBroadcast = new Intent();
                                        intentBroadcast.setAction(Constants.BroadcastsActions.RideFromAdmin);
                                        LocalBroadcastManager.getInstance(BaseActivity.this).sendBroadcast(intentBroadcast);
                                    }
                                }


                            } else if (model.successMessage.equalsIgnoreCase("another_user_loggedin")) {
                                Toast.makeText(getBaseContext(), getString(R.string.text_already_login), Toast.LENGTH_LONG).show();
                                sharedPrefence.savevalue(SharedPrefence.DRIVERERDETAILS, null);
                                sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
                                sharedPrefence.saveIntValue(SharedPrefence.USER_ID, Constants.NO_ID);
                                sharedPrefence.saveIntValue(SharedPrefence.IS_SHARE, Constants.NO_ID);
                                sharedPrefence.saveIntValue(SharedPrefence.TRIP_START, Constants.NO_ID);
                                startActivity(new Intent(BaseActivity.this, LoginOTPActivity.class));
                                finish();
                            } else if (model.request != null) {
                                if (sharedPrefence.getIntvalue(SharedPrefence.LAST_REQUEST_ID) != model.request.id) {
//                                showtheRequest(model);
                                    Intent acceptRejectIntent = new Intent(MyApp.getmContext(), AcceptRejectActivity.class);
                                    acceptRejectIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    acceptRejectIntent.putExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA, intentDAta);
                                    MyApp.getmContext().startActivity(acceptRejectIntent);
                                }
                            }
                    }
                }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        if (requestReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(Constants.BroadcastsActions.NEW_REQUEST);
            LocalBroadcastManager.getInstance(this).registerReceiver(requestReceiver, intentFilter);
        }

        changeDiretionLanguage(this, getWindow());
    }


    public boolean checkLocationorGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            enableLcationDialog();
//            MyFirebaseMessagingService.displayNotificationConnectivity(this,MyApp.getmContext().getString(R.string.text_location_notification_hint));
            return false;
        } else {
            if (dialog_location != null)
                if (dialog_location.isShowing()) {
                    dialog_location.setCancelable(true);
                    dialog_location.dismiss();
                }
            MyFirebaseMessagingService.cancelConnectivityNotification(this);
            return true;
        }
    }

    private void enableLcationDialog() {
        if (dialog_location != null)
            if (!dialog_location.isShowing()) {
                dialog_location.show();
                return;
            } else
                return;

        dialog_location = new Dialog(this);
        dialog_location.setContentView(R.layout.dialog_enable_location);
        if (dialog_location.getWindow() != null) {
            dialog_location.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog_location.findViewById(R.id.btn_exit_locationdialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dialog_location.findViewById(R.id.btn_setting_locationdialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABEL_LOCATION);
            }
        });
        dialog_location.show();
        dialog_location.setCanceledOnTouchOutside(false);
    }

    private void enableInternetDialog() {
        if (dialog_internet != null)
            if (!dialog_internet.isShowing()) {
                dialog_internet.show();
                return;
            } else
                return;

        dialog_internet = new Dialog(this);
        dialog_internet.setContentView(R.layout.dialog_enable_internet);
        if (dialog_internet.getWindow() != null) {
            dialog_internet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog_internet.findViewById(R.id.btn_exit_internetialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dialog_internet.findViewById(R.id.btn_settings_internetdialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), REQUEST_ENABEL_INTERNET);
            }
        });
        dialog_internet.show();
        dialog_internet.setCanceledOnTouchOutside(false);
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    public boolean checkInternetEnabled() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean result = true;
        result = (activeNetworkInfo != null && activeNetworkInfo.isConnected());
        if (!result)
            enableInternetDialog();
        else {
            if (dialog_internet != null)
                if (dialog_internet.isShowing()) {
                    dialog_internet.setCancelable(true);
                    dialog_internet.dismiss();
                }
        }

        return result;
    }

    public BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                checkLocationorGPSEnabled();
            }
        }
    };
    public BroadcastReceiver internetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(ConnectivityManager.CONNECTIVITY_ACTION)) {
                checkInternetEnabled();
            }
        }
    };

    @Override
    public void navigatetoTripFrament(RequestModel requestModel) {
        if (requestModel.request.is_share == 1)
            showShareFragment();
        else
            showTripFragment(requestModel);
    }

    public void showShareFragment() {


        currentFragment = RideListDialogFragment.TAG;
        for (Fragment fragment : fragmentManager.getFragments()) {
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
        fragmentManager
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, RideListDialogFragment.newInstance(), RideListDialogFragment.TAG)
                .commitAllowingStateLoss();
    }


    @Override
    public void updateTripFrament(RequestModel requestModel) {
        showTripFragment(requestModel);

    }


    @Override
    public Resources getResources() {
        return new CustomResources(sharedPrefence, getAssets(), super.getResources().getDisplayMetrics(), super.getResources().getConfiguration());
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }
        return false;
    }

    public void bringAppToFront() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasklist = am.getRunningTasks(10); // Number of tasks you want to get
        if (!tasklist.isEmpty()) {
            int nSize = tasklist.size();
            for (int i = 0; i < nSize; i++) {
                ActivityManager.RunningTaskInfo taskinfo = tasklist.get(i);
                if (taskinfo.topActivity.getPackageName().equals(getPackageName())) {
                    am.moveTaskToFront(taskinfo.id, 0);
                }
            }
        }
    }

    public void openAlert(final Context context, String title, String msg, DialogInterface.OnClickListener listener) {
        if (dialogMessage != null) {
            if (dialogMessage.isShowing())
                dialogMessage.dismiss();
        }
        final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
        dontShowAgain.setText("Do not show again");
        dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPrefence.savebooleanValue(SharedPrefence.POWER_SAVER_DO_NOT_SHOW, isChecked);
            }
        });
        dialogMessage = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setView(dontShowAgain)
                .setPositiveButton("Go to settings", listener)
                .setNegativeButton(android.R.string.cancel, null)
                .show();

    }

    public void setLocatoinReceiverListener(DrawerAct.LocationReceiver listener) {
        this.locatoinReceiverListener = listener;
    }
}

