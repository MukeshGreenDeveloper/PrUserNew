package bz.pei.driver.Pojos.DrawPath;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 12/22/17.
 */

public class Step {
    public List<LatLng> listPoints;

    public Step() {
        listPoints = new ArrayList<LatLng>();
    }
}