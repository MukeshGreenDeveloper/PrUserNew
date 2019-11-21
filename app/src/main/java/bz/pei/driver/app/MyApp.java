package bz.pei.driver.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import bz.pei.driver.di.component.DaggerAppComponent;
import bz.pei.driver.retro.base.BaseResponse;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.Sync.SyncUtils;
import bz.pei.driver.ui.acceptreject.AcceptRejectActivity;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.ui.login.loginviaotp.LoginOTPActivity;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Language.LanguageUtil;
import bz.pei.driver.utilz.Language.SPUtils;
import bz.pei.driver.utilz.SharedPrefence;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.fabric.sdk.android.Fabric;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApp extends Application implements HasActivityInjector, Application.ActivityLifecycleCallbacks {
    private static final String TAG = "MyApp";
    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;
    private static Context mContext;
    private static boolean isDrawerActivityVisible = false;
    private static boolean isDrawerActivityDestroyed = true;
    private static boolean isAcceptRejectActivityDestroyed = true;
    private static boolean isAcceptRejectActivityVisible = false;;
    private static boolean isloginSignUpVisible = false;;
    SharedPrefence sharedPreferences;
    private GitHubService apiService;

    @Override
    public void onCreate() {
        super.onCreate();
        initiatingCrashlytics();
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);
        mContext = this;
        configureLanguage();
        registerActivityLifecycleCallbacks(this);
        initializeSharedPreferences(this);
        initializeAPI(this);
        if (apiService != null)
            apiService.getTranslationsDoc().enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
                    Log.d("keys", "Translations");
                    if (response.body()!=null&&response.body().success)
                        if (response.body().data != null)
                            response.body().saveLanguageTranslations(sharedPreferences,new Gson(),response.body().data);
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    Log.d("keys", "TranslationsError");
                }
            });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getmContext() {
        return mContext;
    }

    private void configureLanguage() {
        /*Locale myLocale = new Locale(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE));
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/
        String localLanguageType = SPUtils.getLocalLanguageType(mContext);
        if ("en".equals(localLanguageType)) {
            Locale locale = Locale.getDefault();
            locale.setDefault(new Locale("en"));
            LanguageUtil.changeLanguageType(this, locale);
        } else {
            LanguageUtil.changeLanguageType(this, Locale.ENGLISH);
        }
    }

    private void initiatingCrashlytics() {
        Fabric.with(this, new Crashlytics());
        // TODO: Move this to where you establish a user session
        logUser();
    }

    private void logUser() {
        // TODO: Use the current user's information
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName("TapnGoDriver");
    }


    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof DrawerAct)
            isDrawerActivityDestroyed = false;
        if (activity instanceof AcceptRejectActivity)
            isAcceptRejectActivityDestroyed = false;
        if (activity instanceof LoginOTPActivity)
            isloginSignUpVisible = true;
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof DrawerAct)
            isDrawerActivityVisible = true;
        if (activity instanceof AcceptRejectActivity)
            isAcceptRejectActivityVisible = true;

    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.e("keys---", "keys----onActivityPaused----" + TAG);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.e("keys---", "keys----onActivityStopped----" + TAG);
        SyncUtils.CreateSyncAccount(this);
        if (activity instanceof DrawerAct)
            isDrawerActivityVisible = false;
        if (activity instanceof AcceptRejectActivity)
            isAcceptRejectActivityVisible = false;
        if (activity instanceof LoginOTPActivity)
            isloginSignUpVisible = false;

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.e(TAG, "isDrawerDestroyed = " + (activity instanceof DrawerAct));
        if (activity instanceof DrawerAct) {
            isDrawerActivityDestroyed = true;
            SyncUtils.CreateSyncAccount(this);
        }
        if (activity instanceof AcceptRejectActivity)
            isAcceptRejectActivityDestroyed = true;
        if (activity instanceof LoginOTPActivity)
            isloginSignUpVisible = false;
    }

    public static boolean isIsDrawerActivityVisible() {
        return isDrawerActivityVisible;
    }

    public static boolean isIsAcceptRejectActivityVisible() {
        return isAcceptRejectActivityVisible;
    }

    public static boolean isIsDrawerActivityDestroyed() {
        return isDrawerActivityDestroyed;
    }

    public static boolean isIsAcceptRejectActivityDestroyed() {
        return isAcceptRejectActivityDestroyed;
    }

    public static void setIsDrawerActivityVisible(boolean isDrawerActivityVisible) {
        MyApp.isDrawerActivityVisible = isDrawerActivityVisible;
    }

    public static boolean isIsLoginSignUpActivityVisible() {
        return isloginSignUpVisible;
    }



    void initializeSharedPreferences(Context application) {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(application);
        sharedPreferences=new SharedPrefence(preferences,preferences.edit());
    }

    void initializeAPI(Context application) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        okhttpbuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                return chain.proceed(request);
            }
        });
        okhttpbuilder.readTimeout(60, TimeUnit.SECONDS);
        okhttpbuilder.connectTimeout(60, TimeUnit.SECONDS);
        okhttpbuilder.addInterceptor(logging);
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(okhttpbuilder.build())
                .baseUrl(Constants.URL.BaseURL);
        apiService = builder.build().create(GitHubService.class);
    }

}
