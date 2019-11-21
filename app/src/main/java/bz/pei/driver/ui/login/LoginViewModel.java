package bz.pei.driver.ui.login;

import android.content.Context;
import androidx.databinding.ObservableField;
import android.text.Editable;
import android.view.View;

import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubCountryCode;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.CountryCodeModel;
import bz.pei.driver.retro.responsemodel.SignupModel;
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
 * Created by root on 10/9/17.
 */

public class LoginViewModel extends BaseNetwork<SignupModel, LoginNavigator> {


    @Inject
    Context context;

    SharedPrefence sharedPrefence;
    Gson gson;

    public ObservableField<String> password = new ObservableField<>();
    public ObservableField<String> EmailorPhone = new ObservableField<>();
    public ObservableField<String> Countrycode = new ObservableField<>();

    @Inject
    HashMap<String, String> Map;
    GitHubCountryCode gitHubCountryCode;

    @Inject
    public LoginViewModel(@Named(Constants.ourApp) GitHubService gitHubService, @Named(Constants.countryMap) GitHubCountryCode gitHubCountryCode,
                          SharedPrefence sharedPrefence,
                          Gson gson) {
        super(gitHubService,sharedPrefence,gson);
        this.gitHubCountryCode = gitHubCountryCode;
        this.sharedPrefence= sharedPrefence;
        this.gson= gson;
    }


    public void onclickForgot(View view) {
        getmNavigator().OpenForgotActivity();
    }


    public void onClickSocial(View view) {
//        getmNavigator().OpenSocialActivity();
    }

    public void onclicklogin(View view) {

        if (CommonUtils.IsEmpty(EmailorPhone.get()))
            getmNavigator().showSnackBar(view, view.getContext().getString(R.string.Validate_Email)); //change later
        else if (CommonUtils.IsEmpty(password.get()))
            getmNavigator().showSnackBar(view, view.getContext().getString(R.string.Validate_Password));
        else if (password.get().length() < 6)
            getmNavigator().showSnackBar(view, view.getContext().getString(R.string.Validate_PasswordLength));
        else {

            Map.put(Constants.NetworkParameters.login_by, Constants.NetworkParameters.android);
            Map.put(Constants.NetworkParameters.device_token, sharedPrefence.getDeviceToken());
            Map.put(Constants.NetworkParameters.password, password.get());
            Map.put(Constants.NetworkParameters.login_method, Constants.NetworkParameters.manual);
            if (EmailorPhone.get().contains("@") || EmailorPhone.get().toString().contains(".co") || EmailorPhone.get().toString().contains(".com"))
                Map.put(Constants.NetworkParameters.username, EmailorPhone.get().replace(" ", "")); //change later
            else
                Map.put(Constants.NetworkParameters.username, Countrycode.get().replace(" ", "") +
                        CommonUtils.removeFirstZeros(EmailorPhone.get().replace(" ", "")));
            if (getmNavigator().isNetworkConnected()) {
                setIsLoading(true);
                LoginNetworkcall();
            }


        }


    }

    public void onUsernameChanged(Editable e) {
        EmailorPhone.set(e.toString());
        if (e.toString().contains("@") || e.toString().contains(".co") || e.toString().contains(".com")) {
            getmNavigator().OpenCountrycodeINvisible();

        } else {

            if (e.toString().matches("[a-zA-Z]")) //only alphanumeric
                getmNavigator().OpenCountrycodeINvisible();
            else if (e.toString().isEmpty())
                getmNavigator().OpenCountrycodeINvisible();
            else if (e.toString().matches("[0-9]+"))//only number
                getmNavigator().OpenCountrycodevisible();
        }
    }

    public void getCurrentCountry() {
        if (!CommonUtils.IsEmpty(sharedPrefence.getCURRENT_COUNTRY())) {
            Constants.COUNTRY_CODE = sharedPrefence.getCURRENT_COUNTRY();
            getmNavigator().setCurrentCountryCode(Constants.COUNTRY_CODE);
            return;
        }
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            gitHubCountryCode.getCurrentCountry().enqueue(new Callback<CountryCodeModel>() {
                @Override
                public void onResponse(Call<CountryCodeModel> call, Response<CountryCodeModel> response) {
                    setIsLoading(false);
                    if (response != null)
                        if (response.toString() != null) {
                            Constants.COUNTRY_CODE = response.body().toString();
                            if (!CommonUtils.IsEmpty(Constants.COUNTRY_CODE)) {
                                sharedPrefence.saveCURRENT_COUNTRY(Constants.COUNTRY_CODE);
                                getmNavigator().setCurrentCountryCode(Constants.COUNTRY_CODE);
                            }
                        }
                }

                @Override
                public void onFailure(Call<CountryCodeModel> call, Throwable t) {
                    setIsLoading(false);
                    if (t != null)
                        t.printStackTrace();
                    getmNavigator().setCurrentCountryCode(Constants.COUNTRY_CODE);
                }
            });
        }
    }

    @Override
    public void onSuccessfulApi(long taskId, SignupModel response) {
        setIsLoading(false);
        if (response.success) {
            if (!CommonUtils.IsEmpty(response.successMessage)) {
//                getmNavigator().showMessage(response.successMessage);
                String userstring = gson.toJson(response.driver);
                sharedPrefence.savevalue(SharedPrefence.DRIVERERDETAILS, userstring);
                sharedPrefence.savevalue(SharedPrefence.DRIVER_ID, response.driver.id + "");
                sharedPrefence.savevalue(SharedPrefence.DRIVER_TOKEN, response.driver.token);
                getmNavigator().OpenDrawerActivity();

            }

        }

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e);
        e.printStackTrace();
    }

    @Override
    public HashMap<String, String> getMap() {
        return Map;
    }
}
