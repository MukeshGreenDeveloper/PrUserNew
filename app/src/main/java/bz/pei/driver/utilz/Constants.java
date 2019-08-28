package bz.pei.driver.utilz;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import bz.pei.driver.BuildConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Created by root on 9/22/17.
 */

public final class Constants {
    public static String[] Array_permissions = new String[]{

            Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static int SPLASH_TIME_OUT = 3000;
//    public static String SERVER_API_KEY = "AIzaSyD6IThzCJ-lHoFjq94zv3CXMuHw_PBFn9I";
    public static String SERVER_API_KEY = "AIzaSyCutNBjAvKMdqlPw8VFr_u_d7amic0LU40";

    public static String COUNTRY_CODE = "AE";
    public static final int REQUEST_PERMISSION = 9000;
    public static final int SELECT_FILE = 100;
    public static final int REQUEST_CAMERA = 200;
    public static final int GOOGLE_SIGN_IN = 300;
    public final static int PLAY_SERVICES_REQUEST = 1000;
    public final static int MinRequestTime = 7000;
    public final static int ADD_CHARGE_VALUE = 1001;
    public static final int LICENSE_DOC = 1;
    public static final int INSURANCE_DOC = 2;
    public static final int RCBOOK_DOC = 3;
    public static final int NO_REQUEST = -1;
    public static final int NO_ID = -1;
    public static final int CARD = 0;
    public static final int INITIAL_DELAY = 1;
    public static final int UPDATE_INTERVAL = 5;
    public static final int CASH = 1;
    public static final int WALLET = 2;
    public static final int WALLET_CASH = 3;
    public static final String EXTRA_Data = "Data";
    public static final String ourApp = "ourApp";
    public static final String sharedPref = "sharedPref";
    public static final String googleMap = "googleMap";
    public static final String countryMap = "countryMap";
    public static final String frmManager = "frmManager";
    public static final String NEW_REQUEST = "NEW_REQUEST";
    public static String Broadcast_SignupAction = "Broadcast_SignupAction";
    public static String Broadcast_SignupFrgAction = "Broadcast_SignupFrgAction";
    public static String Broadcast_VehicleTypeChangeAction = "Broadcast_VehicleTypeChangeAction";
    public static String Broadcast_VehicleFrgAction = "Broadcast_VehicleFrgAction";
    public static String Broadcast_DocmentFrgAction = "Broadcast_DocmentFrgAction";
    public static String ShareState = "ShareState";
    public static List<Intent> POWERMANAGER_INTENTS = Arrays.asList(
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.android.lava.powersave", "com.android.lava.powersave.ui.SmartAppManagerActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
            new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(Uri.parse("mobilemanager://function/entry/AutoStart")),
            new Intent().setComponent(new ComponentName("com.dewav.dwappmanager", "com.dewav.dwappmanager.memory.SmartClearupWhiteList")),
            new Intent().setComponent(new ComponentName("com.evenwell.powersaving.g3", "com.evenwell.powersaving.g3.exception.PowerSaverExceptionActivity")),
            new Intent().setComponent(new ComponentName("com.evenwell.powersaving.g3", "com.evenwell.powersaving.g3.exception.PowerSaverExceptionActivity"))
    );

    public static final String CHANNEL_ID = "my_service";
    public static final String CHANNEL_NAME = "My Background Service";
    public interface HttpErrorMessage {
        String INTERNAL_SERVER_ERROR = "Our server is under maintenance. We will reslove shortly!";
        String FORBIDDEN = "Seems like you haven't permitted to do this operation!";
    }

    public interface URL {
        /*Live*/
        String BaseURL = BuildConfig.BASE_URL+"/production/peitaxi/public/";
        String SOCKET_URL = BuildConfig.SOCKET_URL+":3001/driver/home";
        String TERMSANDCONDITIONS ="https://pei.bz/terms.html";
        String GooglBaseURL = "https://maps.googleapis.com/";
        String DRAW_PATH = "maps/api/directions/json";
        String CONVERT_ADDRESS = "maps/api/geocode/json";
        String COUNTRY_CODE_URL = "http://ip-api.com/";//"/json";
        String VehicleTypes = "v1/application/types";
        String DriverShareRequestInProgress = "v1/driver/share/requestInprogress";
        String TokenGeneratorURL = "v1/driver/temptoken";
        String otpsendURL = "v1/driver/sendotp";
        String otpvalidate = "v1/driver/otpvalidate";
        String otpResend = "v1/user/resendotp";
        String SignUpURL = "v1/driver/signup";
        String LoginURL = "v1/driver/login";
        String PROFILE_UPDATE_URL = "v1/driver/profile";
        String LOGOUT = "v1/driver/logout";
        String TOGGLE_STATE = "v1/driver/toogle/status";
        String COMPLAINTS = "v1/compliants/list";
        String REPORT_COMPLAINTS = "v1/compliants/driver";
        String RESPOND_REQUEST = "v1/driver/response";
        String REQUEST_IN_PROGRESS = "v1/driver/requestInprogress";
        String CANCEL_TRIP = "v1/driver/trip/cancel";
        String DRIVER_ARRIVED = "v1/driver/arrived";
        String START_TRIP = "v1/driver/trip/start";
        String GENERATE_TEMP_TOKEN = "v1/user/temptoken";
        String END_TRIP = "v1/driver/requestBill";
        String RATE_USER = "v1/driver/review";
        String SINGLE_HISTORY = "v1/driver/historySingle";
        String GETALL_HISTORY = "v1/driver/historyList";
        String Forgoturl = "v1/driver/forgotpassword";
        String AREA_LIST = "v1/driver/admin/list";
        String TRANSLATIONS_DOC = "v1/lang/get";
        String TOGGLE_SHARE = "v1/driver/share/toogle/status";
        String SHARE_PASSENGER_LIST = "v1/driver/share/requestInprogress";
        String DOCUMENT_UPLOADS = "v1/driver/document/upload";
        String UPLOADED_DOCUMENT_LIST = "v1/driver/document/get";
        String FAQ_LIST = "v1/driver/get/faqlist";
        String HEAT_MAP = "v1/driver/heatmap";
        String emailphnoCheckURL = "v1/driver/phone/email/availability";
        String getShareStatus = "v1/driver/get/share_status";
        String CANCEL_REASON_LIST_URL = "v1/cancellation/list";
        String GET_LOGIN_OTP = "v1/driver/login/otp";
        String GET_PROFILE_DETAILS = "v1/driver/profile/retrieve";
        String SUPPORT = "v1/driver/support/email";
        String ADD_ADDIONAL_CHAGE = "v1/driver/additionalCharge/add";
        String DELETE_ADDIONAL_CHAGE = "v1/driver/additionalCharge/delete";
        String LOCATION_UPDATED = "v1/driver/location/update";
    }

    public interface TaskID {
        int LOGOUT = 101;
        int TOGGLE_STATE = 102;
        int TOGGLE_SHARE = 103;
        int COMPLAINT_LIST = 104;
        int REPORT_COMPLAINT = 105;
        int RESPOND_REQUEST = 106;
        int REQUEST_IN_PROGRESS = 107;
        int CANCEL_REQUEST = 108;
        int DRIVER_ARRIVED = 109;
        int TRIP_STARTED = 110;
        int TRIP_COMPLETED = 111;
        int GENERATE_TOKEN = 112;
        int REVIEW_DRIVER = 113;
        int SINGLE_HISTORY = 114;
        int GET_HISTORY = 115;
        int GET_CURRENT_COUNTRY = 116;
        int GET_HEATMAP_DATA = 117;
        int SHARE_STATE = 118;
        int LOCATION_UPDATE = 119;
    }

    public interface SuccessMessages {
        String LOGOUT_Message = "logged_out_successfully";
        String REQUEST_IN_PROGRESS_Message = "driver request inprogress";
        String LOCATION_UPDATED_SUCCESSFULLY = "location_updated_successfully";
        String ADDITIONAL_CHARGE_DELETED_SUCCESSFULLY = "additional_charge_deleted_successfully";
        String AREA_LIST_Message = "driver admin list";
    }

    public interface ErrorCode {
        Integer TOKEN_EXPIRED = 609;
        Integer TOKEN_MISMATCHED = 606;
        Integer INVALID_LOGIN = 603;
        Integer REQUEST_ALREADY_ENDED = 727;
        Integer REQUEST_NOT_REGISTERED = 803;
        Integer REQUEST_ALREADY_CANCELLED = 808;
        Integer REQUEST_ALREADY_CANCELLED2 = 805;
        Integer DRIVER_ALREADY_ARRiVED = 810;
        Integer DRIVER_ALREADY_STARTED = 809;
        Integer DRIVER_ALREADY_RATED = 904;
        Integer CONTACT_ADMIN = 903;
    }

    public interface BroadcastsActions {
        String NEW_REQUEST = "NEW_REQUEST";
        String APPROVE_DECLINE = "APPROVE_DECLINE";
        String RideFromAdmin = "RideFromAdmin";
        String CANCEL_REASON = "cancel_reason";
        String LOCATION_UPDATING_SERVICE = "LOCATION_UPDATING_SERVICE";
        String CANCEL_RECEIVER = "cancel_receiver";
    }

    public interface setResult {
        int HISTORY_RESULTS = 101;
    }

    public interface IntentExtras {
        String REQUEST_DATA = "request_data";
        String WAITING_TIME = "waiting_time";
        String PREVAILING_WAITING_TIME = "PrevailingWAiting";
        String PREVAILING_WAITING_SEC= "prevailing_waiting_sec";
        String CANCEL_REASON = "cancel_reason";
        String ADD_CHARGE_VALUES = "ADD_CHARGE_VALUES";
        String ACCEPT_REJECT_DATA = "accept_reject_data";
        String LOCATION_DATA = "location_data";
        String LAST_UPDATED = "LAST_UPDATED";
        String USER_PHONE = "USER_PHONE";
        String USER_COUNTRY = "USER_COUNTRY";
        String LOCATION_ID = "LOCATION_ID";
        String LOCATION_LAT = "LAT";
        String LOCATION_LNG= "LNG";
        String LOCATION_BEARING= "BEARING";
    }

    public interface NetworkParameters {
        String firstname = "firstname";
        String lastname = "lastname";
        String email = "email";
        String password = "password";
        String phone = "phone";
        String type = "type";
        String car_number = "car_number";
        String car_model = "car_model";
        String profile_pic = "profile_pic";
        String device_token = "device_token";
        String login_by = "login_by";
        String login_method = "login_method";
        String social_unique_id = "social_unique_id";
        String admin_id = "admin_id";
        String additionalCharges = "additionalCharges";
        String country = "country";
        String country_code = "country_code";
        String license = "license";
        String license_name = "license_name";
        String amount = "amount";
        String name = "name";
        String license_date = "license_date";
        String insurance = "insurance";
        String insurance_name = "insurance_name";
        String insurance_date = "insurance_date";
        String rcbook = "rcbook";
        String rcbook_name = "rcbook_name";
        String rcbook_date = "rcbook_date";
        String BEARING = "bearing";

        String id = "id";
        String token = "token";
        String message = "message";
        String otp_key = "otp_key";
        String username = "username";
        String phone_number = "phone_number";
        String android = "android";
        String manual = "manual";
        String facebook = "facebook";
        String google = "google";

        String SET_LOCATION = "set_location";
        String TRIP_LOCATION = "trip_location";
        String START_CONNECT = "start_connect";
        String DRIVER_REQUEST = "driver_request";
        String REQUEST_HANDLER = "request_handler";

        String LAT = "lat";
        String LNG = "lng";
        String OLD_PASSWORD = "old_password";
        String NEW_PASSWORD = "new_password";

        String PLATITUDE = "platitude";
        String PLONGITUDE = "plongitude";
        String PLOCATION = "plocation";
        String DLATITUDE = "dlatitude";
        String DLONGITUDE = "dlongitude";
        String DLOCATION = "dlocation";
        String IS_RESPONSE = "is_response";
        String REQUEST_ID = "request_id";
        String IS_SHARE = "is_share";
        String RESASON = "reason";

        String TITLE = "title";
        String DESCRIPTION = "description";
        String USER_ID = "user_id";
        String TRIP_START = "trip_start";
        String TRIP_STATUS = "trip_status_driver";
        String DISTANCE = "distance";
        String BEFORE_WAITING_TIME = "before_waiting_time";
        String AFTER_WAITING_TIME = "after_waiting_time";
        String RATING = "rating";
        String COMMENT = "comment";
        String page = "page";
        String document_name = "document_name";
        String document_image = "document_image";
        String expiry_date = "expiry_date";
        String identify_number = "identify_number";
        String cancel_other_reason = "cancel_other_reason";
        String car_make = "car_make";
        String car_year = "car_year";
        String current_latitude = "current_latitude";
        String current_longitude = "current_longitude";
        String phoneNumber = "phoneNumber";
        String additional_charge_id = "additional_charge_id";
        String is_signup = "is_signup";
        String LAT_LNG_ARRAY="lat_lng_array";
        String RANDOM="RANDOM";
    }

    public final static String OFFLINE = "Offline";
    public final static String ONLINE = "Online";
    public final static String DRIVER_LIST_TYPES = "2";
}
