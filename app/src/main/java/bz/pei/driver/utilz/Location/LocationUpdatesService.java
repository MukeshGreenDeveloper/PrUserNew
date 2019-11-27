/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bz.pei.driver.utilz.Location;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import bz.pei.driver.app.MyApp;
import bz.pei.driver.fcm.MyFirebaseMessagingService;
import bz.pei.driver.R;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.Sync.SyncRestartReceiver;
import bz.pei.driver.Sync.SyncUtils;
import bz.pei.driver.ui.acceptreject.AcceptRejectActivity;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.NetworkUtils;
import bz.pei.driver.utilz.SharedPrefence;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

/**
 * A bound and started service that is promoted to a foreground service when location updates have
 * been requested and all clients unbind.
 * <p>
 * For apps running in the background on "O" devices, location is computed only once every 10
 * minutes and delivered batched every 30 minutes. This restriction applies even to apps
 * targeting "N" or lower which are run on "O" devices.
 * <p>
 * This sample show how to use a long-running service for location updates. When an activity is
 * bound to this service, frequent location updates are permitted. When the activity is removed
 * from the foreground, the service promotes itself to a foreground service, and location updates
 * continue. When the activity comes back to the foreground, the foreground service stops, and the
 * notification assocaited with that service is removed.
 */
public class LocationUpdatesService extends Service {

    private static final String PACKAGE_NAME = "bz.pei.driver.utilz.Location";
    private static final String TAG = "LocationUpdatesService";
    static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";
    private final IBinder mBinder = new LocalBinder();
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long INTERVAL = 1000 * 5;
    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_INTERVAL = 1000 * 3;

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;


    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    private Handler mServiceHandler;

    /**
     * Socket
     */
    Socket mSocket = null;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor shardPrefEditor;
    public int userID, isTripStarted, isShareRide;
    public long lastUpdated = System.currentTimeMillis();
    GitHubService apiService;
    Gson gson = new Gson();
    public String id, lat, lng, bearing;
    // Schedule the task such that it will be executed every second
    ScheduledFuture<?> scheduledFuture;
    public Context applicationContext;

