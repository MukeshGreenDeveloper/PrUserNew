package bz.pei.driver.Pojos.DrawPath;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 12/23/17.
 */

public class BeanStep {
    private double startLat;
    private double startLon;
    private double endLat;
    private double endLong;
    private String html_instructions;
    private String strPoint;
    private List<LatLng> listPoints;

    public BeanStep() {
        listPoints = new ArrayList<LatLng>();
    }

    public List<LatLng> getListPoints() {
        return listPoints;
    }

    public void setListPoints(List<LatLng> listPoints) {
        this.listPoints = listPoints;
    }


    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public void setStartLon(double startLon) {
        this.startLon = startLon;
    }


    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }


    public void setEndLong(double endLong) {
        this.endLong = endLong;
    }


    public void setHtml_instructions(String html_instructions) {
        this.html_instructions = html_instructions;
    }

    public String getStrPoint() {
        return strPoint;
    }

    public void setStrPoint(String strPoint) {
        this.strPoint = strPoint;
    }

}
