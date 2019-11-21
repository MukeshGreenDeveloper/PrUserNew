package bz.pei.driver.ui.drawerscreen.dialog.ridelistdialog;

import android.support.v4.app.FragmentActivity;

import bz.pei.driver.adapter.RecyclerAdapter;
import bz.pei.driver.ui.Base.BaseView;

public interface RideListNavigator extends BaseView {

    FragmentActivity getmContext();
    void showMapFragment(int isAcive);
    void setAdapter(RecyclerAdapter recyclerAdapter);


}
