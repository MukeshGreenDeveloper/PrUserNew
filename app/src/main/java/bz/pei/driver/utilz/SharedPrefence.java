package bz.pei.driver.utilz;

import android.content.SharedPreferences;

import javax.inject.Inject;

public class SharedPrefence {
    private static final String MY_PREFS_NAME = "MY_PERFER";
    public static final String Logged = "Logged";
    public static final String INTERVALTIME = "Internaltime";
    public static final String ID = "id";
    public static final String TOKEN = "token";
    public static final String DRIVERERDETAILS = "driverdetails";
    public static final String SOSDETAILS = "sosdetails";
    public static final String IS_OFFLINE = "is_offline";
    public static final String IN_TRIP = "in_trip";
    public static final String Lat = "lat";
    public static final String Lng = "lng";
    public static final String BEARING = "bearing";
    public static final String CUSTOMER_CARE_NO = "customer_care_no";
    public static final String LANGUANGE = "languange";
    public static final String NEW_REQUEST = "new_request";
    public static final String DEVICE_TOKEN = "device_token";
    public static final String REQUEST_ID = "request_id";
    public static final String TRIP_START = "trip_start";
    public static final String IS_SHARE = "is_share";
    public static final String USER_ID = "user_id";
    public static final String LAST_REQUEST_ID = "last_request_id";
    public static final String LAST_UPDATED_TIME = "last_updated_time";
    public static final String DRIVER_ID = "id";
    public static final String DRIVER_TOKEN = "token";
    public static final String SOUND = "sound";
    public static final String CURRENT_COUNTRY = "current_country";
    public static final String WAITING_TIME = "Waiting_time";
    public static final String WAITING_SEC = "Waiting_sec";
    public static final String IsWating = "IsWating";
    public static final String TRANSLATIONS = "TRANSLATIONS";
    public static final String AR = "ar";
    public static final String LANGUAGES = "languages";
    public static final String EN = "en";
    public static final String ES = "es";
    public static final String ZH = "zh";
    public static final String JA = "ja";
    public static final String KO = "ko";
    public static final String PT = "pt";
    public static final String POWER_SAVER_DO_NOT_SHOW = "power_saver_do_not_show";
    public static final String TRIP_DISTANCE= "TRIP_DISTANCE";


    SharedPreferences.Editor editor;
    SharedPreferences prefs;

    @Inject
    public SharedPrefence(SharedPreferences sharedPreferences, SharedPreferences.Editor editor) {
        prefs = sharedPreferences;
        this.editor = editor;
    }

    public void savevalue(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String Getvalue(String key) {
        return prefs.getString(key, "");
    }

    public void saveIntValue(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getIntvalue(String key) {
        return prefs.getInt(key, -1);
    }

    public void savebooleanValue(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void saveCURRENT_COUNTRY(String value) {
        editor.putString(CURRENT_COUNTRY, value);
        editor.apply();
    }


    public void saveWAITING_TIME(long value) {
        editor.putLong(WAITING_TIME, value);
        editor.apply();
    }

    public void saveWAITING_Sec(long value) {
        editor.putLong(WAITING_SEC, value);
        editor.apply();
    }

    public void saveLong(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key) {
        return prefs.getLong(key, 0);
    }

    public void saveIsWating(boolean value) {
        editor.putBoolean(IsWating, value);
        editor.apply();
    }

    public boolean getbooleanvalue(String key) {
        return prefs.getBoolean(key, true);
    }

    public String getDeviceToken() {
        return prefs.getString(DEVICE_TOKEN, "");
    }

    public String getDRIVER_ID() {
        return prefs.getString(DRIVER_ID, "");
    }

    public String getCURRENT_COUNTRY() {
        return prefs.getString(CURRENT_COUNTRY, "");
    }

    public long getWAITING_TIME() {
        return prefs.getLong(WAITING_TIME, 0);
    }

    public long getWAITING_TIMESec() {
        return prefs.getLong(WAITING_SEC, 0);
    }

    public boolean getIsWating() {
        return prefs.getBoolean(IsWating, false);
    }


    public void clearAll() {
        editor.clear().commit();
    }


    public void saveTripDistance(float value) {
        editor.putFloat(TRIP_DISTANCE, value);
        editor.apply();
    }

    public float getTripDistance() {
        return prefs.getFloat(TRIP_DISTANCE, 0);
    }

}