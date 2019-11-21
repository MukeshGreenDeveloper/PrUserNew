package bz.pei.driver.retro;

import bz.pei.driver.retro.responsemodel.CountryCodeModel;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by root on 10/12/17.
 */

public interface GitHubCountryCode {
    @GET("/json")
    Call<CountryCodeModel> getCurrentCountry();
}
