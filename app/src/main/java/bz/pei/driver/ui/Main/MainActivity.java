package bz.pei.driver.ui.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import bz.pei.driver.BR;
import bz.pei.driver.Pojos.RegisterationModel;
import bz.pei.driver.R;
import bz.pei.driver.databinding.ActivityMainBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.Login.LoginViaOTP.LoginOTPActivity;
import bz.pei.driver.ui.SignupScreen.SignupActivity;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

/**
 * Created by root on 10/9/17.
 */

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> implements MainNavigator {
    @Inject
    MainViewModel mainViewModel;
    ActivityMainBinding activityMainBinding;
    @Inject
    SharedPrefence sharedPrefence;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = getViewDataBinding();
        mainViewModel.setNavigator(this);

    }

    @Override
    public void openLoginActivity() {
        RegisterationModel.clearInstance();
        startActivity(new Intent(this, LoginOTPActivity.class));
        finish();
    }

    @Override
    public void openSignupActivity() {
        RegisterationModel.clearInstance();
        startActivity(new Intent(this, SignupActivity.class));
        finish();
    }


    @Override
    public MainViewModel getViewModel() {
        return mainViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }
}
