package bz.pei.driver.ui.drawerscreen.fragmentz.trip;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptor;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.ui.Base.BaseView;

import java.util.List;

/**
 * Created by root on 12/21/17.
 */

public interface TripNavigator extends BaseView {
    public Context getApplicationContext();

    public void openMapFragment();

    public void openFeedbackFragment(RequestModel model);

    public void openCallDialer(String phone);

    public void openGoogleMap(Double destLat, Double destLng);

    public void navigateCancelDialog();

    public void openWaitingDialog(int time, int waiting_timeSec);

    public BitmapDescriptor getMarkerIcon(int drawID);

    public List<Integer> getMapPadding();

    public void openSmsDrafter(String user_phoneNumber);

    public void showAddionalChargeDialog();

    public void listAddnlCharge();
}
