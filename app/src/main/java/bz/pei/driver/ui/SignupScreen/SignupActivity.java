package bz.pei.driver.ui.SignupScreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.databinding.ActivitySignupBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;
import bz.pei.driver.ui.Login.LoginViaOTP.LoginOTPActivity;
import bz.pei.driver.ui.SignupScreen.Fragmentz.Adapter.SignupPagerAdapter;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class SignupActivity extends BaseActivity<ActivitySignupBinding, SignupViewModel>
        implements SignupNavigator, HasSupportFragmentInjector, ViewPager.OnPageChangeListener {
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    ActivitySignupBinding activitySignupBinding;
    @Inject
    SignupViewModel signupViewModel;
//    VehicleInfoViewModel vehicleInfoViewModel;

    SignupPagerAdapter signupPagerAdapter;

    Intent intent;
    @Inject
    SharedPrefence sharedPrefence;
    public String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter(Constants.Broadcast_SignupAction));

        activitySignupBinding = getViewDataBinding();
        signupViewModel.setNavigator(this);
        setUpToolbar();
        if(getIntent()!=null && getIntent().getStringExtra(Constants.IntentExtras.USER_PHONE)!=null) {
            phoneNumber = getIntent().getStringExtra(Constants.IntentExtras.USER_PHONE);
        }
        setUpViewPager();

    }

    private void setUpViewPager() {
        activitySignupBinding.pagerSignup.setAdapter(signupPagerAdapter = new SignupPagerAdapter(getSupportFragmentManager()));
        activitySignupBinding.indicatorPager.setViewPager(activitySignupBinding.pagerSignup);
        activitySignupBinding.pagerSignup.addOnPageChangeListener(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(activitySignupBinding.signinToolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getString(R.string.text_title_registration));
        activitySignupBinding.signinToolbar.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public SignupViewModel getViewModel() {
        return signupViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_signup;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public void onBackPressed() {
        switch (activitySignupBinding.pagerSignup.getCurrentItem()) {
            case 0:
                openMainActivity();
                break;
            case 1:
                activitySignupBinding.pagerSignup.setCurrentItem(0);
                break;
            case 2:
                activitySignupBinding.pagerSignup.setCurrentItem(1);
                break;
        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            changePagerPosition();
        }
    };


    @Override
    public void sendBroadtoFragment() {

        switch (activitySignupBinding.pagerSignup.getCurrentItem()) {
            case 0:
                intent = new Intent(Constants.Broadcast_SignupFrgAction);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                break;
            case 1:
                intent = new Intent(Constants.Broadcast_VehicleFrgAction);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                break;

            case 2:
                intent = new Intent(Constants.Broadcast_DocmentFrgAction);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                break;

        }


    }

    int currentPage;

    @Override
    public void changePagerPosition() {
        switch (currentPage) {
            case 0:
                activitySignupBinding.pagerSignup.setCurrentItem(1);
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Broadcast_VehicleTypeChangeAction));
                // call
//                signupViewModel.send_adminID();
                break;
            case 1:
                signupViewModel.setParamToSignup();
//                activitySignupBinding.pagerSignup.setCurrentItem(2);
                break;
            /*case 2:
                signupViewModel.setParamToSignup();
                break;*/

        }
    }

    @Override
    public void openMainActivity() {
        startActivity(new Intent(this, LoginOTPActivity.class));
        finish();
    }

    @Override
    public Context getAttachedContext() {
        return this;
    }

    @Override
    public void openDrawrActivity() {
        startActivity(new Intent(this, DrawerAct.class));
        finish();
    }

//    @Override
//    public boolean validatefields() {
//
//        return false;
//    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currentPage = position;
    }

    @Override
    public void onPageSelected(int position) {
        if (activitySignupBinding.pagerSignup.getCurrentItem() == 1||activitySignupBinding.pagerSignup.getCurrentItem() == 2)
            signupViewModel.isLastFragment.set(true);
        else
            signupViewModel.isLastFragment.set(false);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
