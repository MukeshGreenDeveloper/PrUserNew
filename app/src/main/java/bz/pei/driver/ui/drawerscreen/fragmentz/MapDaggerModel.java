package bz.pei.driver.ui.drawerscreen.fragmentz;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.LocationRequest;
import com.google.gson.Gson;
import bz.pei.driver.retro.GitHubMapService;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.HistoryModel;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.ui.drawerscreen.fragmentz.faq.FaqFragmentViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.feedback.FeedbackViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.historylist.HistoryListFrag;
import bz.pei.driver.ui.drawerscreen.fragmentz.historylist.HistoryListViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.historylist.adapter.HistoryAdapter;
import bz.pei.driver.ui.drawerscreen.fragmentz.managedocument.ManageDocumentViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.multipleshareride.MultipleShareRideViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.profile.ProfileViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.setting.SettingsViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.sharetrip.ShareTripViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.sos.SOSViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.supportpage.SupportFragViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.trip.TripViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.complaints.ComplaintFragmentViewModel;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 10/11/17.
 */

@Module
public class MapDaggerModel {
    @Provides
    MapFragmentViewModel provideMapFragmentViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                     @Named(Constants.googleMap) GitHubMapService gitHubgoogle,
                                                     SharedPrefence sharedPrefence,
                                                     Gson gson,
                                                     DrawerAct drawerAct,
                                                     io.socket.client.Socket socket,
                                                     LocationRequest locationRequest) {
        return new MapFragmentViewModel(gitHubService, gitHubgoogle, sharedPrefence, gson, drawerAct, socket/*,locationRequest*/);
    }

    @Provides
    SettingsViewModel provideSettingsViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                               SharedPrefence sharedPrefence, Gson gson) {
        return new SettingsViewModel(gitHubService, sharedPrefence, gson);
    }


    @Provides
    ComplaintFragmentViewModel provideComplaintFragmentOneViewModel(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        return new ComplaintFragmentViewModel(gitHubService, sharedPrefence, gson);
    }

    @Provides
    SOSViewModel provideSOSViewModel(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        return new SOSViewModel(gitHubService, sharedPrefence, gson);
    }


    @Provides
    ShareTripViewModel provideShareTripViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                 @Named(Constants.googleMap) GitHubMapService gitHubMapService,
                                                 SharedPrefence sharedPrefence,
                                                 Gson gson,
                                                 DrawerAct drawerAct,
                                                 io.socket.client.Socket socket,
                                                 LocationRequest locationRequest) {
        return new ShareTripViewModel(gitHubService, gitHubMapService, sharedPrefence, gson, socket, locationRequest, drawerAct);
    }

    @Provides
    MultipleShareRideViewModel provideMultipleShareRideViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                                 @Named(Constants.googleMap) GitHubMapService gitHubMapService,
                                                                 SharedPrefence sharedPrefence,
                                                                 Gson gson,
                                                                 DrawerAct drawerAct,

                                                                 io.socket.client.Socket socket,
                                                                 LocationRequest locationRequest, HashMap<String, String> map) {
        return new MultipleShareRideViewModel(gitHubService, gitHubMapService, sharedPrefence, gson, socket, locationRequest, drawerAct, map);
    }

    @Provides
    TripViewModel provideTripViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                       @Named(Constants.googleMap) GitHubMapService gitHubMapService,
                                       SharedPrefence sharedPrefence,
                                       Gson gson,
                                       DrawerAct drawerAct,
                                       io.socket.client.Socket socket,
                                       LocationRequest locationRequest) {
        return new TripViewModel(gitHubService, gitHubMapService, sharedPrefence, gson, socket, locationRequest, drawerAct);
    }

    @Provides
    ProfileViewModel provideProfileViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                             SharedPrefence sharedPrefence,
                                             DrawerAct drawerAct,
                                             Gson gson,
                                             HashMap<String, String> map) {
        return new ProfileViewModel(gitHubService, sharedPrefence, drawerAct, gson, map);
    }

    @Provides
    FeedbackViewModel provideFeedbackViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                               SharedPrefence sharedPrefence,
                                               DrawerAct drawerAct,
                                               HashMap<String, String> map,
                                               Gson gson) {
        return new FeedbackViewModel(gitHubService, sharedPrefence, map, gson);
    }

    @Provides
    HistoryListViewModel provideHistoryListViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                     HashMap<String, String> map, SharedPrefence sharedPrefence) {
        return new HistoryListViewModel(gitHubService, map, sharedPrefence);
    }


    @Provides
    @Named("HistoryList")
    LinearLayoutManager provideHistoryLinearLayoutManage(HistoryListFrag historyListFrag) {
        return new LinearLayoutManager(historyListFrag.getBaseActivity());
    }

    @Provides
    HistoryAdapter HistoryAdapterAdapter(HistoryListFrag c) {
        return new HistoryAdapter(new ArrayList<HistoryModel>(), c);
    }

    @Provides
    ManageDocumentViewModel provideManageDocumentViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                           SharedPrefence sharedPrefence, Gson gson) {
        return new ManageDocumentViewModel(gitHubService, sharedPrefence, gson);
    }
    @Provides
    FaqFragmentViewModel provideFaqFragmentViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                        SharedPrefence sharedPrefence, Gson gson) {
        return new FaqFragmentViewModel(gitHubService, sharedPrefence, gson);
    }
    @Provides
    SupportFragViewModel provideSupportFragViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                     SharedPrefence sharedPrefence, HashMap<String,String>map,Gson gson) {
        return new SupportFragViewModel(gitHubService, sharedPrefence, map,gson);
    }

}
