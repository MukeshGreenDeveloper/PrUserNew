package bz.pei.driver.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.location.LocationRequest;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import bz.pei.driver.retro.GitHubCountryCode;
import bz.pei.driver.retro.GitHubMapService;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.dynamicInterceptor;
import bz.pei.driver.utilz.Constants;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by root on 9/22/17.
 */


/*subcomponents = {
        *//* Add additional sub components here *//*}*/
@Module
public class AppModule {


    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }


    @Provides
    @Singleton
    dynamicInterceptor provideInterceptor() { // This is where the Interceptor object is constructed
        return new dynamicInterceptor();
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Context application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    SharedPreferences.Editor providesSharedPreferencesEditor(SharedPreferences sharedPreferences) {
        return sharedPreferences.edit();
    }


    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    @Named(Constants.ourApp)
    OkHttpClient.Builder provideOkHttpClientInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                return chain.proceed(request);
            }
        });
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);

        httpClient.addInterceptor(logging);
        //  httpClient.addInterceptor(dynamicInterceptor);
        //Add extra Intercepter Here

        return httpClient;
    }

    @Provides
    @Singleton
    @Named(Constants.googleMap)
    OkHttpClient.Builder provideOkHttpCterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        return httpClient;
    }


    @Provides
    @Singleton
    @Named(Constants.ourApp)
    GitHubService provideRetrofit(@Named(Constants.ourApp) Retrofit.Builder builder) {

        return builder.build().create(GitHubService.class);
    }


    @Provides
    @Singleton
    @Named(Constants.googleMap)
    GitHubMapService provideRetrofitgoogle(@Named(Constants.googleMap) Retrofit.Builder builder) {

        return builder.build().create(GitHubMapService.class);
    }


    @Provides
    @Named(Constants.ourApp)
    @Singleton
    Retrofit.Builder provideRetrofitBuilder(Gson gson, @Named(Constants.ourApp) OkHttpClient.Builder okhttpbuilder) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpbuilder.addInterceptor(logging);
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okhttpbuilder.build())
                .baseUrl(Constants.URL.BaseURL);
        return builder;
    }

    @Provides
    @Named(Constants.googleMap)
    @Singleton
    Retrofit.Builder provideRetrzzofit(Gson gson, @Named(Constants.googleMap) OkHttpClient.Builder okhttpbuilder) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okhttpbuilder.build())
                .baseUrl(Constants.URL.GooglBaseURL);
        return builder;
    }

    @Provides
    @Singleton
    @Named(Constants.countryMap)
    GitHubCountryCode provideRetrofitcountryCode(@Named(Constants.countryMap) Retrofit.Builder builder) {

        return builder.build().create(GitHubCountryCode.class);
    }

    @Provides
    @Named(Constants.countryMap)
    @Singleton
    Retrofit.Builder provideRetrzzofitForCountry(Gson gson, @Named(Constants.ourApp) OkHttpClient.Builder okhttpbuilder) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okhttpbuilder.build())
                .baseUrl(Constants.URL.COUNTRY_CODE_URL);
        return builder;
    }

    @Provides
    @Singleton
    HashMap<String, String> provideHashMap() {

        return new HashMap<String, String>();
    }

    @Provides
    @Singleton
    io.socket.client.Socket provideSocket() {
        Socket socket = null;
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = true;
        opts.transports = new String[] { WebSocket.NAME };
        try {
            socket = IO.socket(Constants.URL.SOCKET_URL,opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return socket;
    }

    @Provides
    @Singleton
    LocationRequest provideLocationRequest() {
        long INTERVAL = 1000 * 10;
        long FASTEST_INTERVAL = 1000 * 5;
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }


}
