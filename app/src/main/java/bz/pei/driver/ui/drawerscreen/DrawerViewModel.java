package bz.pei.driver.ui.drawerscreen;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import bz.pei.driver.app.MyApp;
import bz.pei.driver.fcm.MyFirebaseMessagingService;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubCountryCode;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.CountryCodeModel;
import bz.pei.driver.retro.responsemodel.DriverModel;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.Sync.SyncUtils;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.NetworkUtils;
import bz.pei.driver.utilz.SharedPrefence;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 10/11/17.
 */

public class DrawerViewModel extends BaseNetwork<RequestModel, DrawerNavigator> {
    String TAG = getClass().getSimpleName();
    Gson gson;
    SharedPrefence sharedPrefence;
    GitHubService gitHubService;
    GitHubCountryCode gitHubCountryCode;
    public ObservableField<String> Imageurlssadf = new ObservableField<>("");
    public ObservableField<String> Email = new ObservableField<>("");
    public ObservableField<String> firstName = new ObservableField<>("");
    public ObservableField<String> lastName = new ObservableField<>("");
    public static Context context;
    @Inject
    io.socket.client.Socket mSocket;
    private String driver_ID;
    public long lastUpdatedTime = System.currentTimeMillis();
    public boolean isDriverOffline = false;

    @Inject
    public DrawerViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                           @Named(Constants.countryMap) GitHubCountryCode gitHubCountryCode,
                           SharedPrefence sharedPrefence,
                           Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        lastUpdatedTime = System.currentTimeMillis();
        this.gitHubService = gitHubService;
        this.gitHubCountryCode = gitHubCountryCode;
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
    }

    public void setupProfile(Context context) {
        this.context = context;
        String userstr = sharedPrefence.Getvalue(SharedPrefence.DRIVERERDETAILS);

        DriverModel driver = CommonUtils.IsEmpty(userstr) ? null : gson.fromJson(userstr, DriverModel.class);

        if (driver != null) {
            if (!CommonUtils.IsEmpty(driver.firstname)) {
                firstName.set(driver.firstname);
            }
            if (!CommonUtils.IsEmpty(driver.lastname)) {
                lastName.set(driver.lastname);
            }
            if (!CommonUtils.IsEmpty(driver.email))
                Email.set(driver.email);

//            if (!CommonUtils.IsEmpty(driver.profilepic))
            Imageurlssadf.set(driver.profilepic);
        }
        driver_ID = sharedPrefence.getDRIVER_ID();
//        getRequestinProgress();
    }

    @BindingAdapter({"imageUrlDrawer"})
    public static void setImageFromUrl(ImageView v, String url) {
        if (!CommonUtils.IsEmpty(url))
            Glide.with(v.getContext().getApplicationContext())
                    .load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(v);
    }

    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        setIsLoading(false);
        if (response == null)
            getmNavigator().showMessage(" ");
        else if (response.success) {
            if (response.successMessage != null &&response.successMessage.contains(Constants.SuccessMessages.LOGOUT_Message))
                getmNavigator().logoutApp();
            else if (response.successMessage != null && response.successMessage.equalsIgnoreCase(Constants.SuccessMessages.REQUEST_IN_PROGRESS_Message)) {
                if (!CommonUtils.IsEmpty(response.customer_care_number))
                    sharedPrefence.savevalue(SharedPrefence.CUSTOMER_CARE_NO, response.customer_care_number);
                if (response.shareStatus != null && response.shareStatus) {
                    sharedPrefence.saveIntValue(SharedPrefence.IS_SHARE, 1);
                    offSocket();
                    getmNavigator().openShareFragment();
                    return;
                }

                if (response.driver_status != null) {
                    if (response.shareStatus != null)
                        sharedPrefence.savebooleanValue(Constants.ShareState, response.shareStatus);
//                        if (response.driver_status.is_approve == 1)
                    String sosstring = gson.toJson(response.sosModelList);
                    sharedPrefence.savevalue(SharedPrefence.SOSDETAILS, sosstring);
                    sharedPrefence.savebooleanValue(SharedPrefence.IS_OFFLINE, response.driver_status.is_active == 0);
                    isDriverOffline = response.driver_status.is_active == 0;
                    getmNavigator().passtoFragmentDriverOfflineOnline(response.driver_status.is_active);
                    if (response.driver_status.is_approve == 0 || response.driver_status.document_upload_status != 1) {
                        getmNavigator().openApprovalDialog(response.driver_status.document_upload_status);
                        offSocket();
                        sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
                    } else if (response.request != null) {
                        if (response.request.trip && !response.request.inprogress) {
                            sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, (int) response.request.id);
                            sharedPrefence.saveIntValue(SharedPrefence.USER_ID, response.request.user.id);
                            sharedPrefence.saveIntValue(SharedPrefence.IS_SHARE, 0);
                            sharedPrefence.saveIntValue(SharedPrefence.TRIP_START, response.request.is_trip_start);
                            offSocket();
                            if (response.request.is_completed == 1)
                                getmNavigator().openFeedbackFragment(response);
                            else
                                getmNavigator().openTripFragment(response);
                        } else {
                            sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
                            getmNavigator().openMapFragment(response.driver_status.is_active);
                            initiateSocket();
                        }
                    } else {
                        sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
                        getmNavigator().openMapFragment(response.driver_status.is_active);
                        initiateSocket();
                    }
                    if (response.driver_status.is_active == 0)
                        offSocket();

                }
            } else if (response.successMessage != null && response.successMessage.equalsIgnoreCase(Constants.SuccessMessages.LOCATION_UPDATED_SUCCESSFULLY)) {
                if (response.current_request != null && response.current_request.requestId != null) {
                    response.request = response.current_request;
                    getmNavigator().openAcceptReject(response, CommonUtils.getStringFromObject(response));
                }
            } else if (response.successMessage != null &&
                    (response.successMessage.equalsIgnoreCase("Status Updated") || response.successMessage.contains("status_updated"))) {
                if (response.driver != null)
                    if (response.driver.is_active >= 0) {
                        getmNavigator().passtoFragmentDriverOfflineOnline(response.driver.is_active);
                        sharedPrefence.savebooleanValue(SharedPrefence.IS_OFFLINE, response.driver.is_active == 0);
                    }
            } else if (response.successMessage != null &&
                    (response.successMessage.equalsIgnoreCase("Status Updated") || response.successMessage.contains("status_updated"))) {
                if (response.driver != null)
                    if (response.driver.is_active >= 0) {
                        getmNavigator().passtoFragmentDriverOfflineOnline(response.driver.is_active);
                        sharedPrefence.savebooleanValue(SharedPrefence.IS_OFFLINE, response.driver.is_active == 0);
                    }
            } else if (response.successMessage != null &&response.successMessage.contains("driver_toogle_shared_state_successfully")) {
                if (response.driver != null)
                    if (response.driver.share_state >= 0) {
                        getmNavigator().ShareStatus(response.driver.share_state);
                    }
            } else if (!CommonUtils.IsEmpty(response.errorMessage))
                getmNavigator().showMessage(response.errorMessage);

        } else if (!CommonUtils.IsEmpty(response.errorMessage))
            getmNavigator().showMessage(response.errorMessage);
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED ||
                e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED ||
                e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            getmNavigator().logoutApp();
        } else if (mCurrentTaskId == Constants.TaskID.LOCATION_UPDATE) {
            e.printStackTrace();
            if (e.getMessage().contains("Unable to resolve host"))
                getmNavigator().showMessage(e.getMessage());
        } else {
            getmNavigator().showMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<String, String> getMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.DRIVER_ID));
        map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.DRIVER_TOKEN));
        return map;
    }

    public void logoutDriver() {
        String driverDetails = sharedPrefence.Getvalue(SharedPrefence.DRIVERERDETAILS);
        if (!TextUtils.isEmpty(driverDetails)) {
            DriverModel driverModel = CommonUtils.getSingleObject(driverDetails, DriverModel.class);
            if (driverModel != null) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Constants.NetworkParameters.id, driverModel.id + "");
                hashMap.put(Constants.NetworkParameters.token, driverModel.token);
                if (getmNavigator().isNetworkConnected()) {
                    setIsLoading(true);
                    Logout(hashMap);
                }
            }
        }
    }

    public void getRequestinProgress() {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            getRequestInProgress();
        }
    }

    public void getCurrentCountry() {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            gitHubCountryCode.getCurrentCountry().enqueue(new Callback<CountryCodeModel>() {
                @Override
                public void onResponse(Call<CountryCodeModel> call, Response<CountryCodeModel> response) {
                    setIsLoading(false);
                    if (response != null)
                        if (response.toString() != null)
                            Constants.COUNTRY_CODE = response.toString();
                }

                @Override
                public void onFailure(Call<CountryCodeModel> call, Throwable t) {
                    setIsLoading(false);
                    if (t != null)
                        t.printStackTrace();
                }
            });
        }
    }

    //    Socket
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject object = new JSONObject();
            try {
                if (CommonUtils.IsEmpty(driver_ID))
                    driver_ID = sharedPrefence.getDRIVER_ID();
                object.put(Constants.NetworkParameters.id, CommonUtils.IsEmpty(driver_ID) ? sharedPrefence.getDRIVER_ID() : driver_ID);
                if (getmNavigator().isNetworkConnected())
                    mSocket.emit(Constants.NetworkParameters.START_CONNECT, object.toString());
                Log.e("Keys", TAG + "_onConnect:" + object);

            } catch (JSONException e) {
                e.printStackTrace();
            }


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
    private Emitter.Listener driverRequest = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("Key_receivedNewRequest:", "" + (args.length > 0 ? args[0] : ""));
            if (args.length > 0) {
                RequestModel model = CommonUtils.getSingleObject(args[0] + "", RequestModel.class);
                if (model != null)
                    if (model.request != null)
                        getmNavigator().openAcceptReject(model, args[0] + "");
            }
        }
    };
    private Emitter.Listener request_handler = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("Key_receivedCancel:", "" + (args.length > 0 ? args[0] : ""));
            if (args.length > 0) {
                RequestModel model = CommonUtils.getSingleObject(args[0] + "", RequestModel.class);
                if (model != null && model.message != null && model.is_cancelled == 1)
                    getmNavigator().sendCancelBroadcast(args[0].toString());
            }
        }
    };

    public void initiateSocket() {
        try {
//            if (!mSocket.connected()) {
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on(Constants.NetworkParameters.DRIVER_REQUEST, driverRequest);
            mSocket.on(Constants.NetworkParameters.REQUEST_HANDLER, request_handler);
            mSocket.connect();
//            }
            if (!mSocket.connected())
                mSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void offSocket() {
        if (mSocket == null)
            return;
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off(Constants.NetworkParameters.DRIVER_REQUEST, driverRequest);
        mSocket.off(Constants.NetworkParameters.REQUEST_HANDLER, request_handler);
    }
    double lastBearing=0;
    public void sendLocation(String id, String lat, String lng, String bearing) {

        if (!mSocket.connected())
            initiateSocket();
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.NetworkParameters.id, sharedPrefence.getDRIVER_ID());
        map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.DRIVER_TOKEN));
        map.put(Constants.NetworkParameters.LAT, lat);
        map.put(Constants.NetworkParameters.LNG, lng);
        try {
            map.put(Constants.NetworkParameters.RANDOM, (Math.random() * 49 + 1) + "");
        }catch (Exception e){
            e.printStackTrace();
        }
        if(lastBearing==0)
            lastBearing=Double.parseDouble(bearing);
        map.put(Constants.NetworkParameters.BEARING, (Double.parseDouble(bearing)==lastBearing||Double.parseDouble(bearing)==0)?((Double.parseDouble(bearing)+1)+""):bearing);
        lastBearing=Double.parseDouble(bearing);
        if ((System.currentTimeMillis() - lastUpdatedTime) > Constants.MinRequestTime) {
            Log.e(TAG, "Keys______________Socket = " + mSocket.connected() + "  Time-" + (System.currentTimeMillis() - lastUpdatedTime) / 1000);
            if (isNetworkConnected() && CommonUtils.checkLocationorGPSEnabled())
                updateLocation(map);
            lastUpdatedTime = System.currentTimeMillis();
        }
        SyncUtils.syncSendData(bearing);
    }

    public void sendTripLocation(String jsonObject) {
        Log.e(TAG, "Keys______________-" + jsonObject);
        if (!mSocket.connected())
            initiateSocket();
        if (getmNavigator().isNetworkConnected())
            mSocket.emit(Constants.NetworkParameters.TRIP_LOCATION, jsonObject);
        else
            MyFirebaseMessagingService.displayNotificationConnectivity(MyApp.getmContext(), MyApp.getmContext().getString(R.string.text_enable_internet));
        SyncUtils.syncSendData(jsonObject);
    }

    public void changeStatusString() {
        if (getmNavigator().isNetworkConnected())
            if (getMap().size() > 0) {
                setIsLoading(true);
                toggleOfflineOnline(getMap());
            }
    }

    public boolean isNetworkConnected() {
        boolean result = NetworkUtils.isNetworkConnected(MyApp.getmContext());
        try {
            if (!result)
                getmNavigator().showMessage(R.string.txt_NoInternet_title);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

