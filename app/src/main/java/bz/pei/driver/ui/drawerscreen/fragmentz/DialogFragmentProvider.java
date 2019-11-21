package bz.pei.driver.ui.drawerscreen.fragmentz;

import bz.pei.driver.ui.drawerscreen.dialog.acceptrejectdialog.AcceptRejectDialogFragment;
import bz.pei.driver.ui.drawerscreen.dialog.additonalcharge.AddChargeDialogFragment;
import bz.pei.driver.ui.drawerscreen.dialog.approval.ApprovalFragment;
import bz.pei.driver.ui.drawerscreen.dialog.billdialog.BillDialogFragment;
import bz.pei.driver.ui.drawerscreen.dialog.canceldialog.CancelDialogFragment;
import bz.pei.driver.ui.drawerscreen.dialog.displayaddcharge.DisplayChargesDialog;
import bz.pei.driver.ui.drawerscreen.dialog.logoutdialog.LogoutDialogFragment;
import bz.pei.driver.ui.drawerscreen.dialog.processdocumentupload.ProcessDocUploadDialog;
import bz.pei.driver.ui.drawerscreen.dialog.ridelistdialog.RideListDialogFragment;
import bz.pei.driver.ui.drawerscreen.dialog.sharebilldialog.ShareBillDialogFragment;
import bz.pei.driver.ui.drawerscreen.dialog.waitingdialog.WaitingDialogFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by root on 10/11/17.
 */
@Module
public abstract class DialogFragmentProvider {
    @ContributesAndroidInjector(modules = DialogDaggerModel.class)
    abstract AcceptRejectDialogFragment provideAcceptRejectDialogFragmentProviderFactory();

    @ContributesAndroidInjector(modules = DialogDaggerModel.class)
    abstract ProcessDocUploadDialog provideProcessDocUploadDialog();


    @ContributesAndroidInjector(modules = DialogDaggerModel.class)
    abstract ApprovalFragment provideApprovalDialogFragmentProviderFactory();

    @ContributesAndroidInjector(modules = DialogDaggerModel.class)
    abstract CancelDialogFragment provideCancelDialogFragment();

    @ContributesAndroidInjector(modules = DialogDaggerModel.class)
    abstract BillDialogFragment provideBillDialogFragment();

    @ContributesAndroidInjector(modules = DialogDaggerModel.class)
    abstract WaitingDialogFragment provideWaitingDialogFragment();

    @ContributesAndroidInjector(modules = DialogDaggerModel.class)
    abstract LogoutDialogFragment provideLogoutDialogFragment();


    @ContributesAndroidInjector(modules = DialogDaggerModel.class)
    abstract RideListDialogFragment provideRideListDialogFragment();


    @ContributesAndroidInjector(modules = DialogDaggerModel.class)
    abstract ShareBillDialogFragment provideShareBillDialogFragment();

    @ContributesAndroidInjector(modules = DialogDaggerModel.class)
    abstract AddChargeDialogFragment provideAddChargeDialogFragment();

    @ContributesAndroidInjector(modules = DialogDaggerModel.class)
    abstract DisplayChargesDialog provideDisplayChargesDialog();
}
