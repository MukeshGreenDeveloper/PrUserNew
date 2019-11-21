package bz.pei.driver.retro;

import bz.pei.driver.retro.base.BaseResponse;
import bz.pei.driver.retro.responsemodel.ComplaintsModel;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.retro.responsemodel.SignupModel;
import bz.pei.driver.retro.responsemodel.VehicleTypeModel;
import bz.pei.driver.utilz.Constants;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;

/**
 * Created by root on 9/25/17.
 */

public interface GitHubService {
    @POST(Constants.URL.VehicleTypes)
    Call<VehicleTypeModel> getVehicleTypes(@QueryMap Map<String, String> options);


    @POST(Constants.URL.DriverShareRequestInProgress)
    Call<RequestModel> getDriverShareRequestInProgress(@QueryMap Map<String, String> options);

    @POST(Constants.URL.TokenGeneratorURL)
    Call<SignupModel> TokenGenerator();

    @FormUrlEncoded
    @POST(Constants.URL.otpsendURL)
    Call<SignupModel> SendOtpNetork(@FieldMap  Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.otpvalidate)
    Call<SignupModel> OtpVerfiycall(@FieldMap  Map<String, String> options);
    @FormUrlEncoded
    @POST(Constants.URL.otpResend)
    Call<SignupModel> OtpResendcall(@FieldMap  Map<String, String> options);

    @Multipart
    @POST(Constants.URL.SignUpURL)
    Call<SignupModel> SignupCall(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part profile_pic,
            @Part MultipartBody.Part file_license,
            @Part MultipartBody.Part file_insurance,
            @Part MultipartBody.Part file_rcbook);

    @Multipart
    @POST(Constants.URL.SignUpURL)
    Call<SignupModel> SignupCall(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part file_license,
            @Part MultipartBody.Part file_insurance,
            @Part MultipartBody.Part file_rcbook);

    @FormUrlEncoded
    @POST(Constants.URL.LoginURL)
    Call<SignupModel> Logincall(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.emailphnoCheckURL)
    Call<RequestModel> callemailPhnoAvail(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.getShareStatus)
    Call<RequestModel> getShareStatus(@FieldMap Map<String, String> options);


    //    @FormUrlEncoded
    @POST(Constants.URL.GENERATE_TEMP_TOKEN)
    Call<SignupModel> generateToken();

    @FormUrlEncoded
    @POST(Constants.URL.Forgoturl)
    Call<SignupModel> Forgotcall(@FieldMap Map<String, String> options);

    @Multipart
    @POST(Constants.URL.PROFILE_UPDATE_URL)
    Call<SignupModel> ProfileUpdate(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part profile_pic);

    @Multipart
    @POST(Constants.URL.PROFILE_UPDATE_URL)
    Call<SignupModel> ProfileUpdate(
            @PartMap() Map<String, RequestBody> partMap);

    @FormUrlEncoded
    @POST(Constants.URL.LOGOUT)
    Call<RequestModel> logotcall(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.COMPLAINTS)
    Call<ComplaintsModel> getComplaints(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.REPORT_COMPLAINTS)
    Call<ComplaintsModel> reportComplaint(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.TOGGLE_STATE)
    Call<RequestModel> toggleOfflineOnline(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.TOGGLE_SHARE)
    Call<RequestModel> toggleOffOnShare(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.RESPOND_REQUEST)
    Call<RequestModel> respondRequest(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.REQUEST_IN_PROGRESS)
    Call<RequestModel> requestInProgress(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.CANCEL_TRIP)
    Call<RequestModel> cancelRequest(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.DRIVER_ARRIVED)
    Call<RequestModel> driverArrived(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.START_TRIP)
    Call<RequestModel> startTrip(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.END_TRIP)
    Call<RequestModel> endTrip(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.RATE_USER)
    Call<RequestModel> rateUser(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.SHARE_PASSENGER_LIST)
    Call<RequestModel> rideListApi(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.SINGLE_HISTORY)
    Call<RequestModel> getSingleHistory(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.GETALL_HISTORY)
    Call<RequestModel> getAllHistory(@FieldMap Map<String, String> options);

    //    @FormUrlEncoded
    @POST(Constants.URL.AREA_LIST)
    Call<RequestModel> getAreaListAPI();

    @POST(Constants.URL.TRANSLATIONS_DOC)
    Call<BaseResponse> getTranslationsDoc();

    @FormUrlEncoded
    @POST(Constants.URL.UPLOADED_DOCUMENT_LIST)
    Call<RequestModel> getDocumentList(@FieldMap Map<String, String> map);

    @Multipart
    @POST(Constants.URL.DOCUMENT_UPLOADS)
    Call<RequestModel> DocumentUploadCall(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST(Constants.URL.FAQ_LIST)
    Call<ComplaintsModel> getFaqList(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.HEAT_MAP)
    Call<RequestModel> getHeatMap(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.CANCEL_REASON_LIST_URL)
    Call<BaseResponse> ListCancel(@FieldMap  Map<String, String> options);
    @FormUrlEncoded
    @POST(Constants.URL.GET_LOGIN_OTP)
    Call<SignupModel> loginViaOTP(@FieldMap Map<String, String> params);
    @FormUrlEncoded
    @POST(Constants.URL.GET_PROFILE_DETAILS)
    Call<SignupModel> driveDetails(@FieldMap Map<String, String> params);
    @FormUrlEncoded
    @POST(Constants.URL.SUPPORT)
    Call<SignupModel> sendSupport(@FieldMap Map<String, String> params);
    @FormUrlEncoded
    @POST(Constants.URL.ADD_ADDIONAL_CHAGE)
    Call<RequestModel> addAddionalCharge(@FieldMap Map<String, String> params);
    @FormUrlEncoded
    @POST(Constants.URL.DELETE_ADDIONAL_CHAGE)
    Call<RequestModel> delAddCharge(@FieldMap Map<String, String> params);
    @FormUrlEncoded
    @POST(Constants.URL.LOCATION_UPDATED)
    Call<RequestModel> sendLocation(@FieldMap Map<String, String> params);
}
