package bz.pei.driver.ui.drawerscreen.fragmentz;

import bz.pei.driver.ui.drawerscreen.fragmentz.faq.FaqFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.feedback.FeedbackFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.historylist.HistoryListFrag;
import bz.pei.driver.ui.drawerscreen.fragmentz.managedocument.ManageDocumentFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.multipleshareride.MultipleShareRideFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.profile.ProfileFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.setting.SettingFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.sharetrip.ShareTripFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.sos.SosFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.supportpage.SupportFrag;
import bz.pei.driver.ui.drawerscreen.fragmentz.trip.TripFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.complaints.ComplaintFragment;
import bz.pei.driver.ui.signupscreen.fragmentz.SignupDaggerModel;

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
