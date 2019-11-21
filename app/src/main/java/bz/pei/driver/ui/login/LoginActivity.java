package bz.pei.driver.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.databinding.library.baseAdapters.BR;

import javax.inject.Inject;

import bz.pei.driver.R;
import bz.pei.driver.databinding.ActivityLoginBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.ui.forgot.ForgetPwdActivity;
import bz.pei.driver.ui.main.MainActivity;
import bz.pei.driver.utilz.SharedPrefence;

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> implements LoginNavigator {
    @Inject
    LoginViewModel loginViewModel;
    ActivityLoginBinding activityLoginBinding;

    ScrollView sc_view;
    EditText login_emailorPhone;

    boolean isLoaded = false;

    @Inject
    SharedPrefence sharedPrefence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityLoginBinding = getViewDataBinding();
        loginViewModel.setNavigator(this);
        Setup();
//        sc_view.fullScroll(View.FOCUS_DOWN);

       /* login_emailorPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sc_view.fullScroll(View.FOCUS_DOWN);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        login_emailorPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isLoaded) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sc_view.fullScroll(View.FOCUS_DOWN);
                            isLoaded = true;
                        }
                    }, 500);

                }
                return false;
            }
        });
    }

    private void Setup() {
        setSupportActionBar(activityLoginBinding.loginToolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.text_title_Login));
        }
        loginViewModel.getCurrentCountry();

    }

    @Override
    public void setCurrentCountryCode(String flag) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openMainActivity();
    }

    private void openMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public LoginViewModel getViewModel() {
        return loginViewModel;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void OpenDrawerActivity() {
        startActivity(new Intent(this, DrawerAct.class));
        finish();

    }

    @Override
    public void OpenForgotActivity() {
        startActivity(new Intent(this, ForgetPwdActivity.class));
    }

    @Override
    public void OpenCountrycodevisible() {
        activityLoginBinding.loginCountrycode.setVisibility(View.VISIBLE);
        activityLoginBinding.loginFlag.setVisibility(View.VISIBLE);

    }

    @Override
    public void OpenCountrycodeINvisible() {
        activityLoginBinding.loginCountrycode.setVisibility(View.GONE);
        activityLoginBinding.loginFlag.setVisibility(View.GONE);
    }

    @Override
    public String getCountryCode() {
        return activityLoginBinding.loginCountrycode.getText().toString();
    }

  /*  @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.text_forgt_pwd_signin:
                startActivity(new Intent(this,ForgetPwdActivity.class));
                break;
        }
    }*/
}
