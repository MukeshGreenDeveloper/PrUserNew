package bz.pei.driver.ui.drawerscreen.fragmentz.multipleshareride;

import com.google.android.gms.maps.model.BitmapDescriptor;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.ui.Base.BaseView;

import java.util.List;

public interface MultipleShareRideNavigator extends BaseView{
    public BitmapDescriptor getMarkerIcon(int drawID);
    void passengersDialogs(List<RequestModel.Request> mShareRequest);
}
