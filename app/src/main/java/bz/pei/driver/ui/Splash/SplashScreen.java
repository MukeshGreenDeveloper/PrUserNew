package bz.pei.driver.ui.Splash;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.google.firebase.iid.FirebaseInstanceId;
import bz.pei.driver.FCM.MyFirebaseMessagingService;
import bz.pei.driver.R;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;
import bz.pei.driver.ui.Login.LoginViaOTP.LoginOTPActivity;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Language.LanguageUtil;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.List;
import java.util.Locale;

import static bz.pei.driver.utilz.Constants.Array_permissions;
import static bz.pei.driver.utilz.Constants.REQUEST_PERMISSION;

public class SplashScreen extends Activity {

    SharedPrefence sharedPrefence;
    AppUpdater appUpdater;
    public Dialog dialog_location, dialog_internet;
    public final int REQUEST_ENABEL_INTERNET = 199, REQUEST_ENABEL_LOCATION = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeSharedPreferences(this);
        appUpdater = new AppUpdater(this).setDisplay(Display.DIALOG);
        appUpdater.start();
        configureLanguage();
        Setup();
    }

    void initializeSharedPreferences(Context application) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
        sharedPrefence = new SharedPrefence(preferences, preferences.edit());
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
    private void Setup() {
        if (CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE))) {
            sharedPrefence.savevalue(SharedPrefence.LANGUANGE, SharedPrefence.EN);
        }
//        emptyViewModel.getLanguagees();
        getGCMDeviceToken();
        new Handler().postDelayed(runnable, 3000);
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
                startActivity(new Intent(SplashScreen.this, LoginOTPActivity.class));
                finish();
            }
    }
    public void startRequestingPermissions() {
        new Handler().postDelayed(runnable, 100);
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

    private void openDrawerActivity() {
        startActivity(new Intent(this, DrawerAct.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == REQUEST_PERMISSION) && checkGranted(grantResults)) {
            initiateNaviagation();
        } else {
            alertCalling();
        }
    }
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

}
