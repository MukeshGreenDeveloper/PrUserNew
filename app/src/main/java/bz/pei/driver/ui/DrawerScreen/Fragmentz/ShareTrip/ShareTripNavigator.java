package bz.pei.driver.ui.DrawerScreen.Fragmentz.ShareTrip;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptor;
import bz.pei.driver.Retro.ResponseModel.RequestModel;
import bz.pei.driver.ui.Base.BaseView;

import java.util.List;

public interface ShareTripNavigator extends BaseView {

    public Context getApplicationContext();

    public void openMapFragment();

    public void openFeedbackFragment(RequestModel model);

    public void openCallDialer(String phone);

    public void openGoogleMap(Double destLat, Double destLng);

    public void navigateCancelDialog();

    public void openWaitingDialog(int time, int waiting_timeSec);

    public BitmapDescriptor getMarkerIcon(int drawID);

    public List<Integer> getMapPadding();

    void showPassengerList();
}
