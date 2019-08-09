package bz.pei.driver.ui.DrawerScreen.Fragmentz.MultipleShareRide;

import com.google.android.gms.maps.model.BitmapDescriptor;
import bz.pei.driver.Retro.ResponseModel.RequestModel;
import bz.pei.driver.ui.Base.BaseView;

import java.util.List;

public interface MultipleShareRideNavigator extends BaseView{
    public BitmapDescriptor getMarkerIcon(int drawID);
    void passengersDialogs(List<RequestModel.Request> mShareRequest);
}
