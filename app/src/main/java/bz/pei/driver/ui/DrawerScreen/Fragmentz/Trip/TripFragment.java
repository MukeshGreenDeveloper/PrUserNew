package bz.pei.driver.ui.DrawerScreen.Fragmentz.Trip;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import bz.pei.driver.BR;
import bz.pei.driver.FCM.MyFirebaseMessagingService;
import bz.pei.driver.R;
import bz.pei.driver.Retro.ResponseModel.RequestModel;
import bz.pei.driver.databinding.FragmentJobBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.AdditonalCharge.AddChargeDialogFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.CancelDialog.CancelDialogFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.DisplayAddCharge.DisplayChargesDialog;
import bz.pei.driver.ui.DrawerScreen.Dialog.WaitingDialog.WaitingDialogFragment;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Location.BackgroundLocationUpdateService;
import bz.pei.driver.utilz.Location.LocationUpdatesService;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 12/21/17.
 */

public class TripFragment extends BaseFragment<FragmentJobBinding, TripViewModel> implements TripNavigator,DrawerAct.LocationReceiver {

    public static final String TAG = "TripFragment";
    BaseActivity activity;
    @Inject
    TripViewModel tripViewModel;
    @Inject
    SharedPrefence sharedPrefence;
    FragmentJobBinding binding;
    private RequestModel requestModel;
    public final int TRIP_WAIT_CODE = 101;
    public final int TRIP_CANCL_CODE = 102;
    public int headerHeight, footerHeight;

