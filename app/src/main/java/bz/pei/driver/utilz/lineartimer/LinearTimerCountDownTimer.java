package bz.pei.driver.utilz.lineartimer;

import android.os.CountDownTimer;

public class LinearTimerCountDownTimer extends CountDownTimer {

    private long millisLeftUntilFinished;

    private LinearTimer.TimerListener timerListener;

    /**
     * @param millisLeftUntilFinished The number of millis in the future from the call
     *                                to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                                is called.
     * @param countDownInterval       The interval along the way to receive
     *                                {@link #onTick(long)} callbacks.
     */
    public LinearTimerCountDownTimer(long millisLeftUntilFinished,
                                     long countDownInterval, LinearTimer.TimerListener timerListener) {
        super(millisLeftUntilFinished, countDownInterval);
        this.timerListener = timerListener;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        this.millisLeftUntilFinished = millisUntilFinished;
        timerListener.timerTick(millisUntilFinished);
    }

    @Override
    public void onFinish() {
        if (LinearTimer.intStatusCode != LinearTimerStates.PAUSED.getStaus()) {
            timerListener.timerTick(0);
//            timerListener.timerFinish();
        }
    }

    /**
     * Returns the milliseconds remaining before the counter finishes itself
     *
     * @return millisLeftUntilFinished
     */
    long getTimeLeft() {
        return millisLeftUntilFinished;
    }
}