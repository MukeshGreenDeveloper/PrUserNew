package bz.pei.driver.ui.signupscreen.fragmentz;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import bz.pei.driver.pojos.RegisterationModel;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubCountryCode;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.AreaModel;
import bz.pei.driver.retro.responsemodel.CountryCodeModel;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.ui.signupscreen.SignupActivity;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.RealPathUtil;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;
import java.util.regex.Pattern;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 10/11/17.
 */

public class SignupFragmentViewModel extends BaseNetwork<RequestModel, SignupFragmentNavigator> {
    private static GitHubService gitHubService;
    private static GitHubCountryCode gitHubCountryCode;
    public ObservableField<String> fname = new ObservableField<>("");
    public ObservableField<String> lname = new ObservableField<>("");
    public ObservableField<String> email = new ObservableField<>("");
    public ObservableField<String> password = new ObservableField<>("");
    public ObservableField<String> countrycode = new ObservableField<>("");
    public ObservableField<String> phonenumber = new ObservableField<>("");
    public ObservableField<String> bitmap_profilePicture = new ObservableField<>("");
    public ObservableList<AreaModel> listArea = new ObservableArrayList<>();
    HashMap<String, String> Map = new HashMap<>();
    public String RealPath;
    @Inject
    SignupActivity signupActivity;
    SharedPrefence sharedPrefence;
    public ObservableBoolean isAccTnC = new ObservableBoolean(false);

    @Inject
    public SignupFragmentViewModel(GitHubService gitHubService, GitHubCountryCode gitHubCountryCode,
                                   SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.gitHubService = gitHubService;
        this.gitHubCountryCode = gitHubCountryCode;
        this.sharedPrefence = sharedPrefence;

    }

    public Context context;

    //    public void openTexteditor(View view) {
//        System.out.println("+++" + fname.get());
//        RegisterationModel.getInstance().firstName = fname.get();
//        RegisterationModel.getInstance().lastName = lname.get();
//
//
//    }
    public void checkEmailPhnoAvail() {
        Map.put(Constants.NetworkParameters.email, RegisterationModel.getInstance().email);
        Map.put(Constants.NetworkParameters.phone, RegisterationModel.getInstance().country_code.trim() + CommonUtils.removeFirstZeros(RegisterationModel.getInstance().phonenumber.replaceAll(" ", "")));
        APICallemailPhnoAvail();

    }

