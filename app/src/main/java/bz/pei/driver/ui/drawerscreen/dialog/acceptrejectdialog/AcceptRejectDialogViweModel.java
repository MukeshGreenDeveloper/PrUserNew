package bz.pei.driver.ui.drawerscreen.dialog.acceptrejectdialog;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.media.AudioManager;
import android.media.SoundPool;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.DriverModel;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.MyDataComponent;
import bz.pei.driver.utilz.SharedPrefence;
import bz.pei.driver.utilz.lineartimer.LinearTimer;
import bz.pei.driver.utilz.lineartimer.LinearTimerStates;
import bz.pei.driver.utilz.lineartimer.LinearTimerView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 12/15/17.
 */

public class AcceptRejectDialogViweModel extends BaseNetwork<RequestModel, AcceptRejectNavigator> implements LinearTimer.TimerListener {

    public RequestModel requestDetails;
    private SharedPrefence sharedPrefence;
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableInt userRating = new ObservableInt();
    public ObservableField<String> pickupAddr = new ObservableField<>();
    public ObservableField<String> dropAddr = new ObservableField<>();
    public ObservableField<String> timeLeft = new ObservableField<>("0");
    public ObservableField<String> CurrentTimeTicking = new ObservableField<>("00.01");
    public ObservableBoolean isShare = new ObservableBoolean();
    public static LinearTimer linearTimer;
    public static LinearTimer.TimerListener listener;
    public SoundPool soundPool;
    public boolean loaded;
    public int soundid;
    public static Context context;
    public static String requestId;
    public HashMap<String, String> map;

    @Inject
    public AcceptRejectDialogViweModel(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson, HashMap<String, String> hashMap) {
        super(gitHubService, sharedPrefence, gson);
        DataBindingUtil.setDefaultComponent(new MyDataComponent(this));
        this.sharedPrefence = sharedPrefence;
        this.map = hashMap;
//        super(gitHubService);
        listener = this;
    }

