package bz.pei.driver.ui.signupscreen;

import androidx.databinding.ObservableBoolean;
import android.view.View;

import com.google.gson.Gson;
import bz.pei.driver.pojos.RegisterationModel;
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

import java.io.File;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 10/11/17.
 */

public class SignupViewModel extends BaseNetwork<SignupModel, SignupNavigator> {
    public ObservableBoolean isLastFragment = new ObservableBoolean(false);

    @Inject
    HashMap<String, String> Map;
    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    Gson gson;
    GitHubCountryCode gitHubCountryCode;
    boolean isnewuser=true;
    @Inject
    public SignupViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                           @Named(Constants.countryMap) GitHubCountryCode gitHubCountryCode) {
        super(gitHubService);
        this.gitHubCountryCode = gitHubCountryCode;
    }


    @Override
    public void onSuccessfulApi(long taskId, SignupModel response) {
        if (response.success) {

                getmNavigator().showMessage(getmNavigator().getAttachedContext().getString(R.string.text_thanks_for_register));
                RegisterationModel.clearInstance();
//            getmNavigator().openMainActivity();

                String userstring = gson.toJson(response.driver);
                sharedPrefence.savevalue(SharedPrefence.DRIVERERDETAILS, userstring);
                sharedPrefence.savevalue(SharedPrefence.DRIVER_ID, response.driver.id + "");
                sharedPrefence.savevalue(SharedPrefence.DRIVER_TOKEN, response.driver.token);
                getmNavigator().openDrawrActivity();

        }/* else
            getmNavigator().showMessage(response.errorMessage);*/
        setIsLoading(false);

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e.getMessage());
    }

    @Override
    public HashMap<String, String> getMap() {
        return Map;
    }


    public void OnclickNextBtn(View view) {
        // check email
//        if(getmNavigator().validatefields())
      /*  if(isnewuser)
        checkEmailPhnoAvail();
        else*/
        getmNavigator().sendBroadtoFragment();

    }

   /* public void checkEmailPhnoAvail(){
        if(RegisterationModel.getInstance().email!=null&&               RegisterationModel.getInstance().country_code!=null&& RegisterationModel.getInstance().phonenumber!=null){
            Map.put(Constants.NetworkParameters.email,  RegisterationModel.getInstance().email);
            Map.put(Constants.NetworkParameters.phone, RegisterationModel.getInstance().country_code.trim() + RegisterationModel.getInstance().phonenumber.replaceAll(" ", ""));
            APICallemailPhnoAvail();
        }else {
            getmNavigator().sendBroadtoFragment();
        }

    }*/

    public void setParamToSignup() {
        //            API Parameters
        requestbody.put(Constants.NetworkParameters.firstname, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().firstName));
        requestbody.put(Constants.NetworkParameters.lastname, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().lastName));
        requestbody.put(Constants.NetworkParameters.email, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().email));
        requestbody.put(Constants.NetworkParameters.password, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().password));
        requestbody.put(Constants.NetworkParameters.phone, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().country_code.trim() + CommonUtils.removeFirstZeros(RegisterationModel.getInstance().phonenumber.replaceAll(" ", ""))));
        if (!CommonUtils.IsEmpty(RegisterationModel.getInstance().profile_pic)) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), new File(RegisterationModel.getInstance().profile_pic));
            body_profile_pic = MultipartBody.Part.createFormData("profile_pic", new File(RegisterationModel.getInstance().profile_pic).getName(), reqFile);
        }
        if (CommonUtils.IsEmpty(RegisterationModel.getInstance().vehicleType))
            return;
        requestbody.put(Constants.NetworkParameters.type, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().vehicleType));
        requestbody.put(Constants.NetworkParameters.car_number, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().vehicleNumber));
        requestbody.put(Constants.NetworkParameters.car_model, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().vehicleModel));
        requestbody.put(Constants.NetworkParameters.car_make, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().vehicleManufacturer));
        requestbody.put(Constants.NetworkParameters.car_year, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().vehicleYear));

        requestbody.put(Constants.NetworkParameters.device_token, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.getDeviceToken()));
        requestbody.put(Constants.NetworkParameters.login_by, RequestBody.create(MediaType.parse("text/plain"), Constants.NetworkParameters.android));
        requestbody.put(Constants.NetworkParameters.login_method, RequestBody.create(MediaType.parse("text/plain"), Constants.NetworkParameters.manual));
        requestbody.put(Constants.NetworkParameters.social_unique_id, RequestBody.create(MediaType.parse("text/plain"), ""));
        requestbody.put(Constants.NetworkParameters.admin_id, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().admin_ID));
        requestbody.put(Constants.NetworkParameters.country, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().countryShortName));
        requestbody.put(Constants.NetworkParameters.country_code, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().country_code));
        if (!CommonUtils.IsEmpty(RegisterationModel.getInstance().license_photo)) {
            requestbody.remove(Constants.NetworkParameters.license_name);
            requestbody.remove(Constants.NetworkParameters.license_date);
            body_license = null;
            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), new File(RegisterationModel.getInstance().license_photo));
            body_license = MultipartBody.Part.createFormData(Constants.NetworkParameters.license, new File(RegisterationModel.getInstance().license_photo).getName(), reqFile);
            requestbody.put(Constants.NetworkParameters.license_name, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().license_desc));
            requestbody.put(Constants.NetworkParameters.license_date, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().license_exp));
        }
        if (!CommonUtils.IsEmpty(RegisterationModel.getInstance().insurance_photo)) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), new File(RegisterationModel.getInstance().insurance_photo));
            body_insurance = MultipartBody.Part.createFormData(Constants.NetworkParameters.insurance, new File(RegisterationModel.getInstance().insurance_photo).getName(), reqFile);
            requestbody.put(Constants.NetworkParameters.insurance_name, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().insurance_desc));
            requestbody.put(Constants.NetworkParameters.insurance_date, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().insurance_exp));
        }
        if (!CommonUtils.IsEmpty(RegisterationModel.getInstance().rcBook_photo)) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), new File(RegisterationModel.getInstance().rcBook_photo));
            body_rcbook = MultipartBody.Part.createFormData(Constants.NetworkParameters.rcbook, new File(RegisterationModel.getInstance().rcBook_photo).getName(), reqFile);
            requestbody.put(Constants.NetworkParameters.rcbook_name, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().rcBook_desc));
            requestbody.put(Constants.NetworkParameters.rcbook_date, RequestBody.create(MediaType.parse("text/plain"), RegisterationModel.getInstance().rcBook_exp));
        }


