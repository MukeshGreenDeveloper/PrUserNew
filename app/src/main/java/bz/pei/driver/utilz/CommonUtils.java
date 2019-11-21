/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package bz.pei.driver.utilz;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import androidx.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BitmapCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import bz.pei.driver.app.MyApp;
import bz.pei.driver.fcm.MyFirebaseMessagingService;
import bz.pei.driver.pojos.drawpath.BeanRoute;
import bz.pei.driver.pojos.drawpath.BeanStep;
import bz.pei.driver.pojos.drawpath.PolyLineUtils;
import bz.pei.driver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by amitshekhar on 07/07/17.
 */

public final class CommonUtils {

    /**
     * Time difference threshold set for one minute.
     */
    static final int TIME_DIFFERENCE_THRESHOLD = 1 * 60 * 1000;

    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }


    public static <T> int getListSize(List<T> list) {
        if (list != null && list.size() > 0) {
            return list.size();
        }
        return 0;
    }

    @SuppressLint("all")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String loadJSONFromAsset(Context context, String jsonFileName)
            throws IOException {

        AssetManager manager = context.getAssets();
        InputStream is = manager.open(jsonFileName);

        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return new String(buffer, "UTF-8");
    }

    public static boolean IsEmpty(String s) {
        return s == null || s.isEmpty() || s.equalsIgnoreCase("null") || s.length() == 0;
    }

   /* public static String getTimeStamp() {
        return new SimpleDateFormat(AppConstants.TIMESTAMP_FORMAT, Locale.US).format(new Date());
    }*/

    @BindingAdapter({"image"})
    public static void loadImage(ImageView view, String url) {
        //    Glide.with(view.getContext()).load(url).centerCrop().into(view);
    }


    public static String WriteImage(ByteArrayOutputStream bytes) {
        File ExternalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(ExternalStorageDirectory + File.separator + "profile.jpg");
        FileOutputStream fileOutputStream = null;
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes.toByteArray());

            return file.getAbsolutePath();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    public static RequestBody createRequestBody(@NonNull File file) {
        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), file);
    }

    public static RequestBody createRequestBody(@NonNull String s) {
        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), s);
    }

    public static String getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage = getResizedBitmapLessThan500KB(inImage, 500);
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = null;//= MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        try {
            path = Environment.getExternalStorageDirectory().toString();
            OutputStream fOut = null;
            File file = new File(path, System.currentTimeMillis() + ".jpg");
            fOut = new FileOutputStream(file);
            inImage.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close();
            path = file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (path);
    }

    public static Bitmap getResizedBitmapLessThan500KB(Bitmap image, int maxSize) {

        if (BitmapCompat.getAllocationByteCount(image) > (500 * 1024)) {
            int width = image.getWidth();
            int height = image.getHeight();
            float bitmapRatio = (float) width / (float) height;
            if (bitmapRatio > 0) {
                width = maxSize;
                height = (int) (width / bitmapRatio);
            } else {
                height = maxSize;
                width = (int) (height * bitmapRatio);
            }
            return Bitmap.createScaledBitmap(image, width, height, true);
        } else {
            return image;
        }
    }

    public static <T> T getSingleObject(String json, Class<T> modelclass) {
        try {
            return new Gson().fromJson(json, modelclass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> String getStringFromObject(T model) {
        return new Gson().toJson(model);
    }

    public static int dip2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static Drawable getMaskDrawable(Context context, int maskId) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getDrawable(maskId);
        } else {
            drawable = context.getResources().getDrawable(maskId);
        }

        if (drawable == null) {
            throw new IllegalArgumentException("maskId is invalid");
        }

        return drawable;
    }


    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = Constants.URL.DRAW_PATH + output + "?" + parameters;
        return url;

    }

    /**
     * Decide if new location is better than older by following some basic criteria.
     * This algorithm can be as simple or complicated as your needs dictate it.
     * Try experimenting and get your best  strategy algorithm.
     *
     * @param oldLocation Old location used for comparison.
     * @param newLocation Newly acquired location compared to old one.
     * @return If new location is more accurate and suits your criteria more than the old one.
     */
    public static boolean isBetterLocation(Location oldLocation, Location newLocation) {
        // If there is no old location, of course the new location is better.
        if (oldLocation == null) {
            return true;
        }
        // Check if new location is newer in time.
        boolean isNewer = newLocation.getTime() > oldLocation.getTime();
        // Check if new location more accurate. Accuracy is radius in meters, so less is better.
        boolean isMoreAccurate = newLocation.getAccuracy() < oldLocation.getAccuracy();
        if (isMoreAccurate && isNewer) {
            // More accurate and newer is always better.
            return true;
        } else if (isMoreAccurate && !isNewer) {
            // More accurate but not newer can lead to bad fix because of user movement.
            // Let us set a threshold for the maximum tolerance of time difference.
            long timeDifference = newLocation.getTime() - oldLocation.getTime();
            // If time difference is not greater then allowed threshold we accept it.
            if (timeDifference > -TIME_DIFFERENCE_THRESHOLD) {
                return true;
            }
        }

        return false;
    }

    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */
    public static BeanRoute parseRoute(String response, BeanRoute routeBean) {

        try {
            BeanStep stepBean;
            JSONObject jObject = new JSONObject(response);
            JSONArray jArray = jObject.getJSONArray("routes");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject innerjObject = jArray.getJSONObject(i);
                if (innerjObject != null) {
                    JSONArray innerJarry = innerjObject.getJSONArray("legs");
                    for (int j = 0; j < innerJarry.length(); j++) {

                        JSONObject jObjectLegs = innerJarry.getJSONObject(j);
                        routeBean.setDistanceText(jObjectLegs.getJSONObject(
                                "distance").getString("text"));
                        routeBean.setDistanceValue(jObjectLegs.getJSONObject(
                                "distance").getInt("value"));

                        routeBean.setDurationText(jObjectLegs.getJSONObject(
                                "duration").getString("text"));
                        routeBean.setDurationValue(jObjectLegs.getJSONObject(
                                "duration").getInt("value"));

                        routeBean.setStartAddress(jObjectLegs
                                .getString("start_address"));
                        routeBean.setEndAddress(jObjectLegs
                                .getString("end_address"));

                        routeBean.setStartLat(jObjectLegs.getJSONObject(
                                "start_location").getDouble("lat"));
                        routeBean.setStartLon(jObjectLegs.getJSONObject(
                                "start_location").getDouble("lng"));
                        routeBean.setEndLat(jObjectLegs.getJSONObject(
                                "end_location").getDouble("lat"));
                        routeBean.setEndLon(jObjectLegs.getJSONObject(
                                "end_location").getDouble("lng"));

                        JSONArray jstepArray = jObjectLegs
                                .getJSONArray("steps");
                        if (jstepArray != null) {
                            for (int k = 0; k < jstepArray.length(); k++) {
                                stepBean = new BeanStep();
                                JSONObject jStepObject = jstepArray
                                        .getJSONObject(k);
                                if (jStepObject != null) {

                                    stepBean.setHtml_instructions(jStepObject
                                            .getString("html_instructions"));
                                    stepBean.setStrPoint(jStepObject
                                            .getJSONObject("polyline")
                                            .getString("points"));
                                    stepBean.setStartLat(jStepObject
                                            .getJSONObject("start_location")
                                            .getDouble("lat"));
                                    stepBean.setStartLon(jStepObject
                                            .getJSONObject("start_location")
                                            .getDouble("lng"));
                                    stepBean.setEndLat(jStepObject
                                            .getJSONObject("end_location")
                                            .getDouble("lat"));
                                    stepBean.setEndLong(jStepObject
                                            .getJSONObject("end_location")
                                            .getDouble("lng"));

                                    stepBean.setListPoints(new PolyLineUtils()
                                            .decodePoly(stepBean.getStrPoint()));
                                    routeBean.getListStep().add(stepBean);
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return routeBean;
    }


    public static BitmapDescriptor getBitmapDescriptor(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static String doubleDecimalFromat(Double value) {
        if (value == null)
            return value + "";
        String result = value + "";
        try {
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
            otherSymbols.setDecimalSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("0.00", otherSymbols);
            result = decimalFormat.format(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String trippleDecimalFromat(Double value) {
        if (value == null)
            return value + "";
        String result = value + "";
        try {
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
            otherSymbols.setDecimalSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("0.000", otherSymbols);
            result = decimalFormat.format(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap getBitmap(VectorDrawableCompat vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawableCompat) {
            return getBitmap((VectorDrawableCompat) drawable);
        } else if (drawable instanceof VectorDrawable) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    public static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) {
//        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        Drawable drawable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            drawable = ContextCompat.getDrawable(context, drawableId);
        } else
            drawable = VectorDrawableCompat.create(context.getResources(), drawableId, context.getTheme());

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat || drawable instanceof VectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    public static boolean validateDate(String selectedDate) {
        /*boolean result = false;*/
        try {
            if (IsEmpty(selectedDate))
                return false;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            sdf.setLenient(false);
            Date date = sdf.parse(selectedDate);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean validateDate1(String selectedDate) {
        /*boolean result = false;*/
        try {
            if (IsEmpty(selectedDate))
                return false;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            sdf.setLenient(false);
            Date date = sdf.parse(selectedDate);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Format date dd-MM-yyyy
     * to yyyy-MM-dd
     */
    public static String formatDate(String selectedDate) {
        /*boolean result = false;*/
        try {
            if (IsEmpty(selectedDate))
                return null;
            SimpleDateFormat actualDateformat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            SimpleDateFormat resultDateformat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            actualDateformat.setLenient(false);
            Date date = actualDateformat.parse(selectedDate);

            return resultDateformat.format(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Format date yyyy-MM-dd
     * to dd-MM-yyyy
     */
    public static String reverseFormatDate(String selectedDate) {
        /*boolean result = false;*/
        try {
            if (IsEmpty(selectedDate))
                return null;
            SimpleDateFormat actualDateformat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat resultDateformat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            actualDateformat.setLenient(false);
            Date date = actualDateformat.parse(selectedDate);
            return resultDateformat.format(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String removeFirstZeros(String phoneNumber) {
        String resolvedPhoeNumber = phoneNumber;
        do {
            if (resolvedPhoeNumber.startsWith("0"))
                resolvedPhoeNumber = resolvedPhoeNumber.substring(1, resolvedPhoeNumber.length() == 1 ? resolvedPhoeNumber.length() : resolvedPhoeNumber.length());
        } while (resolvedPhoeNumber.startsWith("0"));
        return resolvedPhoeNumber;
    }

    public static String removeExtraCharsFirstZero(String input) {
        String resolvedStr = input;
        resolvedStr = input.replace("-", "").
                replace("_", "").
                replace(" ", "");
        return removeFirstZeros(resolvedStr);
    }

    public static boolean checkLocationorGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(context)) {
//          location not enabled
            MyFirebaseMessagingService.displayNotificationConnectivity(context, MyApp.getmContext().getString(R.string.text_location_notification_hint));
            return false;
        } else {

            MyFirebaseMessagingService.cancelConnectivityNotification(context);
            return true;
        }
    }

    public static boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    public static String removeExtraCharacters(String input) {

        input = input.trim().replace("-", "").
                replace("_", "").
                replace(" ", "");
        return input;

    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkLocationorGPSEnabled() {
        LocationManager locationManager = (LocationManager) MyApp.getmContext().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(MyApp.getmContext()))
            return false;
        else
            return true;
    }

    public static boolean isNetworkConnected(Context context) {
        boolean result = NetworkUtils.isNetworkConnected(context);
        return result;
    }

    public static void startPowerSaverIntent(final Context context) {
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            boolean foundCorrectIntent = false;
            for (final Intent intent : Constants.POWERMANAGER_INTENTS) {
                if (isCallable(context, intent)) {
                    foundCorrectIntent = true;
                    final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
                    dontShowAgain.setText("Do not show again");
                    dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            editor.putBoolean("skipProtectedAppCheck", isChecked);
                            editor.apply();
                        }
                    });

                    new AlertDialog.Builder(context)
                            .setTitle(Build.MANUFACTURER + " Protected Apps")
                            .setMessage((Constants.POWERMANAGER_INTENTS.get(7).toString().equalsIgnoreCase(intent.toString())) ?
                                    (context.getString(R.string.app_name) + " requires to disable App Intelligent Power-saving to functin properly Goto Settings-> Battery-> Apps Intelligent Power-Saving" +
                                            "Disable - App Freezer, Synchronous Wake-up, Startup Manager")
                                    : String.format("%s requires to be disabled in 'Protected Apps' to function properly.%n", context.getString(R.string.app_name)))
                            .setView(dontShowAgain)
                            .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Constants.POWERMANAGER_INTENTS.get(7).toString().equalsIgnoreCase(intent.toString())) {
//                                        context.startActivity(intent);
                                        context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                                    } else {
                                        if (Build.MANUFACTURER.equalsIgnoreCase("oppo")) {
                                            requestAutoStartPermission(context);
                                        } else
                                            context.startActivity(intent);
                                    }

                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                    break;
                }
            }
        }
    }

    private static boolean isCallable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static boolean isPowerSaveMode(Context context) {
        PowerManager powerManager = (PowerManager)
                context.getSystemService(Context.POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP
                && powerManager.isPowerSaveMode())
            return true;
        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && powerManager.isPowerSaveMode())
            return !powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        else
            return false;
    }

    public static void setwifilock(Context context) {
        WifiManager.WifiLock wifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        if (wifiLock.isHeld()) {
            wifiLock.release();
            setNeverSleepPolicy(context);
        } else {
            wifiLock.acquire();
        }
    }

    public static void setNeverSleepPolicy(Context context) {
        try {
            ContentResolver cr = context.getContentResolver();
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                int set = android.provider.Settings.System.WIFI_SLEEP_POLICY_NEVER;
                android.provider.Settings.System.putInt(cr, android.provider.Settings.System.WIFI_SLEEP_POLICY, set);
            } else {
                int set = android.provider.Settings.Global.WIFI_SLEEP_POLICY_NEVER;
                android.provider.Settings.System.putInt(cr, android.provider.Settings.Global.WIFI_SLEEP_POLICY, set);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void requestAutoStartPermission(Context context) {
        //com.coloros.safecenter.permission.singlepage.PermissionSinglePageActivity     listpermissions
        //com.coloros.privacypermissionsentry.PermissionTopActivity                     privacypermissions
        // getPackageManager().getLaunchIntentForPackage("com.coloros.safecenter");
        if (Build.MANUFACTURER.equalsIgnoreCase("oppo")) {
            try {
                context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.FakeActivity")));
            } catch (Exception e) {
                try {
                    context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupapp.StartupAppListActivity")));
                } catch (Exception e1) {
                    try {
                        context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupmanager.StartupAppListActivity")));
                    } catch (Exception e2) {
                        try {
                            context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startup.StartupAppListActivity")));
                        } catch (Exception e3) {
                            try {
                                context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startupapp.StartupAppListActivity")));
                            } catch (Exception e4) {
                                try {
                                    context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startupmanager.StartupAppListActivity")));
                                } catch (Exception e5) {
                                    try {
                                        context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startsettings")));
                                    } catch (Exception e6) {
                                        try {
                                            context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupapp.startupmanager")));
                                        } catch (Exception e7) {
                                            try {
                                                context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupmanager.startupActivity")));
                                            } catch (Exception e8) {
                                                try {
                                                    context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.startupapp.startupmanager")));
                                                } catch (Exception e9) {
                                                    try {
                                                        context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity.Startupmanager")));
                                                    } catch (Exception e10) {
                                                        try {
                                                            context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity")));
                                                        } catch (Exception e11) {
                                                            try {
                                                                context.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.FakeActivity")));
                                                            } catch (Exception e12) {
                                                                e12.printStackTrace();
                                                                try{
                                                                    context.startActivity(new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")));
                                                                }catch (Exception e13){
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
