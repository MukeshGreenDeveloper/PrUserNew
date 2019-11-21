package bz.pei.driver.ui.splash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.google.firebase.iid.FirebaseInstanceId;
import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.databinding.ActivitySplashBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.ui.login.loginviaotp.LoginOTPActivity;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

import static bz.pei.driver.utilz.Constants.Array_permissions;
import static bz.pei.driver.utilz.Constants.REQUEST_PERMISSION;

public class SplashActivity extends BaseActivity<ActivitySplashBinding, SplashViewModel> implements SplashNavigator {

    @Inject
    SplashViewModel emptyViewModel;
    @Inject
    SharedPrefence sharedPrefence;
    ActivitySplashBinding splashBinding;

    AppUpdater appUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashBinding = getViewDataBinding();
        currentActivty = getClass().getName();
        appUpdater = new AppUpdater(this).setDisplay(Display.DIALOG);
        appUpdater.start();
        configureLanguage();
        Setup();
    }


    private void openDrawerActivity() {
        startActivity(new Intent(this, DrawerAct.class));
        finish();
    }

    @Override
    public SplashViewModel getViewModel() {
        return emptyViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    private void Setup() {
        emptyViewModel.setNavigator(this);
        if (CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE))) {
            sharedPrefence.savevalue(SharedPrefence.LANGUANGE, SharedPrefence.EN);
        }
        emptyViewModel.getLanguagees();
        getGCMDeviceToken();
//        new Handler().postDelayed(runnable, 3000);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGranted(Array_permissions)) {
                requestPermissionsSafely(Array_permissions, REQUEST_PERMISSION);
            } else {
                initiateNaviagation();
            }
        }

    };

    private void alertCalling() {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.Alert_title_Permission))
                .setCancelable(false)
                .setMessage(getString(R.string.Alert_Msg_Location))
                .setPositiveButton(getString(R.string.Txt_Continue), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requestPermissionsSafely(Array_permissions, REQUEST_PERMISSION);

                    }
                })
                .setNegativeButton(getString(R.string.Txt_Exit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();
        if (!isFinishing())
            ad.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == REQUEST_PERMISSION) && checkGranted(grantResults)) {
            /*if (checkInternetEnabled() && checkLocationorGPSEnabled()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }*/
            initiateNaviagation();
        } else {
            alertCalling();
        }
    }

    public void getGCMDeviceToken() {
        if (CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.DEVICE_TOKEN))) {
            String token = FirebaseInstanceId.getInstance().getToken();
            if (!CommonUtils.IsEmpty(token)) {
                sharedPrefence.savevalue(SharedPrefence.DEVICE_TOKEN, token);
                Log.e("RefreshToken", token);
            }
        } else {
            Log.e("RefreshTokenOLD", sharedPrefence.Getvalue(SharedPrefence.DEVICE_TOKEN));
        }
    }

    BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                checkLocationorGPSEnabled();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(locationReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(locationReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternetEnabled();
        checkLocationorGPSEnabled();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (appUpdater != null)
            appUpdater.stop();
    }

    private void initiateNaviagation() {
        if (checkInternetEnabled() && checkLocationorGPSEnabled())
            if (!TextUtils.isEmpty(sharedPrefence.Getvalue(SharedPrefence.DRIVERERDETAILS))) {
                openDrawerActivity();
            } else {
                startActivity(new Intent(SplashActivity.this, LoginOTPActivity.class));
                finish();
            }
    }

    @Override
    public Context getAttachedContext() {
        return this;
    }

    @Override
    public void startRequestingPermissions() {
        new Handler().postDelayed(runnable, 100);
    }
}
