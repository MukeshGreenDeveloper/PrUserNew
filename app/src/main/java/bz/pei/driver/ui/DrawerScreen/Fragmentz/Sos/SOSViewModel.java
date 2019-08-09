package bz.pei.driver.ui.DrawerScreen.Fragmentz.Sos;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import bz.pei.driver.Retro.Base.BaseNetwork;
import bz.pei.driver.Retro.GitHubService;
import bz.pei.driver.Retro.ResponseModel.DriverModel;
import bz.pei.driver.Retro.ResponseModel.SosModel;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.Sos.adapter.SosRecylerAdapter;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.MyDataComponent;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 10/9/17.
 */

public class SOSViewModel extends BaseNetwork<DriverModel, SosNavigator> {

    @Inject
    SharedPrefence sharedPrefence;
    public static Context context;
    public ObservableList<SosModel> listSOS = new ObservableArrayList<>();

    @Inject
    public SOSViewModel(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService,sharedPrefence,gson);
        DataBindingUtil.setDefaultComponent(new MyDataComponent(this));

        this.sharedPrefence = sharedPrefence;
    }


    @Override
    public HashMap<String, String> getMap() {
        return null;
    }

    public void setUpData() {
        context = getmNavigator().getAttachedContext();
        String sosJson = sharedPrefence.Getvalue(SharedPrefence.SOSDETAILS);
//        String sosList = "[{\"id\":\"2\",\"name\":\"customer care\",\"number\":\"+72007040574\"},{\"id\":\"4\",\"name\":\"police\",\"number\":\"+100456987\"}]";
        SosModel[] sosSignupModel = CommonUtils.IsEmpty(sosJson) ? null : CommonUtils.getSingleObject(sosJson, SosModel[].class);
        if (sosSignupModel != null)
            if (sosSignupModel.length > 0) {
                List<SosModel> sosList = Arrays.asList(sosSignupModel);
                sosList = new ArrayList(sosList);
                if (sosList != null)
                    listSOS.addAll(sosList);
            }

    }

    @BindingAdapter("setAdapter")
    public  void setReclyerAdapter(RecyclerView recyclerView, ObservableList<SosModel> listSOS) {
        LinearLayoutManager verticalLayoutmanager
                = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayoutmanager);
        SosRecylerAdapter adapter = new SosRecylerAdapter(context, listSOS);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onSuccessfulApi(long taskId, DriverModel response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

}
