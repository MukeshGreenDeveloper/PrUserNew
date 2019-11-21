package bz.pei.driver.retro.responsemodel;

import com.google.gson.annotations.Expose;

/**
 * Created by root on 12/27/17.
 */

public class TripLocationSocketModel {
    @Expose
    String id;
    @Expose
    String lat;
    @Expose
    String lng;
    @Expose
    String bearing;
    @Expose
    String trip_start;
    @Expose
    String user_id;
    @Expose
    String request_id;

    public TripLocationSocketModel() {

    }

    public TripLocationSocketModel(String id, String lat, String lng, String bearing, String trip_start, String user_id, String request_id) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.bearing = bearing;
        this.trip_start = trip_start;
        this.user_id = user_id;
        this.request_id = request_id;
    }

    public void update(String id, String lat, String lng, String bearing, String trip_start, String user_id, String request_id) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.bearing = bearing;
        this.trip_start = trip_start;
        this.user_id = user_id;
        this.request_id = request_id;
    }

}
