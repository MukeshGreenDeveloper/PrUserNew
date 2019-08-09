package bz.pei.driver.ui.DrawerScreen.Fragmentz.MultipleShareRide;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.Retro.ResponseModel.RequestModel;
import bz.pei.driver.databinding.FragmentMultipleShareRideBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.List;

import javax.inject.Inject;

public class MultipleShareRideFragment extends BaseFragment<FragmentMultipleShareRideBinding, MultipleShareRideViewModel> implements MultipleShareRideNavigator {
    public static String TAG = "MultipleShareRideFragment";
    // TODO: Rename parameter arguments, choose names that match


    FragmentMultipleShareRideBinding binding;


    @Inject
    MultipleShareRideViewModel viewModel;
    @Inject
    SharedPrefence sharedPrefence;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    BaseActivity activity;
    private SupportMapFragment mSupportMapFragment;

    public MultipleShareRideFragment() {
        // Required empty public constructor
    }

    public static MultipleShareRideFragment newInstance(String param1, String param2) {
        MultipleShareRideFragment fragment = new MultipleShareRideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {


                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
              /*      googleMap.setMyLocationEnabled(false);
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);*/
//                if(!googleMap.isTrafficEnabled())
//                    googleMap.setTrafficEnabled(true);

//                    AnimationMarker(latLng);

                viewModel.initMap(googleMap, viewModel.mMapLatLng.get());


            }
        });

        Setup();
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
            viewModel.buildGoogleApiClient();
        } else {
            getBaseActivity().finish();/// Later change..have to look fragment deattach...
        }
    }

    @Override
    public MultipleShareRideViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_multiple_share_ride;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void onStart() {
        super.onStart();
        viewModel.buildGoogleApiClient();
//        startSocket();
        viewModel.startLocationUpdate();
    }

    @Override
    public BitmapDescriptor getMarkerIcon(int drawID) {
        return BitmapDescriptorFactory.fromBitmap(CommonUtils.getBitmapFromDrawable(getActivity(), drawID));
    }

    @Override
    public void passengersDialogs(List<RequestModel.Request> mShareRequest) {
//        RideListDialogFragment fragment = RideListDialogFragment.newInstance(mShareRequest);
//        fragment.show(getChildFragmentManager(), RideListDialogFragment.TAG);
    }
}
