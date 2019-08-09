package bz.pei.driver.ui.History;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptor;
import bz.pei.driver.Retro.ResponseModel.RequestModel;
import bz.pei.driver.ui.Base.BaseView;

import java.util.List;

/**
 * Created by naveen on 13/11/17.
 */

public interface HistoryNavigator extends BaseView {
    public Context getAttachedContext();

    public BitmapDescriptor getMarkerIcon(int drawID);

    public void setRecyclerAdapter(String currency, List<RequestModel.AdditionalCharge> list);

}
