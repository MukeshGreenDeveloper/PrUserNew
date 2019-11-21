package bz.pei.driver.ui.drawerscreen.dialog.ridelistdialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.adapter.RecyclerAdapter;
import bz.pei.driver.databinding.DialogRideListBinding;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

/**
 * Created by root on 1/3/18.
 */

public class RideListDialogFragment extends BaseFragment<DialogRideListBinding, RideListViewModel> implements RideListNavigator {
    DialogRideListBinding binding;
    @Inject
    RideListViewModel viewModel;

    public static String TAG = "RideListDialogFragment";
    private LinearLayoutManager mLayoutManager;
    private LinearLayoutManager linearLayoutManager;


    public static RideListDialogFragment newInstance() {

        Bundle args = new Bundle();
//        args.putSerializable(Constants.IntentExtras.REQUEST_DATA, (Serializable) param);
        RideListDialogFragment fragment = new RideListDialogFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        model = (List<RequestModel.Request>) getArguments().getSerializable(Constants.IntentExtras.REQUEST_DATA);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = getViewDataBinding();
        viewModel.setNavigator(this);
        getBaseActivity().setTitle(R.string.app_name);
        ((DrawerAct) getBaseActivity()).disableToggleStatusIcon(false);

        setUp();
    }

    private void setUp() {
        binding.recyclerRideList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerRideList.setLayoutManager(linearLayoutManager);
        viewModel.buildGoogleApiClient();
        viewModel.passengerList();
    }

    @Override
    public void showMapFragment(int isAcive) {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).showMapFragment(1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.disconnectGoogleApiClient();
        viewModel.offSocket();
        viewModel.stopLocationUpdate();
    }

    @Override
    public RideListViewModel getViewModel() {
        return viewModel;
    }


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_ride_list;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return null;
    }

    @Override
    public FragmentActivity getmContext() {
        return getActivity();
    }

    @Override
    public void setAdapter(RecyclerAdapter recyclerAdapter) {
        binding.recyclerRideList.setAdapter(recyclerAdapter);
    }


}
