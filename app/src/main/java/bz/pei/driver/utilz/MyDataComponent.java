package bz.pei.driver.utilz;

import bz.pei.driver.ui.DrawerScreen.Dialog.AcceptRejectDialog.AcceptRejectDialogViweModel;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.Feedback.FeedbackViewModel;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.Setting.SettingsViewModel;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.ShareTrip.ShareTripViewModel;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.Sos.SOSViewModel;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.Trip.TripViewModel;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.complaintone.ComplaintFragmentViewModel;
import bz.pei.driver.ui.History.HistoryViewModel;

/**
 * Created by root on 12/29/17.
 */

public class MyDataComponent<T> implements android.databinding.DataBindingComponent {
    private T instance;

    public MyDataComponent(T instance) {
        this.instance = instance;
    }

    @Override
    public FeedbackViewModel getFeedbackViewModel() {
        return (FeedbackViewModel) instance;
    }



    @Override
    public ShareTripViewModel getShareTripViewModel() {
        return (ShareTripViewModel) instance;
    }

    @Override
    public TripViewModel getTripViewModel() {
        return (TripViewModel) instance;
    }




    @Override
    public AcceptRejectDialogViweModel getAcceptRejectDialogViweModel() {
        return (AcceptRejectDialogViweModel) instance;
    }

//    @Override
//    public ProfileViewModel getProfileViewModel() {
//        return (ProfileViewModel) instance;
//    }

    @Override
    public SOSViewModel getSOSViewModel() {
        return (SOSViewModel) instance;
    }

    @Override
    public ComplaintFragmentViewModel getComplaintFragmentViewModel() {
        return (ComplaintFragmentViewModel) instance;
    }

    @Override
    public SettingsViewModel getSettingsViewModel() {
        return (SettingsViewModel) instance;
    }

    @Override
    public HistoryViewModel getHistoryViewModel() {
        return (HistoryViewModel) instance;
    }
}
