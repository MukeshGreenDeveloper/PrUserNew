package bz.pei.driver.ui.drawerscreen.fragmentz.multipleshareride;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.ObservableField;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
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
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MultipleShareRideViewModel extends BaseNetwork<RequestModel, MultipleShareRideNavigator> implements
        GoogleApiClient.ConnectionCallbacks, LocationListener {
    public static ObservableField<LatLng> mMapLatLng = new ObservableField<>();
    private static SharedPrefence sharedPrefence;
    private static GoogleMap googleMap;
    private GitHubService gitHubService;
    private GitHubMapService gitHubMapService;
    private Socket socket;
    public ObservableField<LatLng> currentLatlng = new ObservableField<>();
    private static Context context;
    HashMap<String, String> map;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private Location locationNew;
    private PolylineOptions options;
    private List<RequestModel.Request> mShareRequest;

    public MultipleShareRideViewModel(GitHubService gitHubService,
                                      GitHubMapService gitHubMapService, SharedPrefence sharedPrefence, Gson gson,
                                      Socket socket, LocationRequest locationRequest, DrawerAct drawerAct, HashMap<String, String> map) {
        super(gitHubService);
        this.gitHubService = gitHubService;
        this.gitHubMapService = gitHubMapService;
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
        this.socket = socket;
        context = drawerAct;
        this.map = map;

    }

    private void createPath() {

        map.clear();
        map.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        setIsLoading(true);
        GetDriverShareRequestInProgress(map);


    }

    @Override
    public HashMap<String, String> getMap() {
        return map;
    }

    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        setIsLoading(false);
        if (response.success) {
            if (response.successMessage.equalsIgnoreCase("driver share request inprogress")) {
                if (response.share_request != null)
                    drawPath(response.share_request);
            }
        }

    }

    private void drawPath(List<RequestModel.Request> share_request) {

        mShareRequest=share_request;
        ArrayList<Place> places = new ArrayList<Place>();
        String wayPoints = "";
        for (RequestModel.Request share : share_request) {
            LatLng latLng1 = new LatLng(share.dropLatitude, share.dropLongitude);
            Place place = new Place();
            place.latLng = latLng1;
            place.location = share.dropLocation;
            places.add(place);

            if (share.is_trip_start==0) {
                wayPoints = wayPoints + share.pickLatitude + "," + share.pickLongitude+ "|";
                LatLng latLng = new LatLng(share.pickLatitude, share.pickLongitude);
                Marker dropMarker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(share.pickLocation)
                        .anchor(0.5f, 0.5f)
                        .icon(getmNavigator().getMarkerIcon(R.drawable.ic_pickup)));

            } else {

            }

        }


        for (Place p : places) {
            Log.i("Places before sorting", "Place: " + p.latLng);
            System.out.println("MultipleShareRideViewModel.drawPath1===> " + p.latLng);
        }

        //sort the list, give the Comparator the current location
        Collections.sort(places, new SortPlaces(mMapLatLng.get()));

        for (Place p : places) {
            wayPoints = wayPoints + p.latLng.latitude + "," + p.latLng.longitude + "|";

            LatLng latLng = new LatLng(p.latLng.latitude, p.latLng.longitude);
            Marker dropMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(p.location)
                    .anchor(0.5f, 0.5f)
                    .icon(getmNavigator().getMarkerIcon(R.drawable.ic_drop)));

            System.out.println("MultipleShareRideViewModel.drawPath2===> " + p.latLng);
        }
        String mainWayPoints = wayPoints.substring(0, wayPoints.lastIndexOf("|"));

        String destination = String.valueOf(locationNew.getLatitude()) + "," + String.valueOf(locationNew.getLongitude());
        setIsLoading(true);
        gitHubMapService.getSharePathLatLong(String.valueOf(locationNew.getLatitude()) + "," + String.valueOf(locationNew.getLongitude()),
                destination,
                mainWayPoints,
                false, Constants.SERVER_API_KEY).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!CommonUtils.IsEmpty(response.body() + "")) {
                    BeanRoute route = new BeanRoute();
                    route = CommonUtils.parseRoute(response.body() + "", route);
                    if (route != null) {

                        options = new PolylineOptions();
                        final ArrayList<BeanStep> step = route.getListStep();
//                        System.out.println("step size=====> " + step.size());
                        List<LatLng> pointslaglng = new ArrayList<LatLng>();
                        options = new PolylineOptions();

                        for (int i = 0; i < step.size(); i++) {
                            List<LatLng> path = step.get(i).getListPoints();
                            pointslaglng.addAll(path);
                        }
                        options.addAll(pointslaglng);
                        options.width(10);
                        options.geodesic(true);
                        options.color(R.color.colorPrimary); // #00008B rgb(0,0,139)
                        if (pointslaglng != null && googleMap != null) {
                            Polyline polyline = googleMap.addPolyline(options);
//                            buildLatlongBound(pickupLatLng, dropLatLng);
//                            animationMarker(currentLatlng.get());

                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                setIsLoading(false);
            }
        });


    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e.getMessage());
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

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            changeMapStyle();
            locationNew = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (locationNew != null) {
                mMapLatLng.set(new LatLng(locationNew.getLatitude(), locationNew.getLongitude()));

                CameraUpdateFactory.newLatLngZoom(mMapLatLng.get(), 15);
                animationMarker(mMapLatLng.get());
                createPath();
//                doWorkWithNewLocation(locationNew, locationNew.bearingTo(locationOld));

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

    @Override
    public void onConnectionSuspended(int i) {

    }

    public static void initMap(GoogleMap mapView, final LatLng latLng) {

        googleMap = mapView;


    }


    public static void animationMarker(LatLng latLng) {


//                CameraUpdateFactory.newLatLngZoom(latLng, 15);
        CameraPosition newCamPos = new CameraPosition(latLng,
                15.0f,
                googleMap.getCameraPosition().tilt, //use old tilt
                googleMap.getCameraPosition().bearing); //use old bearing
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 18, null);


    }


    @Override
    public void onLocationChanged(Location location) {

    }


    public void passengersOnClick(View view) {
        getmNavigator().passengersDialogs(mShareRequest);
    }
}
