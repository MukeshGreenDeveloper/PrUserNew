package bz.pei.driver.ui.login.otpscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.databinding.library.baseAdapters.BR;
import bz.pei.driver.app.MyApp;
import bz.pei.driver.R;
import bz.pei.driver.databinding.ActivityOtpBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.ui.login.loginviaotp.LoginOTPActivity;
import bz.pei.driver.ui.signupscreen.SignupActivity;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

public class OTPActivity extends BaseActivity<ActivityOtpBinding, OTPViewModel> implements OTPNavigator {
    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    OTPViewModel OTPViewModel;
    ActivityOtpBinding activityOtpBinding;
    OtpView optview;
    int time = 120;
    TextView textTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOtpBinding = getViewDataBinding();
        OTPViewModel.setNavigator(this);
        optview = activityOtpBinding.optCustomview;
        OTPViewModel.startTimerTwoMinuts();

        textTimer = (TextView) findViewById(R.id.timertxt);
        startTimer(time);


    }


    @Override
    public OTPViewModel getViewModel() {
        return OTPViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_otp;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public String getOpt() {
        return optview.getOTP();
    }


    @Override
    public void OpenDrawerActivity() {
        startActivity(new Intent(this, DrawerAct.class));
        finish();
    }

    @Override
    public void openSinupuActivity(String phoneNumber) {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra(Constants.IntentExtras.USER_PHONE, phoneNumber);
        startActivity(intent);
        finish();
    }

    @Override
    public void FinishAct() {
        if (!MyApp.isIsLoginSignUpActivityVisible())
            startActivity(new Intent(this, LoginOTPActivity.class));
        finish();
    }

    @Override
    public void startTimer(int sec) {
        new CountDownTimer(sec * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                textTimer.setText(getString(R.string.text_resendin) + " " + checkDigit(time) + " " + getString(R.string.text_seconds));
                time--;
            }

            public void onFinish() {
                time = 120;
            }

        }.start();
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}
