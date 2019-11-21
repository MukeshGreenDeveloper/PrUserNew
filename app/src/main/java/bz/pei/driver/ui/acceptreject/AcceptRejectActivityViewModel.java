package bz.pei.driver.ui.acceptreject;

import android.content.Context;
import android.databinding.BindingAdapter;
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
import bz.pei.driver.ui.drawerscreen.dialog.acceptrejectdialog.AcceptRejectDialogFragment;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;
import bz.pei.driver.utilz.lineartimer.LinearTimer;
import bz.pei.driver.utilz.lineartimer.LinearTimerStates;
import bz.pei.driver.utilz.lineartimer.LinearTimerView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 10/9/17.
 */

public class AcceptRejectActivityViewModel extends BaseNetwork<RequestModel, AcceptRejectActivityNavigator> implements LinearTimer.TimerListener {

    private RequestModel requestDetails;
    private SharedPrefence sharedPrefence;
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableInt userRating = new ObservableInt();
    public ObservableField<String> pickupAddr = new ObservableField<>();
    public ObservableField<String> dropAddr = new ObservableField<>();
    public ObservableField<String> timeLeft = new ObservableField<>("0");
    public ObservableField<String> CurrentTimeTicking = new ObservableField<>("00.01");
    public static LinearTimer linearTimer;
    public static LinearTimer.TimerListener listener;
    public SoundPool soundPool;
    public boolean loaded;
    public int soundid;
    public static Context context;
    public static String requestId;
    public HashMap<String, String> map;
    Gson gson;

    @Inject
    public AcceptRejectActivityViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                         SharedPrefence sharedPrefence, Gson gson,
                                         HashMap<String, String> hashMap) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
        this.map = hashMap;
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
                Log.d("keys",timeLeft.get()+"  original:"+requestDetails.request.time_left);
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

    @BindingAdapter({"bind:timeLeftActivity"})
    public static void settimeLeftActivity(LinearTimerView view, String timeLeft) {
        Log.e("keys", timeLeft != null ? timeLeft : "TEST");
        /*if (linearTimer != null)*/
        if (Integer.parseInt(timeLeft) < 0) return;
//            if (linearTimer.getState() == LinearTimerStates.ACTIVE)
//                return;
//            linearTimer.resetTimer();
        linearTimer = new LinearTimer.Builder()
                .linearTimerView(view)
                .duration(Integer.parseInt(timeLeft) * 1000)
                .timerListener(listener)
//                .getCountUpdate(LinearTimer.COUNT_UP_TIMER, 1000)
                .build();
        linearTimer.startTimer();
    }

    public void onAcceptClicked(View view) {
//        getmNavigator().dismissDialog();
        if (linearTimer != null)
            if (linearTimer.getState() == LinearTimerStates.ACTIVE)
                linearTimer.pauseTimer();
        setIsLoading(true);
        getMap();
        map.put(Constants.NetworkParameters.IS_RESPONSE, "1");
        acceptRejectRequest(map);
    }

    public void onRejectClicked(View view) {
        if (linearTimer != null)
            if (linearTimer.getState() == LinearTimerStates.ACTIVE)
                linearTimer.pauseTimer();
        setIsLoading(true);
        getMap();
        map.put(Constants.NetworkParameters.IS_RESPONSE, "0");
        map.put(Constants.NetworkParameters.RESASON, "test");
        acceptRejectRequest(map);
    }

    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        /*switch ((int) taskId) {
            case Constants.TaskID.RESPOND_REQUEST:*/
                setIsLoading(false);
                if (response == null)
                    getmNavigator().showSnackBar(translationModel.text_error_contact_server);//context.getString(R.string.text_error_contact_server));
                else {
                    if (response.success) {
                        if (response.successMessage.equalsIgnoreCase("Accepted")) {
                            if (response.request != null) {
                                sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, (int) response.request.id);
                                getmNavigator().gotToTripFragment(response);
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
//                break;
//        }
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
//***************Reject API*****************
        soundPool.stop(soundid);
        /*getmNavigator().dismissDialog();*/
        onRejectClicked(null);
    }

    @Override
    public void onTimerReset() {

    }
}
