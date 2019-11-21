package bz.pei.driver.ui.forgot;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import javax.inject.Inject;

import bz.pei.driver.R;
import bz.pei.driver.databinding.ActivityForgotBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.utilz.SharedPrefence;

public class ForgetPwdActivity extends BaseActivity<ActivityForgotBinding, ForgotViewModel> implements ForgotNavigator {

    @Inject
    ForgotViewModel forgotViewModel;
    ActivityForgotBinding activityForgotBinding;
    @Inject
    SharedPrefence sharedPrefence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityForgotBinding = getViewDataBinding();
        forgotViewModel.setNavigator(this);
        Setup();

    }

    private void Setup() {
        setSupportActionBar(activityForgotBinding.forgotToolbar.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.Txt_Forgot));
        }

        activityForgotBinding.forgotToolbar.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        forgotViewModel.getCurrentCountry();
//        forgotViewModel.obtainTempToken();
    }

    @Override
    public void setCurrentCountryCode(String flag) {
    }

    @Override
    public ForgotViewModel getViewModel() {
        return forgotViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_forgot;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public void openLoginActivity() {
//        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void OpenCountrycodeINvisible() {
        activityForgotBinding.forgotCountrycode.setVisibility(View.GONE);
        activityForgotBinding.forgotFlag.setVisibility(View.GONE);
    }

    @Override
    public void OpenCountrycodevisible() {
        activityForgotBinding.forgotCountrycode.setVisibility(View.VISIBLE);
        activityForgotBinding.forgotFlag.setVisibility(View.VISIBLE);
    }

    @Override
    public Context getAttachedContext() {
        return getBaseContext();
    }
}
