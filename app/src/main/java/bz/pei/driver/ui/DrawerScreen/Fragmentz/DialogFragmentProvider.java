package bz.pei.driver.ui.DrawerScreen.Fragmentz;

import bz.pei.driver.ui.DrawerScreen.Dialog.AcceptRejectDialog.AcceptRejectDialogFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.AdditonalCharge.AddChargeDialogFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.Approval.ApprovalFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.BillDialog.BillDialogFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.CancelDialog.CancelDialogFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.DisplayAddCharge.DisplayChargesDialog;
import bz.pei.driver.ui.DrawerScreen.Dialog.LogoutDialog.LogoutDialogFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.ProcessDocumentUpload.ProcessDocUploadDialog;
import bz.pei.driver.ui.DrawerScreen.Dialog.RideListDialog.RideListDialogFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.ShareBillDialog.ShareBillDialogFragment;
import bz.pei.driver.ui.DrawerScreen.Dialog.WaitingDialog.WaitingDialogFragment;

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
