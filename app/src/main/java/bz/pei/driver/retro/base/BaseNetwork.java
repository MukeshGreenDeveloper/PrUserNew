package bz.pei.driver.retro.base;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import android.graphics.Typeface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.ComplaintsModel;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.retro.responsemodel.SignupModel;
import bz.pei.driver.retro.responsemodel.TranslationModel;
import bz.pei.driver.retro.responsemodel.VehicleTypeModel;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 9/27/17.
 */

public abstract class BaseNetwork<T extends BaseResponse, N> implements Basecallback<T> {

    GitHubService gitHubService;
    SharedPrefence sharedPrefence;
    private N mNavigator;
    public HashMap<String, RequestBody> requestbody = new HashMap<>();
    public List<MultipartBody.Part> filebody = new ArrayList<>();
    public MultipartBody.Part body_profile_pic = null;
    //    public static File file_profile_pic = null;
    public MultipartBody.Part body_license = null;
    public MultipartBody.Part body_insurance = null;
    public MultipartBody.Part body_rcbook = null;
    /*public  ObservableInt mCurrentTaskId = new ObservableInt(-1);*/
    public Integer mCurrentTaskId = -1;
    public ObservableBoolean mIsLoading = new ObservableBoolean(false);
    public TranslationModel translationModel;

    public Gson gson;

    public BaseNetwork(GitHubService gitHubService) {
        this.gitHubService = gitHubService;

    }

