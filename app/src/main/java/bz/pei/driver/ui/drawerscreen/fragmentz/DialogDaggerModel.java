package bz.pei.driver.ui.drawerscreen.fragmentz;

import com.google.gson.Gson;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.ui.drawerscreen.dialog.acceptrejectdialog.AcceptRejectDialogViweModel;
import bz.pei.driver.ui.drawerscreen.dialog.additonalcharge.AddChargeDialogViewModel;
import bz.pei.driver.ui.drawerscreen.dialog.approval.ApprovalViewModel;
import bz.pei.driver.ui.drawerscreen.dialog.billdialog.BillDialogViewModel;
import bz.pei.driver.ui.drawerscreen.dialog.canceldialog.CancelDialogViewModel;
import bz.pei.driver.ui.drawerscreen.dialog.displayaddcharge.DisplayChargesViewModel;
import bz.pei.driver.ui.drawerscreen.dialog.logoutdialog.LogoutViewModel;
import bz.pei.driver.ui.drawerscreen.dialog.processdocumentupload.ProcessDocUploadDialogViewModel;
import bz.pei.driver.ui.drawerscreen.dialog.ridelistdialog.RideListViewModel;
import bz.pei.driver.ui.drawerscreen.dialog.sharebilldialog.ShareBillDialogViewModel;
import bz.pei.driver.ui.drawerscreen.dialog.waitingdialog.WaitingViewModel;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 10/11/17.
 */

@Module
public class DialogDaggerModel {

    @Provides
    AcceptRejectDialogViweModel provideAcceptRejectDialogViweModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                                   SharedPrefence sharedPrefence,
                                                                   Gson gson, HashMap<String, String> hashMap) {
        return new AcceptRejectDialogViweModel(gitHubService, sharedPrefence, gson, hashMap);
    }

    @Provides
    ProcessDocUploadDialogViewModel provideProcessDocUploadDialogViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                                           SharedPrefence sharedPrefence,
                                                                           Gson gson, HashMap<String, String> hashMap) {
        return new ProcessDocUploadDialogViewModel(gitHubService, sharedPrefence, gson, hashMap);
    }


    @Provides
    CancelDialogViewModel provideCancelDialogViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                       SharedPrefence sharedPrefence,
                                                       Gson gson) {
        return new CancelDialogViewModel(gitHubService, sharedPrefence, gson);
    }

    @Provides
    BillDialogViewModel provideBillDialogViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                   SharedPrefence sharedPrefence,
                                                   Gson gson) {
        return new BillDialogViewModel(gitHubService, sharedPrefence, gson);
    }

    @Provides
    WaitingViewModel provideWaitingViewModel(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence) {
        return new WaitingViewModel(gitHubService, sharedPrefence);
    }

    @Provides
    LogoutViewModel provideLogoutViewModel(@Named(Constants.ourApp) GitHubService gitHubService) {
        return new LogoutViewModel(gitHubService);
    }

    @Provides
    RideListViewModel provideRideListViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                               HashMap<String, String> hashMap,
                                               io.socket.client.Socket socket,
                                               Gson gson,
                                               SharedPrefence sharedPrefence) {
        return new RideListViewModel(gitHubService, hashMap, sharedPrefence, socket, gson);
    }

    @Provides
    ApprovalViewModel provideApprovalDialogViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                     SharedPrefence sharedPrefence,
                                                     Gson gson) {
        return new ApprovalViewModel(gitHubService, sharedPrefence, gson);
    }

    @Provides
    ShareBillDialogViewModel provideShareBillDialogViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                             SharedPrefence sharedPrefence) {
        return new ShareBillDialogViewModel(gitHubService, sharedPrefence);
    }
    @Provides
    AddChargeDialogViewModel provideAddChargeDialogViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                             SharedPrefence sharedPrefence,
                                                             Gson gson) {
        return new AddChargeDialogViewModel(gitHubService, sharedPrefence,gson);
    }
    @Provides
    DisplayChargesViewModel provideDisplayChargesViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                            SharedPrefence sharedPrefence,
                                                            Gson gson) {
        return new DisplayChargesViewModel(gitHubService, sharedPrefence,gson);
    }

}
