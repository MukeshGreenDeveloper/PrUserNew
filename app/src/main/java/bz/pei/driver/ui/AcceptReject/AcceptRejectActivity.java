package bz.pei.driver.ui.AcceptReject;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.WindowManager;

import bz.pei.driver.App.MyApp;
import bz.pei.driver.BR;
import bz.pei.driver.FCM.MyFirebaseMessagingService;
import bz.pei.driver.R;
import bz.pei.driver.Retro.ResponseModel.RequestModel;
import bz.pei.driver.databinding.OverlayAcceptRejectLayoutBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.DrawerScreen.Dialog.AcceptRejectDialog.AcceptRejectDialogViweModel;
import bz.pei.driver.ui.DrawerScreen.Dialog.AcceptRejectDialog.AcceptRejectNavigator;
import bz.pei.driver.ui.DrawerScreen.Dialog.CancelDialog.CancelDialogFragment;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;
import bz.pei.driver.ui.Login.LoginViaOTP.LoginOTPActivity;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by root on 10/9/17.
 */

public class AcceptRejectActivity extends BaseActivity<OverlayAcceptRejectLayoutBinding,
        AcceptRejectDialogViweModel> implements AcceptRejectNavigator, HasSupportFragmentInjector {
    RequestModel requestModel;
    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    AcceptRejectDialogViweModel viweModel;
    OverlayAcceptRejectLayoutBinding binding;
    public static final int TRIP_CANCL_CODE = 1000;
    public final String TAG = "AcceptRejectActivity";
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivty = getClass().getName();
        binding = getViewDataBinding();
        viweModel.setNavigator(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (getIntent() != null)
            if (getIntent().getStringExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA) != null) {
                requestModel = CommonUtils.getSingleObject(getIntent().getStringExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA), RequestModel.class);
                if (requestModel != null && requestModel.request != null && requestModel.request.user != null)
                    if (viweModel != null)
                        viweModel.setRequestDetails(requestModel, this);
            }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(cancelReceiver, new IntentFilter(Constants.BroadcastsActions.CANCEL_RECEIVER));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(cancelReceiver);
    }

    @Override
    public AcceptRejectDialogViweModel getViewModel() {
        return viweModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModels;
    }


    @Override
    public int getLayoutId() {
        return R.layout.overlay_accept_reject_layout;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }


    @Override
    public void resumeDriverState() {
        dismissDialog();
    }

    @Override
    public BaseActivity getAttachedContext() {
        return this;
    }

    @Override
    public void dismissDialog() {
        MyFirebaseMessagingService.cancelNotification(this);
        finish();
        if (getApplication() != null)
            if (((MyApp) getApplication()).isIsDrawerActivityDestroyed()) {
                Intent dialogIntent = new Intent(this, DrawerAct.class);
                dialogIntent.setAction(Intent.ACTION_VIEW);
                dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(dialogIntent);
            } else {
                if (!((MyApp) getApplication()).isIsDrawerActivityVisible()) {
                    bringAppToFront();
                }
            }
    }

    @Override
    public void gotToTripFragment(RequestModel model) {
        MyFirebaseMessagingService.cancelNotification(this);
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(Constants.BroadcastsActions.RideFromAdmin);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
        if (getApplication() != null)
            if (((MyApp) getApplication()).isIsDrawerActivityDestroyed()) {
                Intent dialogIntent = new Intent(this, DrawerAct.class);
                dialogIntent.setAction(Intent.ACTION_VIEW);
                dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(dialogIntent);
            } else {
                if (!((MyApp) getApplication()).isIsDrawerActivityVisible()) {
                    bringAppToFront();
                }
            }

    }


    @Override
    public void logoutAppInvalidToken() {
        sharedPrefence.savevalue(SharedPrefence.DRIVERERDETAILS, null);
        sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
        sharedPrefence.saveIntValue(SharedPrefence.USER_ID, Constants.NO_ID);
        sharedPrefence.saveIntValue(SharedPrefence.IS_SHARE, Constants.NO_ID);
        sharedPrefence.saveIntValue(SharedPrefence.TRIP_START, Constants.NO_ID);
        startActivity(new Intent(this, LoginOTPActivity.class));
        finish();
    }

    @Override
    public void cancelReasonDialog() {
        CancelDialogFragment fragment = CancelDialogFragment.newInstance(CancelDialogFragment.TAG);
        if (getSupportFragmentManager() != null)
            fragment.show(getSupportFragmentManager(), CancelDialogFragment.TAG);
    }

    @Override
    public void automaticCancelTheTrip() {
        viweModel.confirmCanecl("cancel");
    }

    public void bringAppToFront() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasklist = am.getRunningTasks(10); // Number of tasks you want to get
        if (!tasklist.isEmpty()) {
            int nSize = tasklist.size();
            for (int i = 0; i < nSize; i++) {
                ActivityManager.RunningTaskInfo taskinfo = tasklist.get(i);
                if (taskinfo.topActivity.getClassName().equalsIgnoreCase(DrawerAct.class.getPackage().getName() + "." + DrawerAct.class.getSimpleName())) {
                    am.moveTaskToFront(taskinfo.id, 0);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TRIP_CANCL_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "CancelReason:" + data.getExtras().get(Constants.IntentExtras.CANCEL_REASON));
                    if (!CommonUtils.IsEmpty(data.getExtras().getString(Constants.IntentExtras.CANCEL_REASON)))
                        if (viweModel != null)
                            viweModel.confirmCanecl(data.getExtras().getString(Constants.IntentExtras.CANCEL_REASON));
                }
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    BroadcastReceiver cancelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (viweModel != null) {
                viweModel.stopTimer();
                if (intent != null && intent.getStringExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA) != null && viweModel.requestDetails != null && viweModel.requestDetails.request != null) {
                    RequestModel.Request requestModel = CommonUtils.getSingleObject(intent.getStringExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA), RequestModel.Request.class);
                    if (requestModel != null && requestModel.is_cancelled >= 0
                            && requestModel.requestId != null
                            && viweModel.requestDetails.request.id == Integer.parseInt(requestModel.requestId)) {
                        if (!CommonUtils.IsEmpty(requestModel.message)&&sharedPrefence.getIntvalue(SharedPrefence.LAST_REQUEST_ID) != Integer.parseInt(requestModel.requestId))
                            showMessage(requestModel.message);

                        sharedPrefence.saveIntValue(SharedPrefence.LAST_REQUEST_ID, viweModel.requestDetails.request.id);
                        sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
                        dismissDialog();
                    }
                }
            }

        }
    };

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viweModel != null)
            viweModel.finishTimer();
    }
}
