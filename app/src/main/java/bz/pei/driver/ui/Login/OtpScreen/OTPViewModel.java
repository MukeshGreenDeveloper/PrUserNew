package bz.pei.driver.ui.Login.OtpScreen;

import android.databinding.ObservableBoolean;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.Retro.Base.BaseNetwork;
import bz.pei.driver.Retro.GitHubService;
import bz.pei.driver.Retro.ResponseModel.SignupModel;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 10/9/17.
 */

public class OTPViewModel extends BaseNetwork<SignupModel, OTPNavigator> {

    @Inject
    HashMap<String, String> Map;
    static int resendtimer = 120;

    SharedPrefence sharedPrefence;
    public View view;
    public ObservableBoolean enableResend = new ObservableBoolean(true);
    //   public TimerTask twoMinuesTimer;
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            enableResend.set(true);
        }
    };


    @Inject
    public OTPViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                        @Named("HashMapData") HashMap<String, String> stringHashMap,
                        SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;

        Map = stringHashMap;

        System.out.println("+++" + Map.get(Constants.NetworkParameters.phone_number));

    }

    @Override
    public HashMap<String, String> getMap() {
        return Map;
    }

    public void onclickverfiy(View view) {
        if (getmNavigator().isNetworkConnected()) {
            if (CommonUtils.IsEmpty(getmNavigator().getOpt())) {
                getmNavigator().showSnackBar(view, view.getContext().getString(R.string.Validate_OTP_Empty));
            } else if (getmNavigator().getOpt().length() < 4) {
                getmNavigator().showSnackBar(view, view.getContext().getString(R.string.Validate_OTP_length));
            } else {
                setIsLoading(true);
                if (Map.get(Constants.NetworkParameters.is_signup) != null &&
                        Integer.parseInt(Map.get(Constants.NetworkParameters.is_signup)) == 1 &&
                        !CommonUtils.IsEmpty(Map.get(Constants.NetworkParameters.is_signup))) {
                    Map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
                    Map.put(Constants.NetworkParameters.otp_key, getmNavigator().getOpt());
                    OptVerificationcall();
                } else {
                    Map.put(Constants.NetworkParameters.login_by, Constants.NetworkParameters.android);
                    Map.put(Constants.NetworkParameters.device_token, TextUtils.isEmpty(sharedPrefence.Getvalue(SharedPrefence.DEVICE_TOKEN)) ? "asdfasf" : sharedPrefence.Getvalue(SharedPrefence.DEVICE_TOKEN));
                    Map.put(Constants.NetworkParameters.login_method, Constants.NetworkParameters.manual);
                    Map.put(Constants.NetworkParameters.username, Map.get(Constants.NetworkParameters.phoneNumber));
//                Map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
                    Map.put(Constants.NetworkParameters.password, getmNavigator().getOpt());
                    Map.put(Constants.NetworkParameters.otp_key, getmNavigator().getOpt());

                    LoginNetworkcall();
                }
            }
        } else {
            getmNavigator().showNetworkMessage();
        }

    }


    public void onClickResend(View view) {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            this.view = view;
            if (Map.get(Constants.NetworkParameters.is_signup) != null &&
                    Map.get(Constants.NetworkParameters.is_signup).equalsIgnoreCase("1") &&
                    !CommonUtils.IsEmpty(Map.get(Constants.NetworkParameters.is_signup))) {
                Map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
                OptResendcall();
            } else {
                loginViaOTPNetwork(Map);
            }
            getmNavigator().startTimer(resendtimer);
        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    public void onclickEditNumber(View view) {
        getmNavigator().FinishAct();


    }

    @Override
    public void onSuccessfulApi(long taskId, SignupModel response) {
        setIsLoading(false);
        if (response.success) {
            if (response.successMessage.equalsIgnoreCase("Resend_Otp")) {
                getmNavigator().showSnackBar(view, view.getContext().getString(R.string.Toast_OTP_send));
                startTimerTwoMinuts();
            } else if (response.successMessage.equalsIgnoreCase("Otp_Validate")) {
                getmNavigator().openSinupuActivity(Map.get(Constants.NetworkParameters.phone_number));
            } else if (response.successMessage.equalsIgnoreCase("User_Logged") ||
                    response.successMessage.equalsIgnoreCase("driver_logged")) {
                String userstring = gson.toJson(response.driver);
                sharedPrefence.savevalue(SharedPrefence.DRIVERERDETAILS, userstring);
                sharedPrefence.savevalue(SharedPrefence.DRIVER_ID, response.driver.id + "");
                sharedPrefence.savevalue(SharedPrefence.DRIVER_TOKEN, response.driver.token);
                getmNavigator().OpenDrawerActivity();
            }
        }
    }

    public void startTimerTwoMinuts() {
        enableResend.set(false);
        new android.os.Handler().postDelayed(runnable, resendtimer * 1000);
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.CONTACT_ADMIN) {
            getmNavigator().FinishAct();
        }
        getmNavigator().showMessage(e);
    }
}
