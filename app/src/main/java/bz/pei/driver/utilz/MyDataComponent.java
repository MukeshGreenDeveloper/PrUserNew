package bz.pei.driver.utilz;

import bz.pei.driver.ui.drawerscreen.dialog.acceptrejectdialog.AcceptRejectDialogViweModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.feedback.FeedbackViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.setting.SettingsViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.sharetrip.ShareTripViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.sos.SOSViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.trip.TripViewModel;
import bz.pei.driver.ui.drawerscreen.fragmentz.complaints.ComplaintFragmentViewModel;
import bz.pei.driver.ui.history.HistoryViewModel;

/**
 * Created by root on 12/29/17.
 */

public class MyDataComponent<T> implements androidx.databinding.DataBindingComponent {
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
