package bz.pei.driver.ui.drawerscreen.fragmentz.profile;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.DriverModel;
import bz.pei.driver.retro.responsemodel.SignupModel;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.RealPathUtil;
import bz.pei.driver.utilz.SharedPrefence;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by root on 10/9/17.
 */

public class ProfileViewModel extends BaseNetwork<SignupModel, ProfileNavigator> {
    HashMap<String, String> Map;

    Context context;

    Gson gson;


    SharedPrefence sharedPrefence;

    public ObservableField<String> Email = new ObservableField<>();
    public ObservableField<String> LastName = new ObservableField<>();
    public ObservableField<String> FirstName = new ObservableField<>();
    public ObservableField<String> cnf_Password = new ObservableField<>("");
    public ObservableField<String> Password = new ObservableField<>("");
    public ObservableField<String> new_Password = new ObservableField<>("");
    public ObservableField<String> Phone_Number = new ObservableField<>();
    public ObservableField<String> vehicleType = new ObservableField<>();
    public ObservableField<String> vehicleNumber = new ObservableField<>();
    public ObservableField<String> vehicleModel = new ObservableField<>();
    public ObservableBoolean mIsEditable = new ObservableBoolean(true);
    public ObservableField<String> bitmap_profilePicture = new ObservableField<>();

    String RealPath = null;
    File RealFile;
    static String driverID, driverToken;
    GitHubService gitHubService;


    @Inject
    public ProfileViewModel(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence, DrawerAct drawerAct, Gson gson, HashMap<String, String> map) {
        super(gitHubService, sharedPrefence, gson);
//        DataBindingUtil.setDefaultComponent(new MyDataComponent(this));
        this.context = drawerAct;
        this.sharedPrefence = sharedPrefence;
        this.gitHubService = gitHubService;

        this.gson = gson;
        this.Map = map;
    }

    public void setDriverDetails(Context context) {
        String driverDetails = sharedPrefence.Getvalue(SharedPrefence.DRIVERERDETAILS);
        this.context = context;
        if (!TextUtils.isEmpty(driverDetails)) {
            DriverModel driverModel = CommonUtils.getSingleObject(driverDetails, DriverModel.class);
            if (driverModel != null) {
                if (!CommonUtils.IsEmpty(driverModel.firstname)) {
                    FirstName.set(driverModel.firstname);
                }
                if (!CommonUtils.IsEmpty(driverModel.lastname)) {
                    LastName.set(driverModel.lastname);
                }
                if (!CommonUtils.IsEmpty(driverModel.email))
                    Email.set(driverModel.email);

                if (!CommonUtils.IsEmpty(driverModel.profilepic))
                    bitmap_profilePicture.set(driverModel.profilepic);
                if (!CommonUtils.IsEmpty(driverModel.phone))
                    Phone_Number.set(driverModel.phone);
                if (!CommonUtils.IsEmpty(driverModel.id + ""))
                    driverID = driverModel.id + "";
                if (!CommonUtils.IsEmpty(driverModel.token))
                    driverToken = driverModel.token + "";
                if (!CommonUtils.IsEmpty(driverModel.car_model))
                    vehicleModel.set(driverModel.car_model);
                if (!CommonUtils.IsEmpty(driverModel.car_number))
                    vehicleNumber.set(driverModel.car_number);
                if (!CommonUtils.IsEmpty(driverModel.type_name))
                    vehicleType.set(driverModel.type_name);
            }
        }
    }

