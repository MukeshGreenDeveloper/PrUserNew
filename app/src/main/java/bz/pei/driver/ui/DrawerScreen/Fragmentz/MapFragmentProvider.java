package bz.pei.driver.ui.DrawerScreen.Fragmentz;

import bz.pei.driver.ui.DrawerScreen.Fragmentz.Faq.FaqFragment;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.Feedback.FeedbackFragment;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.HistoryLIst.HistoryListFrag;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.ManageDocument.ManageDocumentFragment;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.MultipleShareRide.MultipleShareRideFragment;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.Profile.ProfileFragment;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.Setting.SettingFragment;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.ShareTrip.ShareTripFragment;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.Sos.SosFragment;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.SupportPage.SupportFrag;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.Trip.TripFragment;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.complaintone.ComplaintFragment;
import bz.pei.driver.ui.SignupScreen.Fragmentz.SignupDaggerModel;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by root on 10/11/17.
 */
@Module
public abstract class MapFragmentProvider {
    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract MapFragment provideMapFragmentProviderFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract SettingFragment provideSettingFragmentProviderFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract ComplaintFragment provideComplaintFragmentONeProviderFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract SosFragment provideSosFragmentProviderFactory();


    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract TripFragment provideTripFragmentProviderFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract ShareTripFragment provideShareTripFragmentProviderFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract ProfileFragment provideProfileFragment();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract FeedbackFragment provideFeedbackFragment();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract HistoryListFrag provideHistoryListFrag();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract MultipleShareRideFragment provideMultipleShareRideFragment();

    @ContributesAndroidInjector(modules = {MapDaggerModel.class, SignupDaggerModel.class})
    abstract ManageDocumentFragment provideManageDocumentFragment();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract FaqFragment provideFaqFragment();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract SupportFrag provideSupportFrag();



/*@ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract AcceptRejectDialogFragment provideAcceptRejectDialogFragmentProviderFactory();*/
}
