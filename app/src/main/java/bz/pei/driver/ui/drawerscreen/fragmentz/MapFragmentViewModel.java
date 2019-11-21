package bz.pei.driver.ui.drawerscreen.fragmentz;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.gson.Gson;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubMapService;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.DriverModel;
import bz.pei.driver.retro.responsemodel.HeatMapObject;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import io.socket.client.Socket;

/**
 * Created by root on 10/11/17.
 */
public class MapFragmentViewModel extends BaseNetwork<RequestModel, MapNavigator> implements GoogleApiClient.ConnectionCallbacks,
        LocationListener, OnMapReadyCallback {
    private static final String TAG = "MapFragment";
    public static ObservableField<LatLng> mMapLatLng = new ObservableField<>();
    private static ObservableBoolean zoomset = new ObservableBoolean(false);
    public ObservableField<String> availableUnavailable = new ObservableField<>("offline");
    public ObservableField<String> stsShareRide = new ObservableField<>("Share state on");
    public ObservableBoolean availableDriver = new ObservableBoolean(true);
    public ObservableBoolean isHeatmapSet = new ObservableBoolean(false);
    public ObservableBoolean driverisShare = new ObservableBoolean(false);
    private static GoogleMap googleMap;
    private static GitHubService gitHubService;
    private static GitHubMapService gitgoogle;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private Location locationNew, locationOld;
    private static Context context;
    private static SharedPrefence sharedPrefence;
    //    private io.socket.client.Socket mSocket;
    private String driver_ID, driverToken;
    private String bearing, id, lat, lng;
    // Schedule the task such that it will be executed every second
    ScheduledFuture<?> scheduledFuture;


    List<LatLng> heatMapLocations = new ArrayList<>();
    /**
     * Alternative radius for convolution
     */
    private static final int ALT_HEATMAP_RADIUS = 30;
    /**
     * Alternative opacity of heatmap overlay
     */
    private static final double ALT_HEATMAP_OPACITY = 0.4;
    /**
     * Alternative heatmap gradient (blue -> red)
     * Copied from Javascript version
     */
    private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
//            Color.argb(0, 255, 255, 0),// transparent
//            Color.argb(255 / 3 * 2, 0, 255, 0),
            Color.rgb(0, 255, 0),
            Color.rgb(255, 0, 0),
//            Color.rgb(0, 255, 0)
    };
    /*private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.argb(0, 0, 255, 255),// transparent
            Color.argb(255 / 3 * 2, 0, 255, 255),
            Color.rgb(0, 191, 255),
            Color.rgb(0, 0, 127),
            Color.rgb(255, 0, 0)
            };

            Color.argb(0, 0, 255, 255),// transparent
            Color.argb(255 / 3 * 2, 0, 255, 0),
            Color.rgb(0, 255, 0),
            Color.rgb(255, 0, 0),
            Color.rgb(0, 255, 0)*/
    public static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = {
//            0.0f, 0.10f, 0.20f, 0.60f, 1.0f
            0.2f, 1.0f
    };

    public static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
            ALT_HEATMAP_GRADIENT_START_POINTS);

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private ObservableBoolean heatmapSet = new ObservableBoolean(false);
    private boolean mDefaultRadius = true;
    private boolean mDefaultOpacity = true;

    /**
     * swith map view to Heatmap View and remove Hetamap
     */
    public void switchHeatMap() {
        if (googleMap != null && mMapLatLng.get() != null) {
            if (heatMapLocations != null && heatMapLocations.size() > 0)
                if (!heatmapSet.get()) {
                    mProvider = new HeatmapTileProvider.Builder().data(heatMapLocations).build();
                    mOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                    mProvider.setGradient(ALT_HEATMAP_GRADIENT);
                    mProvider.setRadius(ALT_HEATMAP_RADIUS);
                    mProvider.setData(heatMapLocations);
                    mOverlay.clearTileCache();
//                    heatmapSet.set(true);
                } /*else {
                    mOverlay.remove();
                    heatmapSet.set(false);
                }*/
        }
    }

    public MapFragmentViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                @Named(Constants.googleMap) GitHubMapService googlegit,
                                SharedPrefence sharedPrefence,
                                Gson gson, DrawerAct drawerAct,
                                Socket mSocket) {
        super(gitHubService, sharedPrefence, gson);
        Log.d("keys--123"," MapFragViewModel123");
        this.gitHubService = gitHubService;
        this.gitgoogle = googlegit;
        this.sharedPrefence = sharedPrefence;
        if (!TextUtils.isEmpty(sharedPrefence.Getvalue(SharedPrefence.Lat)))
            mMapLatLng.set(new LatLng(Double.parseDouble(sharedPrefence.Getvalue(SharedPrefence.Lat)),
                    Double.parseDouble(sharedPrefence.Getvalue(SharedPrefence.Lng))));
        context = drawerAct;
        getDriverDetails();
    }

    private void getDriverDetails() {
        if (sharedPrefence != null && driver_ID == null) {
            driver_ID = sharedPrefence.getDRIVER_ID();
        }

    }


    @BindingAdapter({"initMap"})
    public static void initMap(MapView mapView, final LatLng latLng) {


        if (mapView != null && latLng != null) {


            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googlMap) {
                    googleMap = googlMap;

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    if (!googleMap.isTrafficEnabled())
                        googlMap.setTrafficEnabled(true);
                    if (driverMarker != null) {
                        if (driverMarker.isVisible())
                            driverMarker.remove();
                        driverMarker = null;
                    }
                    AnimationMarker(latLng);

                }
            });


        }
    }

    public void setupShare() {
        if (getmNavigator().isNetworkConnected()) {
            HashMap<String, String> map = new HashMap<>();
            String driverDetails = sharedPrefence.Getvalue(SharedPrefence.DRIVERERDETAILS);
            if (!TextUtils.isEmpty(driverDetails)) {
                DriverModel driverModel = CommonUtils.getSingleObject(driverDetails, DriverModel.class);
                if (driverModel != null) {
                    map.put(Constants.NetworkParameters.id, driverModel.id + "");
                    map.put(Constants.NetworkParameters.token, driverModel.token);
                }
            }
            getShrareStatusDriver(map);
        }
    }

    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        switch ((int) taskId) {
            case Constants.TaskID.TOGGLE_STATE:
                setIsLoading(false);
                if (response.success) {
                    if (response.successMessage.contains("status_updated")) //"Status Updated"))
                        if (response.driver != null)
                            if (response.driver.is_active >= 0) {
                                changeDriverStatus(response.driver.is_active);
                            }
                } else
                    getmNavigator().showMessage(response.errorMessage);
                break;
            case Constants.TaskID.GET_HEATMAP_DATA:
                setIsLoading(false);
                if (response.success) {
                    if (response.successMessage.contains("heat_map"))
                        if (response.heatmap_list != null) {
                            for (HeatMapObject heatMapObject : response.heatmap_list)
                                if (heatMapObject != null && heatMapObject.getLatLng() != null)
                                    heatMapLocations.add(heatMapObject.getLatLng());
                            switchHeatMap();
                            isHeatmapSet.set(true);
                        }
//                    setupShare();
                }
                break;

            case Constants.TaskID.TOGGLE_SHARE:
                setIsLoading(false);
                if (response.success) {
                    if (response.successMessage.contains("driver_toogle_shared_state_successfully"))
                        //"Status Updated"))
                        if (response.driver != null)
                            if (response.driver.share_state >= 0) {
                                changeShareState(response.driver.share_state);
                                sharedPrefence.savebooleanValue(Constants.EXTRA_Data, true);
                            }
                } else
                    getmNavigator().showMessage(response.errorMessage);
                break;
            case Constants.TaskID.SHARE_STATE:
                setIsLoading(false);
                if (response.successMessage.contains("driver_toogle_shared_state_get_successfully"))
                    //"Status Updated"))
                    if (response != null)
                        if (response.driver.share_state >= 0) {
                            changeShareState(response.driver.share_state);
                        }
                break;
        }
    }

    public void changeDriverStatus(int is_active) {
        availableUnavailable.set(is_active == 1 ? context.getString(R.string.text_offline) : context.getString(R.string.text_online));
        availableDriver.set(is_active == 1 ? true : false);
        getmNavigator().toggleOflineOnline(is_active == 1);
        if (is_active == 1) {
            startLocationUpdate();
        } else {
            changeShareState(0);
            stopLocationUpdate(true);
        }
    }

    public void changeShareState(int is_share) {
//        stsShareRide.set(is_share == 1 ? context.getString(R.string.text_shareOff) : context.getString(R.string.text_shareOn));
        driverisShare.set(is_share == 1 ? true : false);

    }


    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        switch ((int) taskId) {
            case Constants.TaskID.TOGGLE_STATE:
                getmNavigator().showMessage(e.getMessage());
                if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED ||
                        e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED ||
                        e.getCode() == Constants.ErrorCode.INVALID_LOGIN)
                    ((DrawerAct) context).logoutApp();
                break;
            default:
                if (getmNavigator() != null && getmNavigator().getAttachedcontext() != null)
                    getmNavigator().showMessage(getmNavigator().getAttachedcontext().getString(R.string.text_error_contact_server));
                break;
        }
    }

    @Override
    public HashMap<String, String> getMap() {
        HashMap<String, String> map = new HashMap<>();
        String driverDetails = sharedPrefence.Getvalue(SharedPrefence.DRIVERERDETAILS);
        if (!TextUtils.isEmpty(driverDetails)) {
            DriverModel driverModel = CommonUtils.getSingleObject(driverDetails, DriverModel.class);
            if (driverModel != null) {
                map.put(Constants.NetworkParameters.id, driverModel.id + "");
                map.put(Constants.NetworkParameters.token, driverModel.token);
            }
        }
        if (locationNew != null) {
            map.put(Constants.NetworkParameters.current_latitude, locationNew.getLatitude() + "");
            map.put(Constants.NetworkParameters.current_longitude, locationNew.getLongitude() + "");
        } else {
            map.put(Constants.NetworkParameters.current_latitude, "0.0");
            map.put(Constants.NetworkParameters.current_longitude, "0.0");
        }
        return map;
    }

    private static Marker driverMarker;


    private static void AnimationMarker(LatLng latLng) {
//        if (googleMap != null)
//            googleMap.clear();
        if (latLng != null && googleMap != null) {
            float bearing_nw = TextUtils.isEmpty(sharedPrefence.Getvalue(SharedPrefence.BEARING)) ? 0f : Float.valueOf(sharedPrefence.Getvalue(SharedPrefence.BEARING));
            if (driverMarker == null)
                driverMarker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("currentlocation")
                        .rotation(bearing_nw)
//                    .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_driver)));
            else {
                driverMarker.setPosition(latLng);
                driverMarker.setRotation(bearing_nw);
            }
            if (!zoomset.get()) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        latLng, 18));
