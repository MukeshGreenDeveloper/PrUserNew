package bz.pei.driver.ui.Login.LoginViaOTP;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.nplus.countrylist.CountryCodeChangeListener;
import com.nplus.countrylist.CountryUtil;
import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.databinding.ActivityLoginViaOtpBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.Login.OtpScreen.OTPActivity;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

/**
 * Created by naveen on 8/24/17.
 */

public class LoginOTPActivity extends BaseActivity<ActivityLoginViaOtpBinding, LoginOTPViewModel> implements LoginOTPNavigator, CountryCodeChangeListener {
    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    LoginOTPViewModel loginViewModel;
    ActivityLoginViaOtpBinding activitySignupBinding;
    EditText edt_text;
    private CountryUtil mCountryUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignupBinding = getViewDataBinding();
        loginViewModel.setNavigator(this);
        Setup();
        activitySignupBinding.signupEmailorPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activitySignupBinding.scrollRegistration.scrollTo(0, activitySignupBinding.scrollRegistration.getBottom());
                    }
                }, 500);

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!activitySignupBinding.btnLoginSignup.isEnabled()) {
            activitySignupBinding.btnLoginSignup.setEnabled(true);
        }
    }

    private void Setup() {
        loginViewModel.getCurrentCountry();
        edt_text = activitySignupBinding.signupEmailorPhone;
    }

    @Override
    public void setCountryCode(String countryCode) {
        mCountryUtil = new CountryUtil(LoginOTPActivity.this, Constants.COUNTRY_CODE);
        mCountryUtil.initUI(activitySignupBinding.editCountryCodeSignup, this, activitySignupBinding.signupFlag);
        mCountryUtil.initCodes(LoginOTPActivity.this);
    }

    @Override
    public LoginOTPViewModel getViewModel() {
        return loginViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login_via_otp;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }


    @Override
    public void openOtpActivity(String country_code, String phonenumber) {
        activitySignupBinding.btnLoginSignup.setEnabled(false);
        startActivity(new Intent(this, OTPActivity.class).putExtra(Constants.EXTRA_Data, loginViewModel.getMap()));
    }

    @Override
    public void openSocialActivity() {
        startActivity(new Intent(this, LoginOTPActivity.class));
    }


    @Override
    public void HideKeyBoard() {
        hideKeyboard();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    String CountryCode, countryShort;

    @Override
    public String getCountryCode() {
        return CountryCode;
    }

    @Override
    public String getCountryShortName() {
        return countryShort;
    }

    @Override
    public void onCountryCodeChanged(String countryCode, String countryShort) {
        CountryCode = countryCode;
        this.countryShort = countryShort;
    }
}