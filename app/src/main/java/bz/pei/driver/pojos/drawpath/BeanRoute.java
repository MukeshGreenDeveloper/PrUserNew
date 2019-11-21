package bz.pei.driver.pojos.drawpath;

import java.util.ArrayList;

/**
 * Created by root on 12/23/17.
 */
public class BeanRoute {

    private String startAddress;
    private String endAddress;
    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;
    private String distanceText;
    private int distanceValue;
    private String durationText;
    private int durationValue;
    private ArrayList<BeanStep> listStep;

    public ArrayList<BeanStep> getListStep() {
        return listStep;
    }


    public BeanRoute() {
        listStep = new ArrayList<BeanStep>();
    }


    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
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

    public void setEndLon(double endLon) {
        this.endLon = endLon;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public void setDistanceValue(int distanceValue) {
        this.distanceValue = distanceValue;
    }


    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }


    public void setDurationValue(int durationValue) {
        this.durationValue = durationValue;
    }

}