//                zoomset.set(true);
            } /*else {
                CameraUpdateFactory.newLatLngZoom(latLng, 15);
            }*/
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            changeMapStyle();
            startLocationUpdate();
            locationNew = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (!isHeatmapSet.get())
                getHeaTData();
            if (locationNew != null) {
                mMapLatLng.set(new LatLng(locationNew.getLatitude(), locationNew.getLongitude()));
                if (locationOld == null) {
                    locationOld = locationNew;
                }
                CameraUpdateFactory.newLatLngZoom(mMapLatLng.get(), 15);
//                doWorkWithNewLocation(locationNew, locationNew.bearingTo(locationOld));
                if (!sharedPrefence.getbooleanvalue(SharedPrefence.IS_OFFLINE))
                    sendLocation(locationNew, locationNew.bearingTo(locationOld));

            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void changeMapStyle() {
        if (googleMap == null)
            return;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json);
        googleMap.setMapStyle(style);
    }

    /**
     * Make use of location after deciding if it is better than previous one.
     *
     * @param location Newly acquired location.
     * @param bearing
     */
//    void doWorkWithNewLocation(Location location, float bearing) {
//        if (isBetterLocation(locationOld, location)) {
    // If location is better, do some user preview.
//        sendLocation(location,bearing);
//        } else {
//            sendLocation(locationOld);
//        }
//    }

    /**
     * Time difference threshold set for one minute.
     */
    static final int TIME_DIFFERENCE_THRESHOLD = 1 * 60 * 1000;

    /**
     * Decide if new location is better than older by following some basic criteria.
     * This algorithm can be as simple or complicated as your needs dictate it.
     * Try experimenting and get your best location strategy algorithm.
     *
     * @param oldLocation Old location used for comparison.
     * @param newLocation Newly acquired location compared to old one.
     * @return If new location is more accurate and suits your criteria more than the old one.
     */
    boolean isBetterLocation(Location oldLocation, Location newLocation) {
        // If there is no old location, of course the new location is better.
        if (oldLocation == null) {
            return true;
        }
        // Check if new location is newer in time.
        boolean isNewer = newLocation.getTime() > oldLocation.getTime();
        // Check if new location more accurate. Accuracy is radius in meters, so less is better.
        boolean isMoreAccurate = newLocation.getAccuracy() < oldLocation.getAccuracy();
        if (isMoreAccurate && isNewer) {
            // More accurate and newer is always better.
            return true;
        } else if (isMoreAccurate && !isNewer) {
            // More accurate but not newer can lead to bad fix because of user movement.
            // Let us set a threshold for the maximum tolerance of time difference.
            long timeDifference = newLocation.getTime() - oldLocation.getTime();
            // If time difference is not greater then allowed threshold we accept it.
            if (timeDifference > -TIME_DIFFERENCE_THRESHOLD) {
                return true;
            }
        }

        return false;
    }


    public void sendLocation(Location locatION, float bearing) {
        sharedPrefence.savevalue(SharedPrefence.Lat, locatION.getLatitude() + "");
        sharedPrefence.savevalue(SharedPrefence.Lng, locatION.getLongitude() + "");
        sharedPrefence.savevalue(SharedPrefence.BEARING, bearing + "");
        lat = locatION.getLatitude() + "";
        lng = locatION.getLongitude() + "";
        this.bearing = bearing + "";
        locationOld = locatION;
        if (driver_ID == null)
            getDriverDetails();
        sendSocketLocationMessage(locatION.getLatitude() + "", locatION.getLongitude() + "", driver_ID, "" + bearing);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!availableDriver.get())
            stopLocationUpdate(true);
        locationNew = location;

        if (locationOld == null) {
            locationOld = locationNew;
        }
        AnimationMarker(new LatLng(locationNew.getLatitude(), locationNew.getLongitude()));
        mMapLatLng.set(new LatLng(locationNew.getLatitude(), locationNew.getLongitude()));
        Log.d("MapViewModelLat", location.getLatitude() + " Lng:" + location.getLongitude() + " Bearing:" + (location.hasBearing() ? location.getBearing() : locationNew.bearingTo(locationOld)));
        if (sharedPrefence.getIntvalue(SharedPrefence.REQUEST_ID) != Constants.NO_REQUEST) {
            stopLocationUpdate(true);
        } else
            sendLocation(location, (location.hasBearing() ? location.getBearing() : locationNew.bearingTo(locationOld)));//location.getBearing());
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void buildGoogleApiClient() {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                /*.addOnConnectionFailedListener(this)*/
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    public void startLocationUpdate() {
        if (!mGoogleApiClient.isConnected()) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started.......................");
        getmNavigator().startSocket();
        setupPeriodicUpdate();
    }


    public void stopLocationUpdate(boolean isManual) {
        if (isManual)
            getmNavigator().stopSocket();
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
        stopPeriodicUpdate();
    }

    public void disconnectGoogleApiClient() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void sendSocketLocationMessage(String lat, String lng, String id, String bearing) {
        getmNavigator().sendLocations(id, lat, lng, bearing);
    }

    public void setupStatusAtStatup(int status) {
        availableUnavailable.set(status == 1 ? context.getString(R.string.text_offline) : context.getString(R.string.text_online));
        availableDriver.set(status == 1 ? true : false);
    }

    /**
     * Toggle Offline and online of driver
     */
    public void changeStatusString(View view) {
//        availableUnavailable.set(availableUnavailable.get().equalsIgnoreCase(context.getString(R.string.text_offline)) ? context.getString(R.string.text_online) : context.getString(R.string.text_offline));
        if (getmNavigator().isNetworkConnected())
            if (getMap().size() > 0) {
                setIsLoading(true);
                toggleOfflineOnline(getMap());
            }
    }

    public void changeShareOnOff(View view) {
        if (getmNavigator().isNetworkConnected())
            if (getMap().size() > 0) {
                setIsLoading(true);
                toggleONOFFShare(getMap());
            }
    }

    /**
     * click of current location Button
     */
    public void focusCurrentLocation(View view) {
        if (googleMap != null && mMapLatLng.get() != null) {
            CameraPosition newCamPos = new CameraPosition(mMapLatLng.get(),
                    15.5f,
                    googleMap.getCameraPosition().tilt, //use old tilt
                    googleMap.getCameraPosition().bearing); //use old bearing
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 18, null);
        }
    }

    private void setupPeriodicUpdate() {
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        scheduledFuture =
                service.scheduleAtFixedRate(typesRunnable, Constants.INITIAL_DELAY, Constants.UPDATE_INTERVAL, TimeUnit.SECONDS);
    }

    private void stopPeriodicUpdate() {
        if (scheduledFuture != null)
            if (!scheduledFuture.isCancelled())
                scheduledFuture.cancel(true);
    }

    Runnable typesRunnable = new Runnable() {
        @Override
        public void run() {
            if (sharedPrefence.getbooleanvalue(SharedPrefence.IS_OFFLINE))
                return;
            if (CommonUtils.IsEmpty(driver_ID) && CommonUtils.IsEmpty(SharedPrefence.DRIVER_TOKEN)) {
                driver_ID = sharedPrefence.getDRIVER_ID();
                driverToken = sharedPrefence.getDeviceToken();
            }
            if (sharedPrefence.getIntvalue(SharedPrefence.REQUEST_ID) != Constants.NO_REQUEST) {
                stopLocationUpdate(true);
            } else {
                if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng) && !TextUtils.isEmpty(bearing))
                    sendSocketLocationMessage(lat, lng, driver_ID, bearing);
            }
        }
    };
}
