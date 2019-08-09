package bz.pei.driver.ui.Forgot;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.nplus.countrylist.CountryUtil;
import bz.pei.driver.R;
import bz.pei.driver.databinding.ActivityForgotBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

public class ForgetPwdActivity extends BaseActivity<ActivityForgotBinding, ForgotViewModel> implements ForgotNavigator {

    @Inject
    ForgotViewModel forgotViewModel;
    private CountryUtil mCountryUtil;
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
        mCountryUtil = new CountryUtil(ForgetPwdActivity.this, Constants.COUNTRY_CODE);
        mCountryUtil.initUI(activityForgotBinding.forgotCountrycode, activityForgotBinding.forgotFlag);
        mCountryUtil.initCodes(ForgetPwdActivity.this);
//        forgotViewModel.obtainTempToken();
    }

    @Override
    public void setCurrentCountryCode(String flag) {
        mCountryUtil = new CountryUtil(ForgetPwdActivity.this, Constants.COUNTRY_CODE);
        mCountryUtil.initUI(activityForgotBinding.forgotCountrycode, activityForgotBinding.forgotFlag);
        mCountryUtil.initCodes(ForgetPwdActivity.this);
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
