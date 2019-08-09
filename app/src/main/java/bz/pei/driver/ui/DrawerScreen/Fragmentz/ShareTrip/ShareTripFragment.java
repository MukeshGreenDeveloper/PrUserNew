package bz.pei.driver.ui.DrawerScreen.Fragmentz.ShareTrip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import bz.pei.driver.databinding.FragmentShareTripBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.BillDialog.BillDialogFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.CancelDialog.CancelDialogFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.ShareBillDialog.ShareBillDialogFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.WaitingDialog.WaitingDialogFragment;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 12/21/17.
 */

public class ShareTripFragment extends BaseFragment<FragmentShareTripBinding, ShareTripViewModel> implements ShareTripNavigator {

    public static final String TAG = "ShareTripFragment";
    BaseActivity activity;
    @Inject
    ShareTripViewModel tripViewModel;
    @Inject
    SharedPrefence sharedPrefence;
    FragmentShareTripBinding binding;
    private RequestModel.Request requestModel;
    public final int TRIP_WAIT_CODE = 101;
    public final int TRIP_CANCL_CODE = 102;
    public int headerHeight, footerHeight;
    private CancelDialogFragment fragment;

    public static ShareTripFragment newInstance(RequestModel.Request param1) {
        ShareTripFragment fragment = new ShareTripFragment();
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
        Setup();/*
        Log.d(TAG, "Header:" + binding.headerTripfragment.getHeight()
                + "\n Footer:" + binding.footerTripfragment.getHeight());*/
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
//                    tripViewModel.setMapPaddingwhileTrip(binding.headerTripfragment.getMeasuredHeight(), binding.footerTripfragment.getMeasuredHeight());
                //  googleMap.setPadding(0, fragmentRideBinding.FRTopLayout.getMeasuredHeight(), 0, fragmentRideBinding.FRBottomlayout.getMeasuredHeight());
//                rideFragViewModel.setPins(mParam1, googleMap);//*
            }
        });
        ((DrawerAct) getBaseActivity()).disableToggleStatusIcon(false);


    }

    @Override
    public List<Integer> getMapPadding() {
        return new ArrayList<Integer>(Arrays.asList(headerHeight, footerHeight));
    }

    @Override
    public void showPassengerList() {
        ((DrawerAct) getActivity()).showShareFragment();
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
    public ShareTripViewModel getViewModel() {
        return tripViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_share_trip;
    }

    @Override
    public Context getApplicationContext() {
        return getContext();
    }

    @Override
    public void openMapFragment() {//Null at here
        getBaseActivity().showRefreshedHOme();
    }

    @Override
    public void openFeedbackFragment(RequestModel model) {
        ShareBillDialogFragment.newInstance(model).setCancelable(false);
        ShareBillDialogFragment.newInstance(model).show(this.getChildFragmentManager(), BillDialogFragment.TAG);

//        if (getBaseActivity() != null)
//            getBaseActivity().showFeedbackFragment(model);
    }

    @Override
    public void openCallDialer(String phoneNUmber) {
        getBaseActivity().makeCAll(phoneNUmber);
    }

    @Override
    public void openGoogleMap(Double destLat, Double destLng) {
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
        fragment = CancelDialogFragment.newInstance(CancelDialogFragment.TAG);
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
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }


}
