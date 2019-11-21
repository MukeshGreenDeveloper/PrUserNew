package bz.pei.driver.retro;

import com.google.gson.JsonObject;
import bz.pei.driver.utilz.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by root on 10/12/17.
 */

public interface GitHubMapService {

    //    @GET("maps/api/geocode/json?")
//    Call<JsonObject> GetAddressFromLatLng(@Query("latlng") String name, @Query("sensor") boolean a,@Query("key") String key);
    @GET(Constants.URL.DRAW_PATH)
    Call<JsonObject> getSharePathLatLong(@Query("origin") String origin,
                                         @Query("destination") String destination,
                                         @Query("waypoints") String waypoints,
                                         @Query("sensor") boolean sensor,
                                         @Query("key") String key);


    @GET(Constants.URL.DRAW_PATH)
    Call<JsonObject> getPathLatLong(@Query("origin") String origin,
                                    @Query("destination") String destination,
                                    @Query("sensor") boolean sensor,
                                    @Query("key") String key);


    @GET(Constants.URL.CONVERT_ADDRESS)
    Call<JsonObject> convertAddressFromLatLng(@Query("latlng") String name, @Query("sensor") boolean a, @Query("key") String key);

}
