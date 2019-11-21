package bz.pei.driver.ui.signupscreen.fragmentz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.retro.responsemodel.VehicleTypeModel;
import bz.pei.driver.databinding.FragmentVehicleInfoBinding;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.signupscreen.fragmentz.adapter.VehiclePagerAdapter;
import bz.pei.driver.ui.signupscreen.fragmentz.adapter.VehicleTypeSelectionListner;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VehicleInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VehicleInfoFragment extends BaseFragment<FragmentVehicleInfoBinding, VehicleInfoViewModel> implements VehicleInfoNavigator, VehicleTypeSelectionListner {
    @Inject
    VehicleInfoViewModel vehicleInfoViewModel;
    @Inject
    SharedPrefence sharedPrefence;
    FragmentVehicleInfoBinding vehicleInfoBinding;
    VehiclePagerAdapter vehiclePagerAdapter;

    public VehicleInfoFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VehicleInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VehicleInfoFragment newInstance() {
        VehicleInfoFragment fragment = new VehicleInfoFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ULocale.setKeywordValue("","sadfdfsa");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                mMessageReceiver, new IntentFilter(Constants.Broadcast_VehicleFrgAction));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                vehicleInfoReceiver, new IntentFilter(Constants.Broadcast_VehicleTypeChangeAction));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vehicleInfoBinding = getViewDataBinding();
        vehicleInfoViewModel.setNavigator(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//            vehicleInfoViewModel.send_adminID();
    }

    @Override
    public VehicleInfoViewModel getViewModel() {
        return vehicleInfoViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_vehicle_info;
    }

    @Override
    public void openDocUploadActivity() {

    }

    @Override
    public void setUpPagerAdapter(List<VehicleTypeModel.Type> types) {
        vehiclePagerAdapter = new VehiclePagerAdapter(getActivity(), types, this);
        vehicleInfoBinding.pagerVehicleSignup.setAdapter(vehiclePagerAdapter);
        vehicleInfoBinding.pagerVehicleSignup.addOnPageChangeListener(vehiclePagerAdapter);
    }

    @Override
    public void setUpPagerPosition(boolean isRight) {
        int pos = vehicleInfoBinding.pagerVehicleSignup.getCurrentItem();
        if (vehiclePagerAdapter == null)
            return;
        int total = vehiclePagerAdapter.getCount();
        if (total > 0)
            if (isRight) {
                int next = 0;
                if (pos < total && pos != (total - 1))
                    next = pos + 1;
                vehicleInfoBinding.pagerVehicleSignup.setCurrentItem(next);
            } else {
                int prev = 0;
                if (pos > 0)
                    prev = pos - 1;
                vehicleInfoBinding.pagerVehicleSignup.setCurrentItem(prev);
            }

    }

    @Override
    public Context getAttachedcontext() {
        return getContext();
    }

    @Override
    public ViewPager getPagerAdapter() {
        return vehicleInfoBinding.pagerVehicleSignup;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (vehicleInfoViewModel.validataion()) {
                Intent intent1 = new Intent(Constants.Broadcast_SignupAction);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent1);
            }
        }
    };
    private BroadcastReceiver vehicleInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (vehicleInfoViewModel != null) {
                vehicleInfoViewModel.getVehicleTypeswithAdminID();
            }
        }
    };

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(
                mMessageReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(
                vehicleInfoReceiver);
        super.onDestroyView();

    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public void onVehicleItemClicked(String vehiclePos) {
        vehicleInfoViewModel.setSelectedVehiclePositon(vehiclePos);
//        vehiclePagerAdapter.notifyDataSetChanged();
    }
}