    public void SetValue() {
        context = getmNavigator().getAttachedcontext();
        if (RegisterationModel.getInstance() != null) {

            if (CommonUtils.IsEmpty(fname.get()) && !CommonUtils.IsEmpty(RegisterationModel.getInstance().firstName))
                fname.set(RegisterationModel.getInstance().firstName);
            if (CommonUtils.IsEmpty(lname.get()) && !CommonUtils.IsEmpty(RegisterationModel.getInstance().lastName))
                lname.set(RegisterationModel.getInstance().lastName);
            if (CommonUtils.IsEmpty(email.get()) && !CommonUtils.IsEmpty(RegisterationModel.getInstance().email))
                email.set(RegisterationModel.getInstance().email);
//            if (CommonUtils.IsEmpty(password.get()) && !CommonUtils.IsEmpty(RegisterationModel.getInstance().password))
//                password.set(RegisterationModel.getInstance().password);
            if (CommonUtils.IsEmpty(bitmap_profilePicture.get()) && !CommonUtils.IsEmpty(RegisterationModel.getInstance().profile_pic))
                bitmap_profilePicture.set(RegisterationModel.getInstance().profile_pic);
            if (CommonUtils.IsEmpty(phonenumber.get()) && !CommonUtils.IsEmpty(RegisterationModel.getInstance().phonenumber))
                phonenumber.set(RegisterationModel.getInstance().phonenumber);
            if (CommonUtils.IsEmpty(countrycode.get()) && !CommonUtils.IsEmpty(RegisterationModel.getInstance().country_code))
                countrycode.set(RegisterationModel.getInstance().country_code);

//            }

        }

    }

    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        setIsLoading(false);
        if (response == null) {
            getmNavigator().showMessage(context.getString(R.string.text_error_contact_server));
            return;
        }
        if (response.successMessage.equalsIgnoreCase("Phone number and Email Available")) {
            getmNavigator().confromNxt();
        }
        if (response.successMessage.equalsIgnoreCase(Constants.SuccessMessages.AREA_LIST_Message))
            if (response.success) {
                if (response.admins != null)
                    if (response.admins.size() > 0)
                        listArea.addAll(response.admins);
            } else
                getmNavigator().showMessage(response.errorMessage);
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e.getMessage());
    }

    public void openGalaryorCamera(View view) {
        getmNavigator().alertSelectCameraGalary();
//        getmNavigator().galleryIntent();
    }

    public void onSelectFromGalleryResult(Intent data) {
        context = getmNavigator().getAttachedcontext();
        if (data != null) {
            RealPath = RealPathUtil.getRealPath(context, data.getData());
            bitmap_profilePicture.set(RealPath);
        }

    }

    public void onCaptureImageResult(Intent data) {
        context = getmNavigator().getAttachedcontext();
        RealPath = CommonUtils.getImageUri((Bitmap) data.getExtras().get("data"));
//        file_profile_pic=new File(RealPath);
        bitmap_profilePicture.set(RealPath);
    }

    @Override
    public HashMap<String, String> getMap() {
        return Map;
    }


    public boolean validataion() {
        String msg = null;
        if (CommonUtils.IsEmpty(fname.get())) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_first_name_empty);
        } else if (!Pattern.matches("^[a-zA-Z]+( [a-zA-Z]+)*$", fname.get())) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_firstname_validation);
        } else if (CommonUtils.IsEmpty(lname.get())) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_error_lastname);
        } else if (!Pattern.matches("^[a-zA-Z]+( [a-zA-Z]+)*$", fname.get())) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_lastname_validation);
        } else if (CommonUtils.IsEmpty(email.get())) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_error_email);
        } else if (!CommonUtils.isEmailValid(email.get())) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_error_email_valid);
        }  else if (listArea != null && listArea.size() <= 0) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_error_area_empty);
        } else if (getmNavigator().spinSelectionPosition() < 0) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_choose_area);
        } else if (!isAccTnC.get()) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_tearmsAndcon_msg);
        }
        if (msg != null) {
            getmNavigator().showMessage(msg);
            return false;
        } else {
            RegisterationModel.getInstance().firstName = fname.get();
            RegisterationModel.getInstance().lastName = lname.get();
            RegisterationModel.getInstance().email = email.get();
            RegisterationModel.getInstance().password = password.get();
            RegisterationModel.getInstance().phonenumber = phonenumber.get();
            if (!CommonUtils.IsEmpty(bitmap_profilePicture.get()))
                RegisterationModel.getInstance().profile_pic = bitmap_profilePicture.get();
            RegisterationModel.getInstance().country_code = countrycode.get();
            RegisterationModel.getInstance().admin_ID = listArea.get(getmNavigator().spinSelectionPosition()).id;
            if (!CommonUtils.IsEmpty(getmNavigator().getCountryShortName()))
                RegisterationModel.getInstance().countryShortName = getmNavigator().getCountryShortName();
            setIsLoading(true);
        }
        return true;
    }

    /**
     * get the list of zone available to be shown in the dropdown spinner
     */
    public void getAreasForDropDown() {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            getAreaList();
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

    public void onPhoneNumberChanged(Editable e) {
        phonenumber.set(e.toString());
       /* if (e.toString().matches("[0-9]+"))
            if (e.toString().length() == 1 && e.toString().startsWith("0")) {
                e.delete(0, 1);
                phonenumber.set("");
            }*/
    }

    @BindingAdapter("imageUrlProfile")
    public static void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        if (!TextUtils.isEmpty(url))
            Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()
                    .error(R.drawable.ic_profile)
                    .placeholder(R.drawable.ic_profile))
                    .into(imageView);
    }

    @BindingAdapter("setAdapter")
    public static void setArrea(Spinner spinner, ObservableList<AreaModel> list) {
        if (list != null)
            spinner.setAdapter(new ArrayAdapter(spinner.getContext(), android.R.layout.simple_list_item_1, list));
    }
}
