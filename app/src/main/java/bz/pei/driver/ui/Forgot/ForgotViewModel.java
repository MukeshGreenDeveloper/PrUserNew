package bz.pei.driver.ui.Forgot;

import android.content.Context;
import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
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
 * Created by root on 10/9/17.
 */

public class ForgotViewModel extends BaseNetwork<SignupModel, ForgotNavigator> {

    @Inject
    HashMap<String, String> Map;

    @Inject
    Context context;
    public ObservableField<String> EmailorPhone = new ObservableField<>();
    public ObservableField<String> country_Code = new ObservableField<>();
    SharedPrefence sharedPrefence;
    public String tempToken;

    GitHubCountryCode gitHubCountryCode;

    @Inject
    public ForgotViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                           @Named(Constants.countryMap) GitHubCountryCode gitHubCountryCode,
                           SharedPrefence sharedPrefence,
                           Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.gitHubCountryCode = gitHubCountryCode;
        this.sharedPrefence = sharedPrefence;
    }


    public void onclickSumbit(View view) {

        if (getmNavigator().isNetworkConnected()) {
            if (CommonUtils.IsEmpty(EmailorPhone.get())) {
                getmNavigator().showSnackBar(view, view.getContext().getString(R.string.text_mail_phNum));
            } else {
                obtainTempToken();
            }
        } else {
            getmNavigator().showNetworkMessage();
        }

    }

    public void obtainTempToken() {
        setIsLoading(true);
        getTempToken();
    }

    public void onUsernameChanged(Editable e) {
        EmailorPhone.set(e.toString());
        if (e.toString().contains("@") || e.toString().contains(".co") || e.toString().contains(".com")) {
            getmNavigator().OpenCountrycodeINvisible();

        } else {
            /*if (!e.toString().isEmpty())
                getmNavigator().OpenCountrycodevisible();
            else*/
            if (e.toString().matches("[a-zA-Z]")) //only alphanumeric
                getmNavigator().OpenCountrycodeINvisible();
            else if (e.toString().isEmpty())
                getmNavigator().OpenCountrycodeINvisible();
            else if (e.toString().matches("[0-9]+")) {//only number
                getmNavigator().OpenCountrycodevisible();
                /*if (e.toString().length() == 1 && e.toString().startsWith("0")) {
                    e.delete(0, 1);
                    EmailorPhone.set("");
                }*/
            }
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
        Map.clear();
        setIsLoading(false);
        if (response.success)
            if (response.successMessage != null && (response.successMessage.equalsIgnoreCase("forgot_password")
                    || response.successMessage.equalsIgnoreCase("forgot_password_sms"))) {
                getmNavigator().showMessage(getmNavigator().getAttachedContext().getString(R.string.text_pass_sent_sms));
                getmNavigator().openLoginActivity();
            }
        if (response.successMessage != null && (response.successMessage.equalsIgnoreCase("forgot_password")
                || response.successMessage.equalsIgnoreCase("forgot_password_email"))) {
            getmNavigator().showMessage(getmNavigator().getAttachedContext().getString(R.string.text_pass_sent_email));
            getmNavigator().openLoginActivity();
        } else if (response.successMessage != null && response.successMessage.equalsIgnoreCase("Token_Created")) {
            tempToken = response.token;
            requestPassword();
        }

    }

    private void requestPassword() {
        setIsLoading(true);
        Map.put(Constants.NetworkParameters.token, tempToken);
        if (EmailorPhone.get().contains("@") || EmailorPhone.get().contains(".co") || EmailorPhone.get().contains(".com"))
            Map.put(Constants.NetworkParameters.email, EmailorPhone.get().replaceAll(" ", ""));
        else {
            Map.put(Constants.NetworkParameters.phone_number, country_Code.get().replaceAll(" ", "")
                    + (TextUtils.isEmpty(CommonUtils.removeFirstZeros(EmailorPhone.get())) ? "" :
                    CommonUtils.removeFirstZeros(EmailorPhone.get()).replaceAll(" ", "")));
        }
        ForgetPasswordCall();
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);

        getmNavigator().showMessage(e);
    }

    @Override
    public HashMap<String, String> getMap() {
        return Map;
    }
}
