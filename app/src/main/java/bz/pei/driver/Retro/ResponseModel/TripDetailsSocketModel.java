package bz.pei.driver.Retro.ResponseModel;

import com.google.gson.annotations.Expose;

/**
 * Created by root on 12/27/17.
 */

public class TripDetailsSocketModel {
    @Expose
    public String success;
    @Expose
    public String distance;

    public TripDetailsSocketModel() {

    }

}
