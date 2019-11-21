package bz.pei.driver.ui.signupscreen.fragmentz;

import android.content.Context;
import android.databinding.ObservableField;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.google.gson.Gson;
import bz.pei.driver.Pojos.RegisterationModel;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.VehicleTypeModel;
import bz.pei.driver.ui.signupscreen.SignupActivity;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 10/9/17.
 */

public class VehicleInfoViewModel extends BaseNetwork<VehicleTypeModel, VehicleInfoNavigator> implements ViewPager.OnPageChangeListener {
    GitHubService gitHubService;
    public ObservableField<Boolean> arrow_leftVisibility = new ObservableField<>(false);
    public ObservableField<Boolean> right_leftVisibility = new ObservableField<>(true);
    public ObservableField<String> edit_vehiclemodel_register = new ObservableField<>();
    public ObservableField<String> edit_car_manufacturer_register = new ObservableField<>();
    public ObservableField<String> edit_car_year_register = new ObservableField<>();
    public ObservableField<String> edit_vehiclenumber_register = new ObservableField<>();
    //    public ObservableField<Integer> pager_vehicle_selected= new ObservableField<>();
    public String selectedVehicleType;
    public int selectedVehicleID;
    public List<VehicleTypeModel.Type> typeModel;

    public Context context;


    public VehicleInfoViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.gitHubService = gitHubService;


//        mIsLoading.set(true);
    }

    @Override
    public void onSuccessfulApi(long taskId, VehicleTypeModel response) {
        if (response != null) {
            getmNavigator().setUpPagerAdapter(response.types);
            typeModel = response.types;
            if (response.types != null)
                arrow_leftVisibility.set(response.types.size() == 0);
        }
        mIsLoading.set(false);
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        mIsLoading.set(false);
        context = getmNavigator().getAttachedcontext();
        if (context != null)
            ((SignupActivity) context).showMessage(context.getString(R.string.text_error_contact_server));
    }

    public void onClickNext(View view) {
        System.out.println("++++++++++" + RegisterationModel.getInstance().firstName);

      /*  getMap();
        getmNavigator().openDocUploadActivity();*/
    }

    public void getVehicleTypeswithAdminID() {
        if (RegisterationModel.getInstance().admin_ID != null) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.NetworkParameters.admin_id, RegisterationModel.getInstance().admin_ID);
            if (getmNavigator().isNetworkConnected()) {
                mIsLoading.set(true);
                vehicleTypes(hashMap);
            }
        }
    }

    public void onClickPagerRight(View view) {
        getmNavigator().setUpPagerPosition(true);
    }

    public void onClickPagerLeft(View view) {
        getmNavigator().setUpPagerPosition(false);
    }

    @Override
    public HashMap<String, String> getMap() {
        HashMap<String, String> Map = new HashMap<>();
        Map.put(Constants.NetworkParameters.car_number, edit_vehiclenumber_register.get());
        Map.put(Constants.NetworkParameters.car_model, edit_vehiclemodel_register.get());
        Map.put(Constants.NetworkParameters.car_make, edit_car_manufacturer_register.get());
        Map.put(Constants.NetworkParameters.car_year, edit_car_year_register.get());
        if (typeModel != null)
            if (typeModel.size() > 0)
                Map.put(Constants.NetworkParameters.type, selectedVehicleType == null ? typeModel.get(0).id : selectedVehicleType);
            else
                Map.put(Constants.NetworkParameters.type, "");
        return Map;
    }

   /* @BindingAdapter({"bind:textfont"})
    public static void settextFont(TextView textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    @BindingAdapter({"bind:Editfont"})
    public static void setEditFont(EditText textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    @BindingAdapter({"bind:Buttonfont"})
    public static void setButtonFont(Button textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }*/

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //If First Type selected Type is empty
        if (typeModel != null && selectedVehicleType == null) {
//            selectedVehicleType = typeModel.get(position).name;
//            selectedVehicleType = position+"";
        }
    }

    public void setSelectedVehiclePositon(String position) {
        selectedVehicleType = position;
    }

    @Override
    public void onPageSelected(int position) {
        arrow_leftVisibility.set(position <= 0 ? false : true);
        if (typeModel != null) {
            right_leftVisibility.set((position == typeModel.size() - 1) ? false : true);
//            selectedVehicleType = typeModel.get(position).name;
//            selectedVehicleType = position+"";
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public boolean validataion() {
        if (context == null)
            context = getmNavigator().getAttachedcontext();

        String msg = null;
        if (CommonUtils.IsEmpty(edit_vehiclenumber_register.get()))
            msg = context.getString(R.string.error_vehicle_number);
        else if (CommonUtils.IsEmpty(edit_vehiclemodel_register.get()))
            msg = context.getString(R.string.error_vehicle_model);
        else if (CommonUtils.IsEmpty(selectedVehicleType))
            msg = context.getString(R.string.error_vehicle_type);
        else if (CommonUtils.IsEmpty(edit_car_manufacturer_register.get()))
            msg = context.getString(R.string.error_vehicle_manufacturer);

        else if (CommonUtils.IsEmpty(edit_car_year_register.get()))
            msg = context.getString(R.string.error_vehicle_year);

        if (msg != null) {
            getmNavigator().showMessage(msg);
            return false;
        } else {
            RegisterationModel.getInstance().vehicleModel = edit_vehiclemodel_register.get();
            RegisterationModel.getInstance().vehicleNumber = edit_vehiclenumber_register.get();
            RegisterationModel.getInstance().vehicleManufacturer = edit_car_manufacturer_register.get();
            RegisterationModel.getInstance().vehicleYear = edit_car_year_register.get();
            RegisterationModel.getInstance().vehicleType = selectedVehicleType;
        }
        return true;
    }
}