    public void setRequestDetails(RequestModel requestData, Context context) {
        this.requestDetails = requestData;
        this.context = context;
        if (requestData != null) {

            if (requestDetails.request != null) {
                sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, (int) requestDetails.request.id);
                requestId = requestDetails.request.id + "";
                pickupAddr.set(requestDetails.request.pickLocation);
                dropAddr.set(requestDetails.request.dropLocation);
                timeLeft.set(requestDetails.request.time_left);
                isShare.set(requestDetails.request.is_share == 1);
                if (requestDetails.request.user != null) {
                    userName.set(requestDetails.request.user.firstname + " " + requestDetails.request.user.lastname);
                    if (requestDetails.request.user.review > 0)
                        userRating.set((int) requestDetails.request.user.review);
                }
            }
            if (soundPool == null)
                soundPool = new SoundPool(5, AudioManager.STREAM_ALARM, 100);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId,
                                           int status) {
                    loaded = true;
                }
            });
            soundid = soundPool.load(context, R.raw.beep, 1);
        }
    }

    @BindingAdapter({"bind:timeLeft"})
    public void setTimeLeft(LinearTimerView view, String timeLeft) {
        Log.e(AcceptRejectDialogFragment.TAG, timeLeft != null ? timeLeft : "TEST");
        if (Integer.parseInt(timeLeft) < 0) return;
        linearTimer = new LinearTimer.Builder()
                .linearTimerView(view)
                .duration(Integer.parseInt(timeLeft) * 1000)
                .timerListener(listener)
                .build();
        linearTimer.startTimer();
    }


    public void onAcceptClicked(View view) {
        if (linearTimer != null)
            if (linearTimer.getState() == LinearTimerStates.ACTIVE)
                linearTimer.pauseTimer();
        setIsLoading(true);
        getMap();
        map.put(Constants.NetworkParameters.IS_RESPONSE, "1");
        if (getmNavigator().isNetworkConnected())
            acceptRejectRequest(map);
        else
            getmNavigator().showNetworkMessage();
    }

    public void onRejectClicked(View view) {
        if (linearTimer != null)
            if (linearTimer.getState() == LinearTimerStates.ACTIVE)
                linearTimer.pauseTimer();
        setIsLoading(true);
        getMap();
        map.put(Constants.NetworkParameters.IS_RESPONSE, "0");
        map.put(Constants.NetworkParameters.RESASON, "test");
        if (getmNavigator().isNetworkConnected())
            acceptRejectRequest(map);
        else
            getmNavigator().showNetworkMessage();
    }

    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        switch ((int) taskId) {
            case Constants.TaskID.RESPOND_REQUEST:
                setIsLoading(false);
                if (response == null)
                    getmNavigator().showSnackBar(getmNavigator().getAttachedContext().getString(R.string.text_error_contact_server));
                else {
                    if (response.success) {
                        if (response.successMessage.equalsIgnoreCase("Accepted")) {
                            if (response.request != null) {
                                sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, (int) response.request.id);
                                getmNavigator().gotToTripFragment(response);
                                sharedPrefence.saveTripDistance(0.0f);
                            } else {
                                sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
                            }
                        } else if (response.successMessage.equalsIgnoreCase("Rejected_Successfully")) {
                            sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
                        }
                        getmNavigator().dismissDialog();
                    } else
                        getmNavigator().showSnackBar(response.errorMessage);
                }
                break;
        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        switch ((int) taskId) {
            case Constants.TaskID.RESPOND_REQUEST:
                setIsLoading(false);
                if (e.getCode() == Constants.ErrorCode.REQUEST_ALREADY_ENDED) {
                    sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
                    getmNavigator().dismissDialog();
                    if (getmNavigator() != null)
                        if (getmNavigator().getAttachedContext() != null && e != null)
                            Toast.makeText(getmNavigator().getAttachedContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
                } else if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED ||
                        e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED ||
                        e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
                    sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
                    getmNavigator().logoutAppInvalidToken();
                } else {
                    getmNavigator().showSnackBar(e.getMessage());
                    e.printStackTrace();
                }
                break;
        }


    }

    @Override
    public HashMap<String, String> getMap() {

        String driverDetails = sharedPrefence.Getvalue(SharedPrefence.DRIVERERDETAILS);
        if (!TextUtils.isEmpty(driverDetails)) {
            DriverModel driverModel = CommonUtils.getSingleObject(driverDetails, DriverModel.class);
            if (driverModel != null) {
                map.put(Constants.NetworkParameters.id, driverModel.id + "");
                map.put(Constants.NetworkParameters.token, driverModel.token);
                map.put(Constants.NetworkParameters.REQUEST_ID, requestId);
            }
        }

        return map;
    }

    public void confirmCanecl(String string) {
        if (linearTimer != null)
            if (linearTimer.getState() == LinearTimerStates.ACTIVE)
                linearTimer.pauseTimer();
        setIsLoading(true);
        getMap();
        map.put(Constants.NetworkParameters.IS_RESPONSE, "0");
        map.put(Constants.NetworkParameters.RESASON, string);
        acceptRejectRequest(map);
    }

    @Override
    public void animationComplete() {

    }

    @Override
    public void timerTick(long tickUpdateInMillis) {
//    ***********SOUND *********
        long timeLeftAlready = Long.parseLong(timeLeft.get());
        String formattedTime =
                (timeLeftAlready - TimeUnit.MILLISECONDS.toSeconds(tickUpdateInMillis)) + "";
        CurrentTimeTicking.set(formattedTime);
        Log.d(AcceptRejectDialogFragment.TAG, "Time:" + tickUpdateInMillis);
        if (!CommonUtils.IsEmpty(timeLeft.get()))
            if (!(Integer.parseInt(timeLeft.get()) == TimeUnit.MILLISECONDS.toSeconds(tickUpdateInMillis)))
                if (tickUpdateInMillis != 0)
                    if (loaded)
                        if (sharedPrefence.getbooleanvalue(SharedPrefence.SOUND))
                            soundPool.play(soundid, 1, 1, 0, 0, 1);
    }

    @Override
    public void timerFinish() {
        soundPool.stop(soundid);
        onRejectClicked(null);
    }

    @Override
    public void onTimerReset() {

    }

    public void stopTimer() {
        if (linearTimer != null)
            if (linearTimer.getState() == LinearTimerStates.ACTIVE)
                linearTimer.pauseTimer();
        getmNavigator().dismissDialog();
    }
    public void finishTimer(){
        if (linearTimer != null)
            if (linearTimer.getState() == LinearTimerStates.ACTIVE)
                linearTimer.pauseTimer();
    }
}
