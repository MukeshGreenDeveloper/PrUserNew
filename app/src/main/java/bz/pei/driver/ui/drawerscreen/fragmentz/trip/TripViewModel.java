package bz.pei.driver.ui.drawerscreen.fragmentz.trip;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import bz.pei.driver.Pojos.DrawPath.BeanRoute;
import bz.pei.driver.Pojos.DrawPath.BeanStep;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubMapService;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.LoginModel;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.retro.responsemodel.TripDetailsSocketModel;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.MyDataComponent;
import bz.pei.driver.utilz.SharedPrefence;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static bz.pei.driver.utilz.Constants.SERVER_API_KEY;

/**
 * Created by root on 12/21/17.
 */

public class TripViewModel extends BaseNetwork<RequestModel, TripNavigator> implements GoogleApiClient.ConnectionCallbacks,
        LocationListener, OnMapReadyCallback {
    private static ObservableBoolean zoomset = new ObservableBoolean(false);
    private static String TAG = "TripViewModel";
    public ObservableField<String> userPic = new ObservableField<>();
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<LatLng> currentLatlng = new ObservableField<>();
    public ObservableField<LatLng> pickupLatlng = new ObservableField<>();
    public ObservableField<LatLng> dropLatlng = new ObservableField<>();
    public ObservableInt userRating = new ObservableInt();
    public ObservableField<String> tripDistance = new ObservableField<>("00.00 ");
    public ObservableField<String> tripTime = new ObservableField<>("00 mins");
    public ObservableField<String> addionalCharge = new ObservableField<>("");
    public ObservableField<String> tripPaymentMode = new ObservableField<>();
    public ObservableBoolean isArrived = new ObservableBoolean();
    public ObservableBoolean isCompleted = new ObservableBoolean();
    public ObservableBoolean isTripStarted = new ObservableBoolean();
    public static int SENT = 4, ERROR = 3, CONNECTING = 2, SENDING = 1;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static SharedPrefence sharedPrefence;
    private GitHubService gitHubService;
    private GitHubMapService gitHubMapService;
    private Gson gson;
    private static DrawerAct drawerAct;
    private Socket socket;
    private static GoogleMap googleMap;
    private Location locationNew, locationOld;
    private String driver_ID, driver_Token, user_ID, user_PhoneNumber, request_ID;
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static Marker driverMarker;
    private Marker pickupMarker, dropMarker;
    private static boolean isPathDrawn = false;
    private String distanceTravelled = "0.00", cancelReason = null, pickupAddress = "", dropAddress = ""/*kilometerString = "km"*/;
    private long waitingtimeBeforeTrip = 0L;
    private long waitingtimeInTrip = 0L;
    private long totalWaitingTime = 0L;
    private double totalDistance = 0L;
    HashMap<String, String> map = new HashMap<>();
    private long lastUpdatedTime;
    PolylineOptions options = new PolylineOptions();
    PolylineOptions options1 = new PolylineOptions();

    public TripViewModel(GitHubService gitHubService, GitHubMapService gitHubMapService,
                         SharedPrefence sharedPrefence, Gson gson,
                         Socket socket, LocationRequest locationRequest, DrawerAct drawerAct) {
        super(gitHubService, sharedPrefence, gson);
        DataBindingUtil.setDefaultComponent(new MyDataComponent(this));
        this.gitHubService = gitHubService;
        this.gitHubMapService = gitHubMapService;
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
        this.socket = socket;
        this.drawerAct = drawerAct;
        isPathDrawn = false;
        getDriverDetails();

    }

    public void setUpTripDetails(RequestModel model) {
        if (model == null)
            return;

        if (model.request != null) {
            pickupLatlng.set(model.request.getPickupLatlng());
            dropLatlng.set(model.request.getDropLatlng());
            tripPaymentMode.set(drawerAct.getString(
                    model.request.payment_opt == 1 ? R.string.txt_cash :
                            model.request.payment_opt == 0 ? R.string.txt_card :
                                    model.request.payment_opt == 4 ? R.string.text_corporate :
                                            R.string.text_wallet));
            isArrived.set(model.request.is_driver_arrived == 1);
            isTripStarted.set(model.request.is_trip_start == 1);
            isCompleted.set(model.request.is_completed == 1);
            request_ID = model.request.id + "";
            sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, model.request.id);
            sharedPrefence.saveIntValue(SharedPrefence.USER_ID, model.request.user.id);
            sharedPrefence.saveIntValue(SharedPrefence.IS_SHARE, 0);
            sharedPrefence.saveIntValue(SharedPrefence.TRIP_START, model.request.is_trip_start);
            if (model.request.user != null) {
                user_ID = model.request.user.id + "";
                userName.set(model.request.user.firstname + " " + model.request.user.lastname);
                user_PhoneNumber = model.request.user.phoneNumber;
                if (model.request.user.review > 0)
                    userRating.set((int) model.request.user.review);
                if (!CommonUtils.IsEmpty(model.request.user.profilePic))
                    userPic.set(model.request.user.profilePic);
            }
           /* if (isTripStarted.get() && sharedPrefence.getTripDistance() > 0) {
                totalDistance = sharedPrefence.getTripDistance();
                tripDistance.set(totalDistance + " ");
            }*/
        }
        if (sharedPrefence.getIsWating()) {
            //open waiting time dialog
            getmNavigator().openWaitingDialog((int) sharedPrefence.getWAITING_TIME(),
                    (int) sharedPrefence.getWAITING_TIMESec());
            stopLocationUpdate();
        }
    }

    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        switch ((int) taskId) {
            case Constants.TaskID.CANCEL_REQUEST:
                setIsLoading(false);
                if (response.success) {
                    if (response.successMessage.equalsIgnoreCase("trip cancelled")) {
                        sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
                        sharedPrefence.saveIntValue(SharedPrefence.USER_ID, Constants.NO_ID);
                        sharedPrefence.saveIntValue(SharedPrefence.IS_SHARE, Constants.NO_ID);
                        sharedPrefence.saveIntValue(SharedPrefence.TRIP_START, Constants.NO_ID);
                        sharedPrefence.saveWAITING_TIME(00);
                        sharedPrefence.saveWAITING_Sec(00);
                        sharedPrefence.saveTripDistance(0.0f);
                        getmNavigator().openMapFragment();
                    }
                } else
                    getmNavigator().showMessage(response.errorMessage);
                break;
            case Constants.TaskID.DRIVER_ARRIVED:
                setIsLoading(false);
                if (response.success) {
                    if (response.successMessage.equalsIgnoreCase("driver arrived"))
                        isArrived.set(true);
                } else
                    getmNavigator().showMessage(response.errorMessage);
                break;
            case Constants.TaskID.TRIP_STARTED:
                setIsLoading(false);
                if (response.success) {
                    if (response.successMessage.equalsIgnoreCase("trip started")) {
                        sharedPrefence.saveIntValue(SharedPrefence.TRIP_START, 1);
                        sharedPrefence.saveTripDistance(0.0f);
                        isTripStarted.set(true);
                    }

                } else
                    getmNavigator().showMessage(response.errorMessage);
                break;
            case Constants.TaskID.TRIP_COMPLETED:
                setIsLoading(false);
                if (response.success) {
                    if (response.successMessage.equalsIgnoreCase("bill_generated") || response.successMessage.equalsIgnoreCase("driver request inprogress")) {
                        stopLocationUpdate();
                        getmNavigator().openFeedbackFragment(response);
                        sharedPrefence.saveIntValue(SharedPrefence.USER_ID, Constants.NO_ID);
                        sharedPrefence.saveIntValue(SharedPrefence.IS_SHARE, Constants.NO_ID);
                        sharedPrefence.saveIntValue(SharedPrefence.TRIP_START, Constants.NO_ID);
                        sharedPrefence.saveIsWating(false);
                        sharedPrefence.saveWAITING_TIME(00);
                        sharedPrefence.saveWAITING_Sec(00);
                        sharedPrefence.saveTripDistance(0.0f);
                    }
                } else
                    getmNavigator().showMessage(response.errorMessage);
                break;
        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.REQUEST_ALREADY_CANCELLED
                || e.getCode() == Constants.ErrorCode.REQUEST_NOT_REGISTERED
                || e.getCode() == Constants.ErrorCode.REQUEST_ALREADY_CANCELLED2) {
            sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
            sharedPrefence.saveIntValue(SharedPrefence.USER_ID, Constants.NO_ID);
            sharedPrefence.saveIntValue(SharedPrefence.IS_SHARE, Constants.NO_ID);
            sharedPrefence.saveIntValue(SharedPrefence.TRIP_START, Constants.NO_ID);
            sharedPrefence.saveTripDistance(0.0f);
            getmNavigator().openMapFragment();
        } else if (e.getCode() == Constants.ErrorCode.DRIVER_ALREADY_ARRiVED) {
            isArrived.set(true);
        } else if (e.getCode() == Constants.ErrorCode.DRIVER_ALREADY_STARTED) {
            isTripStarted.set(true);
        } else
            getmNavigator().showMessage(e.getMessage());
    }

    @Override
    public HashMap<String, String> getMap() {

        map.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        map.put(Constants.NetworkParameters.REQUEST_ID, sharedPrefence.getIntvalue(SharedPrefence.REQUEST_ID) + "");
        if (mCurrentTaskId == Constants.TaskID.CANCEL_REQUEST) {
            map.put(Constants.NetworkParameters.RESASON, "0");
            map.put(Constants.NetworkParameters.cancel_other_reason, cancelReason);
        }
        if (mCurrentTaskId == Constants.TaskID.TRIP_STARTED) {
            map.put(Constants.NetworkParameters.PLATITUDE, currentLatlng.get() != null ? currentLatlng.get().latitude + "" : "");
            map.put(Constants.NetworkParameters.PLONGITUDE, currentLatlng.get() != null ? currentLatlng.get().longitude + "" : "");
            map.put(Constants.NetworkParameters.PLOCATION, pickupAddress);
        } else if (mCurrentTaskId == Constants.TaskID.TRIP_COMPLETED) {
            map.put(Constants.NetworkParameters.DISTANCE, distanceTravelled);
            map.put(Constants.NetworkParameters.BEFORE_WAITING_TIME, waitingtimeBeforeTrip + "");
            map.put(Constants.NetworkParameters.AFTER_WAITING_TIME, waitingtimeInTrip + "");
            map.put(Constants.NetworkParameters.DLATITUDE, currentLatlng.get() != null ? currentLatlng.get().latitude + "" : "");
            map.put(Constants.NetworkParameters.DLONGITUDE, currentLatlng.get() != null ? currentLatlng.get().longitude + "" : "");
            map.put(Constants.NetworkParameters.DLOCATION, dropAddress);
        }
        return map;
    }

    @BindingAdapter("imageloaded")
    public void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).apply(RequestOptions.circleCropTransform().error(R.drawable.ic_user).placeholder(R.drawable.ic_user)).into(imageView);
    }

    public void buildGoogleApiClient() {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(drawerAct)
                .addConnectionCallbacks(this)
                /*.addOnConnectionFailedListener(this)*/
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    public void stopLocationUpdate() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
        stopSocket();
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
            socket.on(Constants.NetworkParameters.TRIP_STATUS, onTripStatus);
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
        socket.off(Constants.NetworkParameters.TRIP_STATUS, onTripStatus);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject object = new JSONObject();
            try {
                object.put(Constants.NetworkParameters.id, driver_ID);
                socket.emit(Constants.NetworkParameters.START_CONNECT, object.toString());
                Log.d("Keys", "_onConnect:" + object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("Keys", "_onDisconnec" + (args.length > 0 ? args[0] : ""));

        }
    };
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("Keys", "_onConnectError" + (args.length > 0 ? args[0] : ""));
        }
    };
    private Emitter.Listener onTripStatus = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("Key_While_Trip:", "" + args[0]);
            if (args.length > 0)
                if (args[0] != null)
                    if (!CommonUtils.IsEmpty(args[0].toString())) {
                        TripDetailsSocketModel socketModel = CommonUtils.getSingleObject(args[0].toString(), TripDetailsSocketModel.class);
                        if (socketModel != null)
                            distanceTravelled = socketModel.distance;
                        tripDistance.set(CommonUtils.doubleDecimalFromat(Double.valueOf(distanceTravelled == null ? "0.00" : distanceTravelled))/* + " " + kilometerString*/);//" km");
                    }
        }
    };

    @Override
    public void onMapReady(GoogleMap googlMap) {
        googleMap = googlMap;
        if (googlMap != null) {
            changeMapStyle();
            googleMap.setPadding(0, getmNavigator().getMapPadding().get(0), 0, getmNavigator().getMapPadding().get(1));
            if (ActivityCompat.checkSelfPermission(drawerAct, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(drawerAct, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(false);
            animationMarker(currentLatlng.get());
            if (pickupLatlng.get() != null && dropLatlng.get() != null)
                drawPathPickupDrop(pickupLatlng.get(), dropLatlng.get());
        }
    }

    public void changeMapStyle() {
        if (googleMap == null)
            return;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getmNavigator().getApplicationContext(), R.raw.style_json);
        googleMap.setMapStyle(style);
    }

    public static void animationMarker(LatLng latLng) {

        if (driverMarker != null)
            driverMarker.remove();
        if (latLng != null && googleMap != null) {
            driverMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(drawerAct.getString(R.string.text_mylocation))
                    .rotation(TextUtils.isEmpty(sharedPrefence.Getvalue(SharedPrefence.BEARING)) ? 0f :
                            Float.valueOf(sharedPrefence.Getvalue(SharedPrefence.BEARING)))
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_driver)));
            driverMarker.setFlat(true);
            if (!zoomset.get()) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                zoomset.set(true);
            } else {
                CameraPosition newCamPos = new CameraPosition(latLng,
                        15.0f,
                        googleMap.getCameraPosition().tilt, //use old tilt
                        googleMap.getCameraPosition().bearing); //use old bearing
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 18, null);
            }

        }
    }

    /**
     * Draw path from @param pickupLatlng and @param dropLatlng
     * and use R.color.colorprimary for line color
     */
    public void drawPathPickupDrop(final LatLng pickupLatLng, final LatLng dropLatLng) {
        Log.d("keys", "END:" + pickupLatLng + "DROP:" + dropLatLng);

        String origin = pickupLatLng.latitude + "," + pickupLatLng.longitude;
        String destination = dropLatLng.latitude + "," + dropLatLng.longitude;
        if (googleMap != null) {
            if (pickupMarker == null)
                pickupMarker = googleMap.addMarker(new MarkerOptions()
                        .position(pickupLatLng)
                        .title("Pickup Location")
                        .anchor(0.5f, 0.5f)
                        .icon(getmNavigator().getMarkerIcon(R.drawable.ic_pickup)));
            if (dropMarker == null)
                dropMarker = googleMap.addMarker(new MarkerOptions()
                        .position(dropLatLng)
                        .title("Drop Location")
                        .anchor(0.5f, 0.5f)

                        .icon(getmNavigator().getMarkerIcon(R.drawable.ic_drop)));
        }
        setIsLoading(true);
        gitHubMapService.getPathLatLong(origin, destination, true, SERVER_API_KEY).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!CommonUtils.IsEmpty(response.body() + "")) {
                    BeanRoute route = new BeanRoute();
                    route = CommonUtils.parseRoute(response.body() + "", route);
                    if (route != null) {
                        if (isPathDrawn)
                            return;
                        isPathDrawn = true;
                        options = new PolylineOptions();
                        final ArrayList<BeanStep> step = route.getListStep();
                        List<LatLng> pointslaglng = new ArrayList<LatLng>();
                        options = new PolylineOptions();

                        for (int i = 0; i < step.size(); i++) {
                            List<LatLng> path = step.get(i).getListPoints();
                            pointslaglng.addAll(path);
                        }
                        options.addAll(pointslaglng);
                        options.width(8);
                        options.geodesic(true);
                        options.color(R.color.colorPrimary); // #00008B rgb(0,0,139)
                        if (pointslaglng != null && googleMap != null) {
                            Polyline polyline = googleMap.addPolyline(options);
                            buildLatlongBound(pickupLatLng, dropLatLng);
                        }

                    }
                }
                setIsLoading(false);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                setIsLoading(false);
                getmNavigator().showMessage(R.string.text_error_contact_server);
            }
        });
    }

    public void buildLatlongBound(LatLng pickup, LatLng drop) {
        LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
        latlngBuilder.include(pickup);
        latlngBuilder.include(drop);
        int width = drawerAct.getResources().getDisplayMetrics().widthPixels;
        int height = drawerAct.getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12);
        if (googleMap != null)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBuilder.build(), width, height, padding));
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            startLocationUpdate();
            locationNew = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (locationNew != null) {
                currentLatlng.set(new LatLng(locationNew.getLatitude(), locationNew.getLongitude()));
                animationMarker(currentLatlng.get());
                if (locationOld == null) {
                    locationOld = locationNew;
                }
                doWorkWithNewLocation(locationNew);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void startLocationUpdate() {
        if (!mGoogleApiClient.isConnected()) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(drawerAct, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(drawerAct, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started.......................");
        startSocket();
    }

    /**
     * Make use of location after deciding if it is better than previous one.
     *
     * @param location Newly acquired location.
     */
    void doWorkWithNewLocation(Location location) {
        if (System.currentTimeMillis() - lastUpdatedTime > 3000) {
            sendLocation(location);
            lastUpdatedTime = System.currentTimeMillis();
        }
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
            animationMarker(currentLatlng.get());
        }
    }

    public void sendLocation(Location locatION) {
        sharedPrefence.savevalue(SharedPrefence.Lat, locatION.getLatitude() + "");
        sharedPrefence.savevalue(SharedPrefence.Lng, locatION.getLatitude() + "");
        String bearing = (locatION.hasBearing() ? locatION.getBearing() : locationNew.bearingTo(locationOld)) + "";
       /* if (isTripStarted.get()) {
            totalDistance = totalDistance > 0 ?
                    (totalDistance + distance(locationNew.getLatitude(), locationNew.getLongitude(), locationOld.getLatitude(), locationOld.getLongitude()))
                    : distance(locationNew.getLatitude(), locationNew.getLongitude(), locationOld.getLatitude(), locationOld.getLongitude());
//            distanceTravelled = CommonUtils.doubleDecimalFromat(totalDistance) + "";
//            sharedPrefence.saveTripDistance(Float.valueOf(distanceTravelled));
//            tripDistance.set(distanceTravelled + " km");
        }*/
        sharedPrefence.savevalue(SharedPrefence.BEARING, bearing);
        locationOld = locatION;
        if (driver_ID == null)
            getDriverDetails();
        sendSocketLocationMessage(locatION.getLatitude(), locatION.getLongitude(), bearing, driver_ID);
    }

    public JSONArray locationArray=new JSONArray();

    public void sendSocketLocationMessage(Double lat, Double lng, String bearing, String id) {
        if (googleMap != null) {
            Polyline line = googleMap.addPolyline(options1
                    .add(new LatLng(lat, lng))
                    .width(8)
                    .color(Color.BLUE));
        }
        JSONObject objectParent = new JSONObject();
        JSONObject object = new JSONObject();
        try {
            objectParent.put(Constants.NetworkParameters.id, id);
            objectParent.put(Constants.NetworkParameters.LAT, lat);
            objectParent.put(Constants.NetworkParameters.LNG, lng);
            object.put(Constants.NetworkParameters.LAT, lat);
            object.put(Constants.NetworkParameters.LNG, lng);
            objectParent.put(Constants.NetworkParameters.BEARING, bearing);
            objectParent.put(Constants.NetworkParameters.TRIP_START, isTripStarted.get() ? "1" : "0");
//            if (isTripStarted.get())
//                objectParent.put(Constants.NetworkParameters.DISTANCE, /*CommonUtils.doubleDecimalFromat(totalDistance) + */"0");
            objectParent.put(Constants.NetworkParameters.USER_ID, user_ID);
            objectParent.put(Constants.NetworkParameters.REQUEST_ID, request_ID);
            objectParent.put(Constants.NetworkParameters.IS_SHARE, "0");

            if (socket.connected()) {
                locationArray.put(object);
                objectParent.put(Constants.NetworkParameters.LAT_LNG_ARRAY, locationArray);
                Log.d(TAG, "Keys______________-" + objectParent);
                socket.emit(Constants.NetworkParameters.TRIP_LOCATION, objectParent.toString());
                locationArray = new JSONArray();
            } else {
                locationArray.put(object);
                Log.d(TAG, "Keys_______Not Connected--Size="+locationArray.length());
                socket.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendSocketLocationMessage(String lat, Double lng, String bearing, String id) {
        Log.d(TAG, "Keys_______sendTripLocation");
        JSONObject object = new JSONObject();
        try {
            object.put(Constants.NetworkParameters.id, id);
            object.put(Constants.NetworkParameters.LAT, lat);
            object.put(Constants.NetworkParameters.LNG, lng);
            object.put(Constants.NetworkParameters.BEARING, bearing);
            object.put(Constants.NetworkParameters.TRIP_START, isTripStarted.get() ? "1" : "0");
            object.put(Constants.NetworkParameters.USER_ID, user_ID);
            object.put(Constants.NetworkParameters.REQUEST_ID, request_ID);
            object.put(Constants.NetworkParameters.IS_SHARE, "0");
            if (socket.connected()) {
                socket.emit(Constants.NetworkParameters.TRIP_LOCATION, object.toString());
            } else {

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
            animationMarker(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    public void driverArrived(View view) {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            tripArrived();
        }
    }

    public void tripCompleted(View view) {
        isCompleted.set(true);
    }

    public void onWaiting(View view) {
        sharedPrefence.saveIsWating(true);
        getmNavigator().openWaitingDialog((int) sharedPrefence.getWAITING_TIME(), (int) sharedPrefence.getWAITING_TIMESec());
//        stopLocationUpdate();
    }

    public void cancelingTrip(View view) {
        if (getmNavigator().isNetworkConnected()) {
            if (isTripStarted.get()) {
                getmNavigator().showMessage(drawerAct.getString(R.string.text_cannot_cancel_trip_right_now));
                return;
            }
            getmNavigator().navigateCancelDialog();
        }
    }

    public void addAdditionalCharge(View view) {
        if (getmNavigator().isNetworkConnected()) {
            if (isTripStarted.get()) {
                getmNavigator().listAddnlCharge();

            }

        }
    }

    public void confirmCanecl(String message) {
        cancelReason = message;
        if (CommonUtils.IsEmpty(message))
            return;
        setIsLoading(true);
        cancelCurrentRequest();
    }

    public void startTheTrip(View view) {
        if (getmNavigator().isNetworkConnected()) {

            if (!isTripStarted.get()) {
                setIsLoading(true);
                if (CommonUtils.IsEmpty(pickupAddress))
                    GetAddressFromLatLng(currentLatlng.get(), true);
                else
                    requestTripStarted();
            } else {
//                getmNavigator().showAddionalChargeDialog();
                processEndTrip("0");
            }
        }

    }

    public void processEndTrip(String addCharge) {
        setIsLoading(true);
        addionalCharge.set(addCharge);
        if (CommonUtils.IsEmpty(dropAddress))
            GetAddressFromLatLng(currentLatlng.get(), false);
        else
            requestTripCompletd();
    }

    public void makeCall(View view) {
        if (!CommonUtils.IsEmpty(user_PhoneNumber))
            getmNavigator().openCallDialer(user_PhoneNumber);
    }

    public void makeSMS(View view) {
        if (!CommonUtils.IsEmpty(user_PhoneNumber))
            getmNavigator().openSmsDrafter(user_PhoneNumber);
    }

    public void openGoogleMap(View view) {

        if (isTripStarted.get()) {
            if (dropLatlng.get() != null && dropLatlng.get().latitude != 0.0 && dropLatlng.get().longitude != 0.0)
                getmNavigator().openGoogleMap(dropLatlng.get().latitude, dropLatlng.get().longitude);
        } else {
            if (pickupLatlng.get() != null && pickupLatlng.get().latitude != 0.0 && pickupLatlng.get().longitude != 0.0)
                getmNavigator().openGoogleMap(pickupLatlng.get().latitude, pickupLatlng.get().longitude);
        }
    }

    public void setWaitingTime(int time, Integer seconds) {
        startLocationUpdate();
        if (isTripStarted.get())
            waitingtimeInTrip = waitingtimeBeforeTrip > 0 ? (time - waitingtimeBeforeTrip) : time;
        else
            waitingtimeBeforeTrip = time;
        totalWaitingTime =/*totalWaitingTime+*/time;
        sharedPrefence.saveWAITING_TIME(totalWaitingTime);
        sharedPrefence.saveWAITING_Sec(seconds);
    }

    private void GetAddressFromLatLng(LatLng latLng, final boolean isPickup) {
        setIsLoading(true);
        gitHubMapService.convertAddressFromLatLng(String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude), false, SERVER_API_KEY).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getAsJsonArray("results") != null) {
                        if (response.body().getAsJsonArray("results").size() != 0) {
                            if (isPickup) {
                                pickupAddress = (response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("formatted_address").getAsString());
                                map.put(Constants.NetworkParameters.PLOCATION, pickupAddress);
                                requestTripStarted();
                            } else {
                                dropAddress = (response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("formatted_address").getAsString());
                                map.put(Constants.NetworkParameters.DLOCATION, dropAddress);
                                requestTripCompletd();
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "GetAddressFromLatlng" + response.toString());
                    if (isPickup) {
                        map.put(Constants.NetworkParameters.PLOCATION, "");
                        requestTripStarted();
                    } else {
                        map.put(Constants.NetworkParameters.DLOCATION, "");
                        requestTripCompletd();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "GetAddressFromLatlng" + t.toString());
                if (isPickup) {
                    map.put(Constants.NetworkParameters.PLOCATION, "");
                    requestTripStarted();
                } else {
                    map.put(Constants.NetworkParameters.DLOCATION, "");
                    requestTripCompletd();
                }
            }
        });

    }


    private double distance(double newLat, double newLng, double oldLat, double oldLng) {
        String unit = "K";
        double radlat1 = Math.PI * oldLat / 180;
        double radlat2 = Math.PI * newLat / 180;
        double theta = oldLng - newLng;
        double radtheta = Math.PI * theta / 180;
        double dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
        dist = Math.acos(dist);
        dist = dist * 180 / Math.PI;
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        }
        if (unit == "N") {
            dist = dist * 0.8684;
        }
        return dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
