package bz.pei.driver.ui.drawerscreen.fragmentz;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.View;

import androidx.databinding.library.baseAdapters.BR;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import bz.pei.driver.R;
import bz.pei.driver.databinding.FragmentMapBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.utilz.SharedPrefence;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**/
public class MapFragment extends BaseFragment<FragmentMapBinding, MapFragmentViewModel> implements MapNavigator {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String AVAILABILITY_PARAM1 = "AVAILABILITY_PARAM1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LISTENER = "listener";
    public static final String TAG = "MapFragment";

    @Inject
    MapFragmentViewModel mapFragmentViewModel;
    @Inject
    SharedPrefence sharedPrefence;

    public FragmentMapBinding fragmentMapBinding;
    private String mParam1;
    private String mParam2;
    //    NewRequestListener listener;
    BaseActivity activity;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2/*, NewRequestListener requestListener*/) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
//        args.putParcelable(LISTENER, requestListener);
        fragment.setArguments(args);
        return fragment;
    }

    public static MapFragment newInstance(int param1) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt(AVAILABILITY_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentMapBinding = getViewDataBinding();
        fragmentMapBinding.mapView.onCreate(savedInstanceState);
        mapFragmentViewModel.setNavigator(this);
        ((DrawerAct) getBaseActivity()).disableToggleStatusIcon(true);
        Setup();
        if (getArguments().getInt(AVAILABILITY_PARAM1) >= 0) {
            mapFragmentViewModel.availableDriver.set(getArguments().getInt(AVAILABILITY_PARAM1) == 1);
//                ((DrawerAct) getBaseActivity()).resumeDriverState();
        } else {
            mapFragmentViewModel.availableDriver.set(true);

        }
    }

    private void Setup() {
//        To Check the status of Driver Offline or Online
        if (activity == null)
            activity = (BaseActivity) getActivity();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (checkPlayServices()) {
            mapFragmentViewModel.buildGoogleApiClient();
        } else {
            getBaseActivity().finish();/// Later change..have to look fragment deattach...
        }
    }

    @Override
    public MapFragmentViewModel getViewModel() {
        return mapFragmentViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_map;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapFragmentViewModel.buildGoogleApiClient();
//        startSocket();
        mapFragmentViewModel.startLocationUpdate();
    }

    @Override
    public void onResume() {
        super.onResume();
        getBaseActivity().setTitle(R.string.app_title);
        fragmentMapBinding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentMapBinding.mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapFragmentViewModel.disconnectGoogleApiClient();
        mapFragmentViewModel.stopLocationUpdate(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMapBinding.mapView.onDestroy();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        fragmentMapBinding.mapView.onLowMemory();
    }


    @Override
    public Context getAttachedcontext() {
        return getContext();
    }

    //change later using api with retrofit ..
    private LatLng getLocationFromAddress(final String place) {
        LatLng loc = null;
        Geocoder gCoder = new Geocoder(getActivity());
        try {
            final List<Address> list = gCoder.getFromLocationName(place, 1);
            if (list != null && list.size() > 0) {
                loc = new LatLng(list.get(0).getLatitude(), list.get(0)
                        .getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loc;
    }


    @Override
    public void sendLocations(String id, String lat, String lng, String bearing) {
        if (getContext() != null)
            ((DrawerAct) getContext()).emitLocationDetails(id, lat, lng, bearing);
    }

    @Override
    public void startSocket() {
        ((DrawerAct) getContext()).startSocket();
    }

    @Override
    public void stopSocket() {
        if(getBaseActivity()!=null)
        ((DrawerAct) getBaseActivity()).stopSocket();
    }

    @Override
    public void toggleOflineOnline(boolean isactive) {
        ((DrawerAct) getContext()).toggleDriverStatus(isactive);
    }

    public void reInitiateLocationListener() {
        if (mapFragmentViewModel != null)
            mapFragmentViewModel.startLocationUpdate();
    }

    public void setOfflineOnline(int status) {
        if (mapFragmentViewModel != null) {
            mapFragmentViewModel.startLocationUpdate();
            mapFragmentViewModel.setupStatusAtStatup(status);
        }
    }

    public void setShareState(int status) {
        if (mapFragmentViewModel != null) {
            mapFragmentViewModel.changeShareState(status);
        }
    }

    public void stopRunningLocationListener() {
        if (mapFragmentViewModel != null)
            mapFragmentViewModel.stopLocationUpdate(true);
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }
}