    public static TripFragment newInstance(RequestModel param1) {
        TripFragment fragment = new TripFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.IntentExtras.REQUEST_DATA, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestModel = getArguments().getParcelable(Constants.IntentExtras.REQUEST_DATA);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        binding.mapViewVew.onCreate(savedInstanceState);
        tripViewModel.setNavigator(this);
        binding.mapViewVew.getMapAsync(tripViewModel);
        Setup();
        binding.footerTripfragment.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT > 16) {
                    binding.headerTripfragment.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    binding.footerTripfragment.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    binding.headerTripfragment.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    binding.footerTripfragment.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                System.out.println("+FRBottomlayout+" + binding.headerTripfragment.getMeasuredHeight());
                if (tripViewModel != null)
                    headerHeight = binding.headerTripfragment.getMeasuredHeight();
                footerHeight = binding.footerTripfragment.getMeasuredHeight();
            }
        });
        ((DrawerAct) getBaseActivity()).disableToggleStatusIcon(false);
    }

    @Override
    public List<Integer> getMapPadding() {
        return new ArrayList<Integer>(Arrays.asList(headerHeight, footerHeight));
    }

    private void Setup() {

        if (activity == null)
            activity = (BaseActivity) getActivity();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (checkPlayServices()) {
            tripViewModel.buildGoogleApiClient();
            if (requestModel != null)
                tripViewModel.setUpTripDetails(requestModel);
        } else {
            getBaseActivity().finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        tripViewModel.startLocationUpdate();
    }

    @Override
    public void onResume() {
        super.onResume();
        getBaseActivity().setTitle(R.string.app_title);
        binding.mapViewVew.onResume();
        stopJobLocationUpdate();
        try {
//            ((DrawerAct) getBaseActivity()).setupProfileDAta();
            MyFirebaseMessagingService.cancelNotification(getBaseActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapViewVew.onPause();
        startJobLocationUpdate();
    }

    @Override
    public void onStop() {
        super.onStop();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.mapViewVew.onDestroy();
        tripViewModel.disconnectGoogleApiClient();
        tripViewModel.offSocket();
        tripViewModel.stopLocationUpdate();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapViewVew.onLowMemory();
    }

    @Override
    public TripViewModel getViewModel() {
        return tripViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_job;
    }

    @Override
    public Context getApplicationContext() {
        return getContext();
    }

    @Override
    public void openMapFragment() {//Null at here
        if(getBaseActivity()!=null)
        getBaseActivity().showRefreshedHOme();
        if(getActivity()!=null)
        ((DrawerAct) getActivity()).resumeDriverState();
    }

    @Override
    public void openFeedbackFragment(RequestModel model) {
        if (getBaseActivity() != null)
            getBaseActivity().showFeedbackFragment(model);
    }

    @Override
    public void openCallDialer(String phoneNUmber) {
        getBaseActivity().makeCAll(phoneNUmber);
    }

    @Override
    public void openSmsDrafter(String user_phoneNumber) {
        getBaseActivity().makeSMS(user_phoneNumber);
    }

    @Override
    public void openGoogleMap(Double destLat, Double destLng) {
        if (!isGoogleMapsInstalled(getActivity()))
            showMessage(getActivity().getString(R.string.text_googleMap));
        else
            getBaseActivity().redirectToNavigation(destLat, destLng);
    }

    @Override
    public void openWaitingDialog(int time, int waiting_timeSec) {

        WaitingDialogFragment fragment = WaitingDialogFragment.newInstance(waiting_timeSec, time);
        fragment.setTargetFragment(this, TRIP_WAIT_CODE);
        fragment.show(getBaseActivity().getSupportFragmentManager(), WaitingDialogFragment.TAG);
    }
    @Override
    public BitmapDescriptor getMarkerIcon(int drawID) {
        return BitmapDescriptorFactory.fromBitmap(CommonUtils.getBitmapFromDrawable(getActivity(), drawID));
    }

    @Override
    public void navigateCancelDialog() {
        CancelDialogFragment fragment = CancelDialogFragment.newInstance(CancelDialogFragment.TAG);
        fragment.setTargetFragment(this, TRIP_CANCL_CODE);
        fragment.show(getBaseActivity().getSupportFragmentManager(), CancelDialogFragment.TAG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TRIP_WAIT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "WaitingTime:" + data.getExtras().get(Constants.IntentExtras.WAITING_TIME));
                    if ((Integer) data.getExtras().get(Constants.IntentExtras.WAITING_TIME) > 0)
                        if (tripViewModel != null) {
                            tripViewModel.setWaitingTime((Integer) data.getExtras().get(Constants.IntentExtras.WAITING_TIME),
                                    (Integer) data.getExtras().get(Constants.IntentExtras.PREVAILING_WAITING_SEC));
                        }
                }
                break;
            case Constants.ADD_CHARGE_VALUE:
                if (resultCode == Activity.RESULT_OK) {

                    if (data != null && data.hasExtra(Constants.IntentExtras.ADD_CHARGE_VALUES)) {
                        tripViewModel.processEndTrip(data.getStringExtra(Constants.IntentExtras.ADD_CHARGE_VALUES));
                    } else {
                        tripViewModel.processEndTrip("0");
                    }

                }
                break;
            case TRIP_CANCL_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "CancelReason:" + data.getExtras().get(Constants.IntentExtras.CANCEL_REASON));
                    if (!CommonUtils.IsEmpty(data.getExtras().getString(Constants.IntentExtras.CANCEL_REASON)))
                        if (tripViewModel != null)
                            tripViewModel.confirmCanecl(data.getExtras().getString(Constants.IntentExtras.CANCEL_REASON));
                }

                break;
        }
    }

    @Override
    public void showAddionalChargeDialog() {
        AddChargeDialogFragment fragment = AddChargeDialogFragment.newInstance(AddChargeDialogFragment.TAG);
        fragment.show(getBaseActivity().getSupportFragmentManager(), AddChargeDialogFragment.TAG);
    }

    public void startJobLocationUpdate() {
//        LocalBroadcastManager.getInstance(getBaseActivity()).registerReceiver(
//                locationBroadReceiver, new IntentFilter(Constants.BroadcastsActions.LOCATION_UPDATING_SERVICE));
    }

    public void stopJobLocationUpdate() {
//        LocalBroadcastManager.getInstance(getBaseActivity()).unregisterReceiver(
//                locationBroadReceiver);
//        if (CommonUtils.isMyServiceRunning(getBaseActivity(), LocationUpdatesService.class)) {
//            ((DrawerAct) getActivity()).removeLocationUpdate();
//        }
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    BroadcastReceiver locationBroadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("keys", "On New Locaiton Received");
            Location location = intent.getParcelableExtra(BackgroundLocationUpdateService.TAG);
            if (location != null) {
                if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0)
                    if (tripViewModel != null)
                        tripViewModel.onLocationChanged(location);
            }
        }
    };


    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public void listAddnlCharge() {
        DisplayChargesDialog fragment = DisplayChargesDialog.newInstance(DisplayChargesDialog.TAG);
        fragment.show(getBaseActivity().getSupportFragmentManager(), DisplayChargesDialog.TAG);
    }

    @Override
    public void passLatlng(String lat, String lng, String bearing, String driverId) {
        if (tripViewModel != null&& !TextUtils.isEmpty(lat)&&!TextUtils.isEmpty(lng)&&!TextUtils.isEmpty(bearing)&&!TextUtils.isEmpty(driverId)) {
            Location location=new Location("");
            location.setLatitude(Double.parseDouble(lat));
            location.setLongitude(Double.parseDouble(lng));
            location.setBearing(Float.valueOf(bearing));
            if(!sharedPrefence.getIsWating())
            tripViewModel.sendLocation(location);
        }
    }
}
