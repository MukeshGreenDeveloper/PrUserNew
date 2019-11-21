package bz.pei.driver.ui.drawerscreen.dialog.waitingdialog;

import android.databinding.ObservableField;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.SignupModel;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

/**
 * Created by root on 12/29/17.
 */


public class WaitingViewModel extends BaseNetwork<SignupModel, WaitingDialogNavigator> {
    public ObservableField<String> timeTravelled = new ObservableField<>("0.0");

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    private SharedPrefence sharedPrefence;
    Handler handler;

    int Seconds, Minutes, MilliSeconds;

    public WaitingViewModel(GitHubService gitHubService,SharedPrefence sharedPrefence) {
        super(gitHubService);
        this.sharedPrefence = sharedPrefence;
        handler = new Handler();

    }

    public void stopWaiting(View v) {
        sharedPrefence.saveIsWating(false);
        sharedPrefence.saveWAITING_TIME(Minutes);
        sharedPrefence.saveWAITING_Sec(Seconds);
        handler.removeCallbacks(runnable);
        getmNavigator().dismissDialog(Minutes,Seconds);

    }

    public void startTimer(int timeBuff,int seconds) {
        TimeBuff=(timeBuff*1000)*60;
        TimeBuff+=(seconds*1000);
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
    }

    public void pauseTimer() {

    }

    public void resumeTimer() {

    }

    public void stopTimer() {
        TimeBuff += MillisecondTime;
        handler.removeCallbacks(runnable);
    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            timeTravelled.set("" + Minutes + ":"
                    + String.format("%02d", Seconds));

            sharedPrefence.saveWAITING_TIME(Minutes);
            sharedPrefence.saveWAITING_Sec(Seconds);
            handler.postDelayed(this, 0);
        }

    };

    @Override
    public void onSuccessfulApi(long taskId, SignupModel response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }
}
