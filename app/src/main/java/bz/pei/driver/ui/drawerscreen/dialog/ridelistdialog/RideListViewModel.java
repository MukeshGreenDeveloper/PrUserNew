package bz.pei.driver.ui.drawerscreen.dialog.ridelistdialog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.LoginModel;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.adapter.RecyclerAdapter;
import bz.pei.driver.adapter.RecyclerCallBack;
import bz.pei.driver.databinding.RideListItemBinding;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RideListViewModel extends BaseNetwork<RequestModel, bz.pei.driver.ui.drawerscreen.dialog.ridelistdialog.RideListNavigator> implements GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    public ObservableBoolean isdata;
    SharedPrefence sharedPrefence;
    HashMap<String, String> map;
    private static final long INTERVAL = 1000 * 5;
    private GoogleApiClient mGoogleApiClient;
    private String TAG = "rideshare";
    private LocationRequest mLocationRequest;
    private String driver_ID, driver_Token;
    private Socket socket;
    private Gson gson;
    private Location locationNew, locationOld;
    public ObservableField<LatLng> currentLatlng = new ObservableField<>();

    @Inject
    public RideListViewModel(GitHubService gitHubService,
                             HashMap<String, String> hashMap, SharedPrefence sharedPrefence, Socket socket, Gson gson) {
        super(gitHubService);
        this.map = hashMap;
        this.gson = gson;
        this.sharedPrefence = sharedPrefence;
        isdata = new ObservableBoolean(false);
        this.socket = socket;
    }

    @Override
    public HashMap<String, String> getMap() {
        return map;
    }


    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        setIsLoading(false);
        if (response.success)
            if (response.successMessage.equalsIgnoreCase("driver share request inprogress")) {
                if (response.share_request != null && response.share_request.size() > 0) {
                    calldriverShareAdapter(response.share_request);
                } else {
                    sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
                    getmNavigator().showMapFragment(1);
                }


            }

    }

    private void calldriverShareAdapter(List<RequestModel.Request> model) {
        if (model != null && model.size() != 0) {
            isdata.set(true);

        }
        RecyclerAdapter<RequestModel.Request, RideListItemBinding> shareRequestModelDialogRideListBindingRecyclerAdapter = new
                RecyclerAdapter<>(getmNavigator().getmContext(),
                model, R.layout.ride_list_item, new RecyclerCallBack<RideListItemBinding, RequestModel.Request>() {
            @Override
            public void bindData(RideListItemBinding binder, final RequestModel.Request model, int position) {
                binder.tvName.setText(model.user.firstname + " " + model.user.lastname);
                binder.txtAddressFrom.setText(model.pickLocation);
                binder.txtAddressTo.setText(model.dropLocation);
                Glide.with(getmNavigator().getmContext()).load(model.user.profilePic).
                        apply(RequestOptions.circleCropTransform().error(R.drawable.ic_user).
                                placeholder(R.drawable.ic_user)).into(binder.imgProfilePicProfile);
                if (model.is_driver_arrived == 1) {
                    binder.tripStatus.setText("No yet start");
                }
                if (model.is_trip_start == 1) {
                    binder.tripStatus.setText("Trip Started");
                }
                if (model.is_completed == 1) {
                    binder.tripStatus.setText("Trip Completed");
                }
                if (model.is_driver_arrived == 0 && model.is_trip_start == 0 && model.is_completed == 0)
                    binder.tripStatus.setText("Trip Accepted");

                binder.executePendingBindings();

                binder.llViews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((DrawerAct) getmNavigator().getmContext()).showShareTripFragment(model);
                    }
                });


            }
        });
        getmNavigator().setAdapter(shareRequestModelDialogRideListBindingRecyclerAdapter);
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e.getMessage());
    }

    public void passengerList() {

        if (sharedPrefence != null && driver_ID == null) {
            String driverDetails = sharedPrefence.Getvalue(SharedPrefence.DRIVERERDETAILS);
            LoginModel model = gson.fromJson(driverDetails, LoginModel.class);
            if (model != null) {
                driver_ID = model.id + "";
                driver_Token = model.token;
            }
        }

        map.clear();
        map.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        setIsLoading(true);
        GetDriverShareRequestInProgress(map);


//        hashMap.clear();
//        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.DRIVER_ID));
//        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.DRIVER_TOKEN));
//        rideListApi();
    }

    public void cancelDialog(View view) {

    }

    public void buildGoogleApiClient() {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(getmNavigator().getmContext())
                .addConnectionCallbacks(this)
                /*.addOnConnectionFailedListener(this)*/
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        /*mLocationRequest.setFastestInterval(FASTEST_INTERVAL);*/
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void stopLocationUpdate() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
        stopSocket();
    }

    public void startLocationUpdate() {
        if (!mGoogleApiClient.isConnected()) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(getmNavigator().getmContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getmNavigator().getmContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started.......................");
//        Toast.makeText(drawerAct, "StartedLocationUpdate", Toast.LENGTH_SHORT).show();
        startSocket();
    }

    public void disconnectGoogleApiClient() {

        mGoogleApiClient.disconnect();
    }

    public void startSocket() {
        initiateSocket();
    }

    public void stopSocket() {
        offSocket();
    }

    private void initiateSocket() {
        try {
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            socket.on(Constants.NetworkParameters.DRIVER_REQUEST, driverRequest);

            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void offSocket() {
        socket.disconnect();
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.off(Constants.NetworkParameters.DRIVER_REQUEST, driverRequest);

    }

    private Emitter.Listener driverRequest = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("Key_receivedNewRequest:", "" + (args.length > 0 ? args[0] : ""));
            offSocket();
            if (args.length > 0) {
                RequestModel model = CommonUtils.getSingleObject(args[0] + "", RequestModel.class);
                if (model != null)
                    if (model.request != null)
                        ((DrawerAct) getmNavigator().getmContext()).openAcceptReject(model, args[0] + "");
            }
        }
    };


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("Keys", "_RideListViewModelonConnect:" + driver_ID);
            JSONObject object = new JSONObject();
            try {
                object.put(Constants.NetworkParameters.id, driver_ID);
                socket.emit(Constants.NetworkParameters.SET_LOCATION, object.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("Keys", "_RideListViewModelonDisconnec" + (args.length > 0 ? args[0] : ""));

        }
    };
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("Keys", "_RideListViewModelonConnectError" + (args.length > 0 ? args[0] : ""));
        }
    };


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            startLocationUpdate();
            locationNew = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (locationNew != null) {
                currentLatlng.set(new LatLng(locationNew.getLatitude(), locationNew.getLongitude()));

                if (locationOld == null) {
                    locationOld = locationNew;
                }
                doWorkWithNewLocation(locationNew);
//                if (pickupLatlng.get() != null && dropLatlng.get() != null)
//                    drawPathPickupDrop(pickupLatlng.get(), dropLatlng.get());
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    void doWorkWithNewLocation(Location location) {
//        if (CommonUtils.isBetterLocation(locationOld, location)) {
//            // If location is better, do some user preview.
        sendLocation(location);
//        } else {
//            sendLocation(locationOld);
//        }
    }

    public void sendLocation(Location locatION) {
        sharedPrefence.savevalue(SharedPrefence.Lat, locatION.getLatitude() + "");
        sharedPrefence.savevalue(SharedPrefence.Lng, locatION.getLatitude() + "");
        String bearing = (locatION.hasBearing() ? locatION.getBearing() : locationNew.bearingTo(locationOld)) + "";
        sharedPrefence.savevalue(SharedPrefence.BEARING, bearing);
        locationOld = locatION;
        if (driver_ID == null)
            getDriverDetails();
        sendSocketLocationMessage(locatION.getLatitude(), locatION.getLongitude(), bearing, driver_ID);
    }

    private void getDriverDetails() {
        if (sharedPrefence != null && driver_ID == null) {
            String driverDetails = sharedPrefence.Getvalue(SharedPrefence.DRIVERERDETAILS);
            LoginModel model = gson.fromJson(driverDetails, LoginModel.class);
            if (model != null) {
                driver_ID = model.id + "";
                driver_Token = model.token;
            }
        }
        if (!TextUtils.isEmpty(sharedPrefence.Getvalue(SharedPrefence.Lat))) {
            currentLatlng.set(new LatLng(Double.parseDouble(sharedPrefence.Getvalue(SharedPrefence.Lat)),
                    Double.parseDouble(sharedPrefence.Getvalue(SharedPrefence.Lng))));

        }
    }

    public void sendSocketLocationMessage(Double lat, Double lng, String bearing, String id) {
        JSONObject object = new JSONObject();
        try {

            object.put(Constants.NetworkParameters.id, id);
            object.put(Constants.NetworkParameters.LAT, lat);
            object.put(Constants.NetworkParameters.LNG, lng);
            object.put(Constants.NetworkParameters.BEARING, bearing);
            object.put(Constants.NetworkParameters.IS_SHARE, "1");

//            socketModel.update(id, lat + "", lng + "", bearing, isTripStarted.get() ? "1" : "0", user_ID, request_ID);
            Log.d(TAG, "Keys______________-" + object);
            if (socket.connected()) {
                socket.emit(Constants.NetworkParameters.TRIP_LOCATION, object.toString()/*CommonUtils.getStringFromObject(socketModel)*/);
//                Toast.makeText(getmNavigator().getApplicationContext(), lat + "," + lng, Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Keys_______Not Connected");
                socket.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null && !sharedPrefence.getIsWating()) {
            System.out.println("++++onLocationChanged++++");
            locationNew = location;
            doWorkWithNewLocation(location);
            currentLatlng.set(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }
}