    public BaseNetwork(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        this.gitHubService = gitHubService;
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
        if (!CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE))) {
            translationModel = gson.fromJson(sharedPrefence.Getvalue(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE)), TranslationModel.class);
        }
    }

    public void OptResendcall() {
        gitHubService.OtpResendcall(getMap()).enqueue((Callback<SignupModel>) baseModelCallBackListener);
    }

    public void sendOtpCall() {
        gitHubService.SendOtpNetork(getMap()).enqueue((Callback<SignupModel>) baseModelCallBackListener);
    }

    public void OptVerificationcall() {
        gitHubService.OtpVerfiycall(getMap()).enqueue((Callback<SignupModel>) baseModelCallBackListener);
    }

    public void SignupCall() {
        gitHubService.SignupCall(requestbody, body_profile_pic, body_license, body_insurance, body_rcbook).enqueue((Callback<SignupModel>) baseModelCallBackListener);
    }

    public void profileUpdateNetworkCall() {
        gitHubService.ProfileUpdate(requestbody, body_profile_pic).enqueue((Callback<SignupModel>) baseModelCallBackListener);
    }

    public void profileUpdateNetworkCallWithoutPic() {
        gitHubService.ProfileUpdate(requestbody).enqueue((Callback<SignupModel>) baseModelCallBackListener);
    }

    public void toggleOfflineOnline(HashMap<String, String> map) {
        mCurrentTaskId = Constants.TaskID.TOGGLE_STATE;
        gitHubService.toggleOfflineOnline(map).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void toggleONOFFShare(HashMap<String, String> map) {
        gitHubService.toggleOffOnShare(map).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void getShrareStatusDriver(HashMap<String, String> map) {
        mCurrentTaskId = Constants.TaskID.SHARE_STATE;
        setIsLoading(true);
        gitHubService.getShareStatus(map).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void APICallemailPhnoAvail() {
        gitHubService.callemailPhnoAvail(getMap()).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void LoginNetworkcall() {
        gitHubService.Logincall(getMap()).enqueue((Callback<SignupModel>) baseModelCallBackListener);
    }

    public void getTempToken() {
        mCurrentTaskId = Constants.TaskID.GENERATE_TOKEN;
        gitHubService.generateToken().enqueue((Callback<SignupModel>) baseModelCallBackListener);
    }

    public void ForgetPasswordCall() {
        gitHubService.Forgotcall(getMap()).enqueue((Callback<SignupModel>) baseModelCallBackListener);
    }

    public void Logout(HashMap<String, String> hashMap) {
        mCurrentTaskId = Constants.TaskID.LOGOUT;
        gitHubService.logotcall(hashMap).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void getComplaints(HashMap<String, String> hashMap) {
        mCurrentTaskId = Constants.TaskID.COMPLAINT_LIST;
        gitHubService.getComplaints(hashMap).enqueue((Callback<ComplaintsModel>) baseModelCallBackListener);
    }

    public void getFaq(HashMap<String, String> hashMap) {
        gitHubService.getFaqList(hashMap).enqueue((Callback<ComplaintsModel>) baseModelCallBackListener);
    }

    public void getHeaTData() {
        mCurrentTaskId = Constants.TaskID.GET_HEATMAP_DATA;
        gitHubService.getHeatMap(getMap()).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void loginViaOTPNetwork(HashMap<String, String> params) {
        setIsLoading(true);
        gitHubService.loginViaOTP(params).enqueue((Callback<SignupModel>) baseModelCallBackListener);
    }

    public void reportComplaint(HashMap<String, String> hashMap) {
        mCurrentTaskId = Constants.TaskID.REPORT_COMPLAINT;
        setIsLoading(true);
        gitHubService.reportComplaint(hashMap).enqueue((Callback<ComplaintsModel>) baseModelCallBackListener);
    }

    public void acceptRejectRequest(HashMap<String, String> hashMap) {
        mCurrentTaskId = Constants.TaskID.RESPOND_REQUEST;
        gitHubService.respondRequest(hashMap).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void getRequestInProgress() {
        mCurrentTaskId = Constants.TaskID.REQUEST_IN_PROGRESS;
        gitHubService.requestInProgress(getMap()).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void cancelCurrentRequest() {
        mCurrentTaskId = Constants.TaskID.CANCEL_REQUEST;
        gitHubService.cancelRequest(getMap()).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void tripArrived() {
        mCurrentTaskId = Constants.TaskID.DRIVER_ARRIVED;
        gitHubService.driverArrived(getMap()).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void requestTripStarted() {
        mCurrentTaskId = Constants.TaskID.TRIP_STARTED;
        gitHubService.startTrip(getMap()).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void requestTripCompletd() {
        mCurrentTaskId = Constants.TaskID.TRIP_COMPLETED;
        HashMap<String, String> map = getMap();
        gitHubService.endTrip(map).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void reviewUser() {
        mCurrentTaskId = Constants.TaskID.REVIEW_DRIVER;
        gitHubService.rateUser(getMap()).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void rideListApi() {
        gitHubService.rideListApi(getMap()).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void getSingleHistory() {
        mCurrentTaskId = Constants.TaskID.SINGLE_HISTORY;
        gitHubService.getSingleHistory(getMap()).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void GetHistoryNetworkCall() {
        mCurrentTaskId = Constants.TaskID.GET_HISTORY;
        gitHubService.getAllHistory(getMap()).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void GetDriverShareRequestInProgress(HashMap<String, String> hashMap) {
        gitHubService.getDriverShareRequestInProgress(hashMap).enqueue((Callback<RequestModel>) baseModelCallBackListener);

    }

    public void vehicleTypes(HashMap<String, String> hashMap) {
        setIsLoading(true);
        gitHubService.getVehicleTypes(hashMap).enqueue((Callback<VehicleTypeModel>) baseModelCallBackListener);

    }

    public void getAreaList() {
        setIsLoading(true);
        gitHubService.getAreaListAPI().enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void getTranslations() {
        setIsLoading(true);
        gitHubService.getTranslationsDoc().enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    public void uploadDoc() {
        setIsLoading(true);
        gitHubService.DocumentUploadCall(requestbody, body_profile_pic).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void listDocs(HashMap<String, String> map) {
        setIsLoading(true);
        gitHubService.getDocumentList(map).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void getDriverDetails(HashMap<String, String> map) {
        setIsLoading(true);
        gitHubService.driveDetails(map).enqueue((Callback<SignupModel>) baseModelCallBackListener);
    }

    public void sendSupportFeedback() {
        setIsLoading(true);
        gitHubService.sendSupport(getMap()).enqueue((Callback<SignupModel>) baseModelCallBackListener);
    }

    public void sendAddionalCharge() {
        setIsLoading(true);
        gitHubService.addAddionalCharge(getMap()).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    public void deleteAddionalCharge() {
        setIsLoading(true);
        gitHubService.delAddCharge(getMap()).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }


    public void updateLocation(HashMap<String, String> map) {
        mCurrentTaskId = Constants.TaskID.LOCATION_UPDATE;
        gitHubService.sendLocation(map).enqueue((Callback<RequestModel>) baseModelCallBackListener);
    }

    private Callback<T> baseModelCallBackListener = new Callback<T>() {
        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (response.isSuccessful() && response.body() != null) {
                if (response.body().success) {

                    onSuccessfulApi(mCurrentTaskId, response.body());
                } else {
                    onFailureApi(mCurrentTaskId, new CustomException(response.body().errorCode, response.body().errorMessage));
                }
            } else {
                onFailureApi(mCurrentTaskId, new CustomException(500, Constants.HttpErrorMessage.INTERNAL_SERVER_ERROR));
            }

        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {

            onFailureApi(mCurrentTaskId, new CustomException(501, t.getLocalizedMessage()));
        }
    };

    public abstract HashMap<String, String> getMap();

    public ObservableBoolean getIsLoading() {

        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        mIsLoading.set(isLoading);
    }

    public void setNavigator(N navigator) {
        this.mNavigator = navigator;
    }

    public N getmNavigator() {
        return mNavigator;
    }

    @BindingAdapter({"bind:textfont"})
    public static void settextFont(TextView textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    @BindingAdapter({"bind:Editfont"})
    public static void setEditFont(EditText textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    @BindingAdapter({"bind:Buttonfont"})
    public static void setButtonFont(Button textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/Padauk.ttf"));
    }


    public String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}

