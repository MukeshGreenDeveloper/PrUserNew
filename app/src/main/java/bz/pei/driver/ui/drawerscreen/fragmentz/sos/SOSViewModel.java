package bz.pei.driver.ui.drawerscreen.fragmentz.sos;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.DriverModel;
import bz.pei.driver.retro.responsemodel.SosModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.sos.adapter.SosRecylerAdapter;
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
