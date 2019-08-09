package bz.pei.driver.ui.Login.LoginViaOTP;

import android.content.Context;
import android.databinding.ObservableField;
import android.text.Editable;
import android.view.View;

import com.google.gson.Gson;
import bz.pei.driver.Pojos.RegisterationModel;
import bz.pei.driver.R;
import bz.pei.driver.Retro.Base.BaseNetwork;
import bz.pei.driver.Retro.GitHubCountryCode;
import bz.pei.driver.Retro.GitHubService;
import bz.pei.driver.Retro.ResponseModel.CountryCodeModel;
import bz.pei.driver.Retro.ResponseModel.SignupModel;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 10/7/17.
 */

public class LoginOTPViewModel extends BaseNetwork<SignupModel, LoginOTPNavigator> {

    @Inject
    HashMap<String, String> Map;


    SharedPrefence sharedPrefence;

    /*BaseView baseView;*/

    public ObservableField<String> EmailorPhone = new ObservableField<>("");
    public ObservableField<String> Countrycode = new ObservableField<>("+");
    GitHubService gitHubService;
    GitHubCountryCode gitHubCountryCode;
    Context context;
    RegisterationModel registerationModel;
    LoginOTPNavigator navigator;

    @Inject
    public LoginOTPViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                             @Named(Constants.countryMap) GitHubCountryCode gitHubCountryCode,
                             SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.gitHubService = gitHubService;
        this.gitHubCountryCode = gitHubCountryCode;
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
        registerationModel = RegisterationModel.getInstance();
    }


    public void onClickLogin(View view) {

        context = view.getContext();
        if (navigator == null)
            navigator = getmNavigator();
        if (navigator.isNetworkConnected()) {
            if (CommonUtils.IsEmpty(EmailorPhone.get().trim().replace("-", "").replace("_", "").replace(" ", "")))
                navigator.showSnackBar(view, view.getContext().getString(R.string.Validate_Mob_Number));
            else if (CommonUtils.IsEmpty(Countrycode.get().trim().replace("+", ""))) {
                navigator.showSnackBar(view, view.getContext().getString(R.string.bt_country_code_invalid));
            } else {
                navigator.HideKeyBoard();
                Map.put(Constants.NetworkParameters.phoneNumber, Countrycode.get().trim() +
                        CommonUtils.removeExtraCharsFirstZero(EmailorPhone.get().trim()));
                loginViaOTPNetwork(Map);
            }
        } else {
            navigator.showNetworkMessage();
        }

    }

    public void onClickSignup(View view) {
        context = view.getContext();
        if (navigator == null)
        navigator = getmNavigator();
        if (navigator.isNetworkConnected()) {
            if (CommonUtils.IsEmpty(EmailorPhone.get().trim().replace("-", "").replace("_", "").replace(" ", "")))
                navigator.showSnackBar(view, context.getString(R.string.Validate_Mob_Number));
            else if (CommonUtils.IsEmpty(Countrycode.get().trim().replace("+", ""))) {
                navigator.showSnackBar(view, context.getString(R.string.bt_country_code_invalid));
            } else {
                navigator.HideKeyBoard();
                TokenGeneratorcall();
            }
        } else {
            navigator.showNetworkMessage();
        }

    }

    public void TokenGeneratorcall() {

        if (navigator == null)
            navigator = getmNavigator();
        if (navigator.isNetworkConnected()) {
            setIsLoading(true);
            TokenGeneratorNetcall();
        } else
            navigator.showNetworkMessage();
    }

    public void TokenGeneratorNetcall() {
        gitHubService.TokenGenerator().enqueue(new Callback<SignupModel>() {
            @Override
            public void onResponse(Call<SignupModel> call, Response<SignupModel> response) {
                setIsLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().success) {
                        if (!CommonUtils.IsEmpty(response.body().token)) {
                            sharedPrefence.savevalue(SharedPrefence.TOKEN, response.body().token);
                            setIsLoading(true);
                            Map.clear();
                            Map.put(Constants.NetworkParameters.phone_number, Countrycode.get().trim() + CommonUtils.removeFirstZeros(EmailorPhone.get().trim().replace("-", "").replace("_", "").replace(" ", "")));
                            Map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
                            Map.put(Constants.NetworkParameters.country_code, getmNavigator().getCountryCode());
                            Map.put(Constants.NetworkParameters.is_signup, "1");
                            sendOtpCall();
                        }
                    } else {
                        getmNavigator().showMessage(response.body().errorMessage);
                    }

                }
            }

            @Override
            public void onFailure(Call<SignupModel> call, Throwable t) {
                setIsLoading(false);
                getmNavigator().showMessage(t.getLocalizedMessage());
            }
        });
    }

    public void onClickOutSide(View view) {
        getmNavigator().HideKeyBoard();
    }

    @Override
    public void onSuccessfulApi(long taskId, SignupModel response) {
        setIsLoading(false);
        if (response.successMessage != null && response.successMessage.equalsIgnoreCase("Login Otp send")) {
            Map.put(Constants.NetworkParameters.is_signup, "0");
            getmNavigator().openOtpActivity(Countrycode.get().trim(), EmailorPhone.get());
        } else if (response.successMessage != null && response.successMessage.equalsIgnoreCase("New_User")) {
            if (registerationModel == null)
                RegisterationModel.getInstance();
            registerationModel.phonenumber = CommonUtils.removeFirstZeros(EmailorPhone.get().trim().replace("-", "").replace("_", "").replace(" ", ""));
            registerationModel.countryShortName = getmNavigator().getCountryShortName();
            registerationModel.country_code = Countrycode.get().trim();
            Map.put(Constants.NetworkParameters.is_signup, "1");
            getmNavigator().openOtpActivity(Countrycode.get().trim(), EmailorPhone.get());
        } else if (response.successMessage != null && response.successMessage.equalsIgnoreCase("Existing_User")) {
            if (response.user != null && response.user.Ispresented) {

                Map.put(Constants.NetworkParameters.phoneNumber, Countrycode.get().trim() +
                        CommonUtils.removeExtraCharacters(EmailorPhone.get()));
                loginViaOTPNetwork(Map);
            }
        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e);
        if (e.getCode() == 702) {
            getmNavigator().openOtpActivity(Countrycode.get().trim(), EmailorPhone.get().trim().replace("-", "").replace("_", "").replace(" ", ""));
        }
    }

    @Override
    public HashMap<String, String> getMap() {
        return Map;
    }


    public void onCCodeChanged(Editable e) {
        Countrycode.set(e.toString());

    }

    public void onUsernameChanged(Editable e) {
        EmailorPhone.set(e.toString());

    }

    public void getCurrentCountry() {
        if (!CommonUtils.IsEmpty(sharedPrefence.getCURRENT_COUNTRY())) {
            Constants.COUNTRY_CODE = sharedPrefence.getCURRENT_COUNTRY();
            getmNavigator().setCountryCode(Constants.COUNTRY_CODE);
            return;
        }
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            gitHubCountryCode.getCurrentCountry().enqueue(new Callback<CountryCodeModel>() {
                @Override
                public void onResponse(Call<CountryCodeModel> call, Response<CountryCodeModel> response) {
                    setIsLoading(false);
                    if (response != null)
                        if (response.toString() != null && response.body() != null) {
                            Constants.COUNTRY_CODE = response.body().toString();
                            if (!CommonUtils.IsEmpty(Constants.COUNTRY_CODE)) {
                                sharedPrefence.saveCURRENT_COUNTRY(Constants.COUNTRY_CODE);
                                getmNavigator().setCountryCode(Constants.COUNTRY_CODE);
                            }
                        }
                }

                @Override
                public void onFailure(Call<CountryCodeModel> call, Throwable t) {
                    setIsLoading(false);
                    if (t != null)
                        t.printStackTrace();
                    getmNavigator().setCountryCode(Constants.COUNTRY_CODE);
                }
            });
        }
    }

}