//        displayLog();
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            SignupCall();
        }

    }
/*

    public void send_adminID() {
        if (RegisterationModel.getInstance().admin_ID != null) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.NetworkParameters.admin_id, RegisterationModel.getInstance().admin_ID);
            if (getmNavigator().isNetworkConnected()) {
                mIsLoading.set(true);
                vehicleTypes(hashMap);
            }
        }
    }
*/

    /*  getMap();
      getmNavigator().openDocUploadActivity();*/
    public void getCurrentCountry() {
        if (!CommonUtils.IsEmpty(sharedPrefence.getCURRENT_COUNTRY())) {
            Constants.COUNTRY_CODE = sharedPrefence.getCURRENT_COUNTRY();
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
                            if (!CommonUtils.IsEmpty(Constants.COUNTRY_CODE))
                                sharedPrefence.saveCURRENT_COUNTRY(Constants.COUNTRY_CODE);
                        }
                }

                @Override
                public void onFailure(Call<CountryCodeModel> call, Throwable t) {
                    setIsLoading(false);
                    if (t != null)
                        t.printStackTrace();
                }
            });
        }
    }
//    private void displayLog() {
//        Log.d(Constants.NetworkParameters.firstname, bodyToString(requestbody.get(Constants.NetworkParameters.firstname)));
//        Log.d(Constants.NetworkParameters.lastname, bodyToString(requestbody.get(Constants.NetworkParameters.lastname)));
//        Log.d(Constants.NetworkParameters.email, bodyToString(requestbody.get(Constants.NetworkParameters.email)));
//        Log.d(Constants.NetworkParameters.password, bodyToString(requestbody.get(Constants.NetworkParameters.password)));
//        Log.d(Constants.NetworkParameters.phone, bodyToString(requestbody.get(Constants.NetworkParameters.phone)));
//        Log.d(Constants.NetworkParameters.type, bodyToString(requestbody.get(Constants.NetworkParameters.type)));
//        Log.d(Constants.NetworkParameters.car_number, bodyToString(requestbody.get(Constants.NetworkParameters.car_number)));
//        Log.d(Constants.NetworkParameters.car_model, bodyToString(requestbody.get(Constants.NetworkParameters.car_model)));
//        Log.d(Constants.NetworkParameters.device_token, bodyToString(requestbody.get(Constants.NetworkParameters.device_token)));
//        Log.d(Constants.NetworkParameters.login_by, bodyToString(requestbody.get(Constants.NetworkParameters.login_by)));
//        Log.d(Constants.NetworkParameters.login_method, bodyToString(requestbody.get(Constants.NetworkParameters.login_method)));
//        Log.d(Constants.NetworkParameters.social_unique_id, bodyToString(requestbody.get(Constants.NetworkParameters.social_unique_id)));
//        if (!CommonUtils.IsEmpty(RegisterationModel.getInstance().license_photo)) {
//            Log.d(Constants.NetworkParameters.license_name, bodyToString(requestbody.get(Constants.NetworkParameters.license_name)));
//            Log.d(Constants.NetworkParameters.license_date, bodyToString(requestbody.get(Constants.NetworkParameters.license_name)));
//        }
//        if (!CommonUtils.IsEmpty(RegisterationModel.getInstance().insurance_photo)) {
//            Log.d(Constants.NetworkParameters.insurance_name, bodyToString(requestbody.get(Constants.NetworkParameters.insurance_name)));
//            Log.d(Constants.NetworkParameters.insurance_date, bodyToString(requestbody.get(Constants.NetworkParameters.insurance_date)));
//        }
//        if (!CommonUtils.IsEmpty(RegisterationModel.getInstance().rcBook_photo)) {
//            Log.d(Constants.NetworkParameters.rcbook_name, bodyToString(requestbody.get(Constants.NetworkParameters.rcbook_name)));
//            Log.d(Constants.NetworkParameters.rcbook_date, bodyToString(requestbody.get(Constants.NetworkParameters.rcbook_date)));
//        }
//    }
}