    public void getDriverProfile() {
        if (getmNavigator().isNetworkConnected()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.DRIVER_ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.DRIVER_TOKEN));
            getDriverDetails(hashMap);
        } else
            getmNavigator().showNetworkMessage();
    }

    @Override
    public HashMap<String, String> getMap() {
        requestbody.clear();
        requestbody.put(Constants.NetworkParameters.id, RequestBody.create(MediaType.parse("text/plain"), driverID));
        requestbody.put(Constants.NetworkParameters.token, RequestBody.create(MediaType.parse("text/plain"), driverToken));
        requestbody.put(Constants.NetworkParameters.firstname, RequestBody.create(MediaType.parse("text/plain"), FirstName.get()));
        requestbody.put(Constants.NetworkParameters.lastname, RequestBody.create(MediaType.parse("text/plain"), LastName.get()));
        requestbody.put(Constants.NetworkParameters.email, RequestBody.create(MediaType.parse("text/plain"), Email.get()));
        if (!CommonUtils.IsEmpty(Password.get())) {
            requestbody.put(Constants.NetworkParameters.NEW_PASSWORD, RequestBody.create(MediaType.parse("text/plain"), new_Password.get()));
            requestbody.put(Constants.NetworkParameters.OLD_PASSWORD, RequestBody.create(MediaType.parse("text/plain"), Password.get()));
        }
//        requestbody.put(Constants.NetworkParameters.phone, RequestBody.create(MediaType.parse("text/plain"),Phone_Number.get()));
        if (!CommonUtils.IsEmpty(RealPath)) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), new File(bitmap_profilePicture.get()));
            body_profile_pic = MultipartBody.Part.createFormData("profile_pic", new File(bitmap_profilePicture.get()).getName(), reqFile);
        }
        return null;
    }


    @BindingAdapter("imageUrlProfile")
    public static void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).
                apply(RequestOptions.circleCropTransform().error(R.drawable.ic_user).
                        placeholder(R.drawable.ic_user)).into(imageView);
    }


    public void onclick_clicktoUpdate(View view) {
        mIsEditable.set(false);
    }

    public void onclick_Updatebtn(View view) {
//        mIsEditable.set(true);
//        *********************
        validateNetworkParametrsToAPI();
    }

    private boolean validateNetworkParametrsToAPI() {
        String msg = null;
        if (CommonUtils.IsEmpty(FirstName.get()))
            msg = context.getString(R.string.text_first_name_empty)/*"First Name cannot be Empty!"*/;
        else if (!Pattern.matches("^[a-zA-Z]+( [a-zA-Z]+)*$", FirstName.get())) {
            msg = context.getString(R.string.text_firstname_validation);
        } else if (CommonUtils.IsEmpty(LastName.get()))
            msg = context.getString(R.string.text_error_lastname)/*"Last Name cannot be Empty!"*/;
        else if (!Pattern.matches("^[a-zA-Z]+( [a-zA-Z]+)*$", LastName.get())) {
            msg = context.getString(R.string.text_lastname_validation);
        } else if (CommonUtils.IsEmpty(Email.get()))
            msg = context.getString(R.string.text_error_email)/*"Email cannot be Empty!"*/;
        else if (!CommonUtils.isEmailValid(Email.get()))
            msg = context.getString(R.string.text_error_email_valid)/*"not a valid email!"*/;
/*        else if (!CommonUtils.IsEmpty(Password.get())) {
            if (CommonUtils.IsEmpty(new_Password.get()))
                msg = context.getString(R.string.text_error_password);
            else if (new_Password.get().length() < 8)
                msg = context.getString(R.string.text_error_pass_char);
            else if (new_Password.get().length() < 8||new_Password.get().length() >15) {
                msg = context.getString(R.string.text_error_password_limit);
            }else if (CommonUtils.IsEmpty(cnf_Password.get()))
                msg = context.getString(R.string.text_error_confirm_empyt);
            else if (cnf_Password.get().length() < 8||cnf_Password.get().length() >15) {
                msg = context.getString(R.string.text_error_password_limit);
            }else if (!cnf_Password.get().contentEquals(new_Password.get()))
                msg = context.getString(R.string.Validate_notmatch);
        }*/
        else if (CommonUtils.IsEmpty(Phone_Number.get()))
            msg = context.getString(R.string.text_error_pass_empty)/*"Phonenumber cannot be Empty!"*/;
//        else if (CommonUtils.IsEmpty(bitmap_profilePicture.get()))
//            msg = "Profile pic cannot be Empty!";
        if (msg != null) {
            getmNavigator().showMessage(msg);
            return false;
        } else {
            if (getmNavigator().isNetworkConnected()) {
                setIsLoading(true);
                getMap();
                if (!CommonUtils.IsEmpty(RealPath))
                    profileUpdateNetworkCall();
                else
                    profileUpdateNetworkCallWithoutPic();
            }
        }
        return false;
    }

    @Override
    public void onSuccessfulApi(long taskId, SignupModel response) {
        setIsLoading(false);
        if (response != null) {
            if (response.success) {
                if (response.successMessage.equalsIgnoreCase("Profile Updated Successfully")) {
                    mIsEditable.set(true);
                    if (!CommonUtils.IsEmpty(response.successMessage)) {
//                    getmNavigator().showMessage(context.getString(R.string.Toast_UserLogged));
                        String userstring = gson.toJson(response.driver);
                        sharedPrefence.savevalue(SharedPrefence.DRIVERERDETAILS, userstring);
//                    ((DrawerAct)context).setupProfileDAta();
                        getmNavigator().refreshDrawerActivity();
                    }
                    getmNavigator().showMessage(response.successMessage);
                } else if (response.successMessage.equalsIgnoreCase("driver_profile_retrieve")) {
                    String userstring = gson.toJson(response.driver);
                    sharedPrefence.savevalue(SharedPrefence.DRIVERERDETAILS, userstring);
                    getmNavigator().refreshDrawerActivity();
                    setDriverDetails(getmNavigator().getAttachedContext());
                }
            } else
                getmNavigator().showMessage(response.errorMessage);
        } else
            getmNavigator().showMessage(translationModel.text_error_contact_server/*context.getString(R.string.text_error_contact_server)*/);

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED ||
                e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED ||
                e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if(getmNavigator().getAttachedContext()!=null)
            getmNavigator().showMessage(getmNavigator().getAttachedContext().getString(R.string.text_already_login));
            getmNavigator().logoutApp();
        } else
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }

    public void openGalaryorCamera(View view) {
        getmNavigator().alertSelectCameraGalary();
    }

    public void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            RealPath = RealPathUtil.getRealPath(context, data.getData());
            bitmap_profilePicture.set(RealPath);
        }
    }

    public void onCaptureImageResult(Intent data) {
        RealPath = CommonUtils.getImageUri((Bitmap) data.getExtras().get("data"));
//        file_profile_pic=new File(RealPath);
        bitmap_profilePicture.set(RealPath);
    }
}