    void initializeSharedPreferences(Context application) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        shardPrefEditor = sharedPreferences.edit();
        applicationContext = application;
    }

    void initializeAPI(Context application) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        okhttpbuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                return chain.proceed(request);
            }
        });
        okhttpbuilder.readTimeout(60, TimeUnit.SECONDS);
        okhttpbuilder.connectTimeout(60, TimeUnit.SECONDS);
        okhttpbuilder.addInterceptor(logging);
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okhttpbuilder.build())
                .baseUrl(Constants.URL.BaseURL);
        apiService = builder.build().create(GitHubService.class);
    }

    /**
     * The current location.
     */
    private Location locationNew, locationOld;

    public LocationUpdatesService() {
    }

    @Override
    public void onCreate() {
        startServiceOreoCondition();
        if (sharedPreferences == null)
            initializeSharedPreferences(getApplication());
        if (apiService == null)
            initializeAPI(getApplication());
        if (!CommonUtils.checkLocationorGPSEnabled()) {
            MyFirebaseMessagingService.displayNotificationConnectivity(MyApp.getmContext(), MyApp.getmContext().getString(R.string.text_location_notification_hint));
        }
        CommonUtils.setwifilock(getApplication());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (!CommonUtils.checkLocationorGPSEnabled()) {
                    MyFirebaseMessagingService.displayNotificationConnectivity(MyApp.getmContext(), MyApp.getmContext().getString(R.string.text_location_notification_hint));
                } else if (!SyncUtils.checkIfSyncEnabled(MyApp.getmContext())) {
                    if (MyApp.getmContext() != null)
                        MyFirebaseMessagingService.displayNotificationConnectivity(MyApp.getmContext(), MyApp.getmContext().getString(R.string.text_sync_disabled));
                } else
                    MyFirebaseMessagingService.cancelConnectivityNotification(MyApp.getmContext());
                if (sharedPreferences.getBoolean(SharedPrefence.IS_OFFLINE, false))
                    offSocket();
                else
                    onNewLocation(locationResult.getLastLocation());
            }
        };
        if (sharedPreferences.getBoolean(SharedPrefence.IS_OFFLINE, false))
            offSocket();
        createLocationRequest();
        getLastLocation();
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "keys----Service started");
        Log.i("keys---", "here -LocationUpdateService!");
        if (intent == null) {
            removeLocationUpdates();
            stopSelf();
            return START_NOT_STICKY;
        }
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration) {
            Log.i(TAG, "Starting foreground service");

//            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "-keys----- on Destroy");
//        super.onDestroy();
        mServiceHandler.removeCallbacksAndMessages(null);
//        offSocket();
        Intent broadcastIntent = new Intent(this, SyncRestartReceiver.class);
        broadcastIntent.setAction("restartservice");
        sendBroadcast(broadcastIntent);
        CommonUtils.setwifilock(getApplication());
        if (scheduledFuture != null)
            if (!scheduledFuture.isCancelled())
                scheduledFuture.cancel(true);
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates");
//        startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
        if (sharedPreferences == null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.getBoolean(SharedPrefence.IS_OFFLINE, false))
            offSocket();
        else if (MyApp.isIsDrawerActivityDestroyed())
            initiateSocket();
    }


    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            stopSelf();
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }


    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                locationNew = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    double lastBearing=0,currentBearing=0;
    private void onNewLocation(Location location) {
        Log.i(TAG, "New location: " + location);
        locationNew = location;
        if (locationOld == null) {
            locationOld = locationNew;
        }
        if (CommonUtils.IsEmpty(sharedPreferences.getString(SharedPrefence.DRIVER_ID, ""))) {
//            stopSelf();
            return;
        }
        if (CommonUtils.IsEmpty(sharedPreferences.getString(SharedPrefence.DRIVER_TOKEN, "")))
            return;

        if (!TextUtils.isEmpty(sharedPreferences.getString(SharedPrefence.DRIVER_ID, "")) && !TextUtils.isEmpty(sharedPreferences.getString(SharedPrefence.DRIVER_TOKEN, ""))) {

            currentBearing=locationOld.bearingTo(locationNew);
            Log.d("keys-Data","Old="+lastBearing+"New="+((currentBearing==lastBearing)?((currentBearing+1)+""):(currentBearing+"")));
            if(lastBearing==0)
                lastBearing=currentBearing;
            Log.d("keys-Data","Old="+lastBearing+"New="+((currentBearing==lastBearing)?((currentBearing+1)+""):(currentBearing+"")));
            sendSocketLocationMessage(location.getLatitude() + "",
                    location.getLongitude() + "",
                    sharedPreferences.getString(SharedPrefence.DRIVER_ID, ""),
                    sharedPreferences.getString(SharedPrefence.DRIVER_TOKEN, ""),
                    (currentBearing==lastBearing)?((currentBearing+1)+""):(currentBearing+""));
            lastBearing=currentBearing;
        }
        id = sharedPreferences.getString(SharedPrefence.DRIVER_ID, "");
        lat = location.getLatitude() + "";
        lng = location.getLongitude() + "";
        bearing = locationOld.bearingTo(locationNew) + "";
        locationOld = locationNew;
        if (sharedPreferences.getInt(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST) != Constants.NO_REQUEST) {
            Intent intent = new Intent(Constants.BroadcastsActions.LOCATION_UPDATING_SERVICE);
            intent.putExtra(EXTRA_LOCATION, location);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        requestLocationUpdates();
        initializeSharedPreferences(getApplication());
        if (apiService == null)
            initializeAPI(getApplication());
        if (mSocket == null) {
            try {
                mSocket = IO.socket(Constants.URL.SOCKET_URL);
                initiateSocket();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        setupPeriodicUpdate();
    }

    //    Socket
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject object = new JSONObject();
            try {
                if (!CommonUtils.IsEmpty(sharedPreferences.getString(SharedPrefence.DRIVER_ID, ""))) {
                    object.put(Constants.NetworkParameters.id, sharedPreferences.getString(SharedPrefence.DRIVER_ID, ""));
                    if (NetworkUtils.isNetworkConnected(MyApp.getmContext()))
                        mSocket.emit(Constants.NetworkParameters.START_CONNECT, object.toString());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("Keys", TAG + "_onConnect:" + object);
        }

    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("Keys", TAG + "_onDisconnec" + (args.length > 0 ? args[0] : ""));
        }
    };
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("Keys", TAG + "_onConnectError" + (args.length > 0 ? args[0] : ""));
        }
    };

    public void initiateSocket() {
        if (sharedPreferences.getBoolean(SharedPrefence.IS_OFFLINE, false))
            return;
        if (!MyApp.isIsDrawerActivityDestroyed())
            return;
        try {

            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on(Constants.NetworkParameters.DRIVER_REQUEST, driverRequest);
            if (!mSocket.connected())
                mSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void offSocket() {
        if (mSocket == null)
            return;
//        if (mSocket.connected())
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off(Constants.NetworkParameters.DRIVER_REQUEST, driverRequest);
    }

    public void sendLocation(String lat, String lng, String bearing, String id, String token) {
        if (sharedPreferences.getBoolean(SharedPrefence.IS_OFFLINE, false))
            return;
        Log.e(TAG, "Keys______________-UpdateStarted---Drawer = " + MyApp.isIsDrawerActivityDestroyed() + "  bearing = " + bearing);
        if (!MyApp.isIsDrawerActivityDestroyed()) {

            Intent intent = new Intent(Constants.BroadcastsActions.LOCATION_UPDATING_SERVICE);
            intent.setAction(Constants.BroadcastsActions.LOCATION_UPDATING_SERVICE);
            intent.putExtra(Constants.IntentExtras.LOCATION_ID, id);
            intent.putExtra(Constants.IntentExtras.LOCATION_LAT, lat);
            intent.putExtra(Constants.IntentExtras.LOCATION_LNG, lng);
            intent.putExtra(Constants.IntentExtras.LOCATION_BEARING, bearing);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//            }
            offSocket();
        } else {

            if (!mSocket.connected())
                initiateSocket();
            if (!CommonUtils.checkLocationorGPSEnabled(this)) {
                MyFirebaseMessagingService.displayNotificationConnectivity(this, getString(R.string.text_location_notification_hint));
                return;
            }
            if (lastUpdated == 0 || ((System.currentTimeMillis() - lastUpdated) >= Constants.MinRequestTime)) {
                if (apiService != null
                        && !CommonUtils.IsEmpty(id) && !CommonUtils.IsEmpty(token)
                        && !CommonUtils.IsEmpty(lat) && !CommonUtils.IsEmpty(lng)
                        && !CommonUtils.IsEmpty(lat) && !CommonUtils.IsEmpty(bearing)) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(Constants.NetworkParameters.id, id);
                    map.put(Constants.NetworkParameters.token, token);
                    map.put(Constants.NetworkParameters.LAT, lat);
                    map.put(Constants.NetworkParameters.LNG, lng);
                    map.put(Constants.NetworkParameters.BEARING, bearing);
                    try {
                        map.put(Constants.NetworkParameters.RANDOM, (Math.random() * 49 + 1) + "");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.e(TAG, "Keys______________-UpdateStarted---Socket= " + mSocket.connected() + "  Time-" + (System.currentTimeMillis() - lastUpdated) / 1000);
                    if (!CommonUtils.isNetworkConnected(this))
                        MyFirebaseMessagingService.displayNotificationConnectivity(MyApp.getmContext(), MyApp.getmContext().getString(R.string.text_enable_internet));
                    else
                        apiService.sendLocation(map).enqueue(new Callback<RequestModel>() {
                            @Override
                            public void onResponse(Call<RequestModel> call, retrofit2.Response<RequestModel> response) {
                                if (response.isSuccessful() && response.body() != null)
                                    if (response.body() != null)
                                        if (response.body().current_request != null && response.body().current_request.requestId != null) {
                                            response.body().request = response.body().current_request;
                                            if (sharedPreferences == null)
                                                initializeSharedPreferences(MyApp.getmContext());
                                            if (apiService == null)
                                                initializeAPI(getApplication());
                                            if (sharedPreferences.getInt(SharedPrefence.LAST_REQUEST_ID, Constants.NO_REQUEST) != response.body().request.id)
                                                openAcceptReject(response.body(), CommonUtils.getStringFromObject(response.body()));
                                        }
                            }

                            @Override
                            public void onFailure(Call<RequestModel> call, Throwable t) {
                            }
                        });
                }
                SyncUtils.syncSendData("");
                lastUpdated = System.currentTimeMillis();
            }
        }
    }

    public void sendTripLocation(String bearing, String jsonObject) {
        Log.d(TAG, "Keys______________-" + jsonObject);
        if (MyApp.isIsDrawerActivityDestroyed()) {
            if (!mSocket.connected())
                initiateSocket();
            else
                mSocket.emit(Constants.NetworkParameters.TRIP_LOCATION, jsonObject);

            SyncUtils.syncSendData(jsonObject);
        }
    }

    public void sendSocketLocationMessage(String lat, String lng, String id, String token, String bearing) {
        JSONObject object = new JSONObject();
        try {
            object.put(Constants.NetworkParameters.id, id);
            object.put(Constants.NetworkParameters.LAT, lat);
            object.put(Constants.NetworkParameters.LNG, lng);
            object.put(Constants.NetworkParameters.BEARING, bearing);
            if (sharedPreferences.getInt(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST) != Constants.NO_REQUEST) {
                userID = sharedPreferences.getInt(SharedPrefence.USER_ID, Constants.NO_ID);
                isTripStarted = sharedPreferences.getInt(SharedPrefence.TRIP_START, Constants.NO_ID);
                isShareRide = sharedPreferences.getInt(SharedPrefence.IS_SHARE, Constants.NO_ID);
                if (userID != Constants.NO_ID)
                    object.put(Constants.NetworkParameters.USER_ID, "" + userID);
                if (isTripStarted != Constants.NO_ID)
                    object.put(Constants.NetworkParameters.TRIP_START, "" + isTripStarted);
                if (isShareRide != Constants.NO_ID)
                    object.put(Constants.NetworkParameters.IS_SHARE, "" + isShareRide);
                object.put(Constants.NetworkParameters.REQUEST_ID, sharedPreferences.getInt(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST));
                sendTripLocation(bearing, object + "");
            } else
                sendLocation(lat, lng, bearing, id, token);
            if (sharedPreferences != null) {
                sharedPreferences.edit().putString(SharedPrefence.Lat, lat);
                sharedPreferences.edit().putString(SharedPrefence.Lng, lat);
                sharedPreferences.edit().putString(SharedPrefence.BEARING, bearing);
            }
            lastUpdated = System.currentTimeMillis();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public static boolean serviceIsRunningInForeground(Context context, Class className) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (className.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        applicationContext = getApplicationContext();
        Intent restartServiceIntent = new Intent(applicationContext, this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(applicationContext, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmService.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartServicePendingIntent);
            alarmService.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartServicePendingIntent);
        } else
            alarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartServicePendingIntent);

        Log.d("keys---", "keys----Removed----" + TAG);
        super.onTaskRemoved(rootIntent);
    }

    private Emitter.Listener driverRequest = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("Key_receivedNewRequest:", "" + (args.length > 0 ? args[0] : ""));
//            offSocket();
            if (args.length > 0) {
                RequestModel model = CommonUtils.getSingleObject(args[0] + "", RequestModel.class);
                if (model != null)
                    if (model.request != null) {
                        if (sharedPreferences == null)
                            initializeSharedPreferences(MyApp.getmContext());
                        if (apiService == null)
                            initializeAPI(getApplication());
                        if (sharedPreferences.getInt(SharedPrefence.LAST_REQUEST_ID, Constants.NO_REQUEST) != model.request.id)
                            openAcceptReject(model, args[0] + "");
                    }

            }
        }
    };

    private void openAcceptReject(RequestModel model, String message) {
        applicationContext = MyApp.getmContext();
        shardPrefEditor.putInt(SharedPrefence.LAST_REQUEST_ID, model.request.id);
        if (!MyApp.isIsAcceptRejectActivityVisible()) {
            Log.d("keys", "(!BaseActivity.isRunning(MyApp.getmContext())) = " + (!BaseActivity.isRunning(applicationContext)));
            Intent acceptRejectIntent = new Intent(applicationContext, AcceptRejectActivity.class);
            acceptRejectIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            acceptRejectIntent.putExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA, message);
            applicationContext.startActivity(acceptRejectIntent);
        }
    }

    private void startServiceOreoCondition() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID,
                    Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                    .setCategory(Notification.CATEGORY_SERVICE).setSmallIcon(R.drawable.ic_small_logo).setPriority(PRIORITY_MIN).
                            setContentTitle("PEI Taxi running in background").build();
            startForeground(101, notification);
        } else {

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, Constants.CHANNEL_ID);
            notificationBuilder.setContentTitle("PEI Taxi")
                    .setContentText("PEI Taxi running in background")
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                    .setSmallIcon(R.drawable.ic_small_logo).setPriority(PRIORITY_MIN);
            startForeground(101, notificationBuilder.build());
        }
    }

    private void setupPeriodicUpdate() {
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        scheduledFuture =
                service.scheduleAtFixedRate(typesRunnable, Constants.INITIAL_DELAY, Constants.UPDATE_INTERVAL, TimeUnit.SECONDS);
    }

    Runnable typesRunnable = new Runnable() {
        @Override
        public void run() {
            if (sharedPreferences.getBoolean(SharedPrefence.IS_OFFLINE, false))
                return;
            if (!CommonUtils.IsEmpty(sharedPreferences.getString(SharedPrefence.DRIVER_ID, "")) && !CommonUtils.IsEmpty(sharedPreferences.getString(SharedPrefence.DRIVER_TOKEN, "")))

                if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng) && !TextUtils.isEmpty(bearing))
                    sendSocketLocationMessage(lat, lng, sharedPreferences.getString(SharedPrefence.DRIVER_ID, ""), sharedPreferences.getString(SharedPrefence.DRIVER_TOKEN, ""), bearing);
        }
    };

}
