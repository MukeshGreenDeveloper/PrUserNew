package bz.pei.driver.utilz;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import org.json.JSONObject;

public class CustomResources extends Resources {
    /**
     * Create a new Resources object on top of an existing set of assets in an
     * AssetManager.
     *
     * @param assets  Previously created AssetManager.
     * @param metrics Current display metrics to consider when
     * selecting/computing resource values.
     * @param config  Desired device configuration to consider when
     * @deprecated Resources should not be constructed by apps.
     * See {@link Context#createConfigurationContext(Configuration)}.
     */
    private SharedPrefence sharedPrefence;

    public CustomResources(SharedPrefence sharedPrefence, AssetManager assets, DisplayMetrics metrics, Configuration config) {
        super(assets, metrics, config);
        this.sharedPrefence = sharedPrefence;
    }

    @NonNull
    @Override
    public String[] getStringArray(int id) throws NotFoundException {
        return super.getStringArray(id);
    }

    @NonNull
    @Override
    public String getString(int id) throws NotFoundException {
        if (getResourceEntryName(id).equalsIgnoreCase("app_name") || getResourceEntryName(id).equalsIgnoreCase("app_title"))
            return super.getString(id);
        else
            return getLocalizedString(getResourceEntryName(id));
    }

    public String getLocalizedString(String resId) {
        String result = resId;
        try {
            JSONObject jsonObject;
            if (!CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE))) {
                jsonObject = new JSONObject(sharedPrefence.Getvalue(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE)));
                result = jsonObject.optString(resId);
            }
           /* if (!CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE))) {
                if (sharedPrefence.Getvalue(SharedPrefence.LANGUANGE).equalsIgnoreCase(SharedPrefence.AR)) {
                    jsonObject = new JSONObject(sharedPrefence.Getvalue(SharedPrefence.AR));
                    result = jsonObject.optString(resId);
                } else if (sharedPrefence.Getvalue(SharedPrefence.LANGUANGE).equalsIgnoreCase(SharedPrefence.EN)) {
                    jsonObject = new JSONObject(sharedPrefence.Getvalue(SharedPrefence.EN));
                    result = jsonObject.optString(resId);
                }

            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (TextUtils.isEmpty(result))
                result = resId;
        }
        return result;

    }/*  public String getLocalizedString(String resId) {
        BufferedReader reader = null;
        String result = resId;
        try {
//            ObjectMapper
            reader = new BufferedReader(new InputStreamReader(openRawResource(R.raw.language), "UTF-8"));
            String line = "";
            String sline;
            while ((sline = reader.readLine()) != null) {
                line += sline;
            }
            JSONObject jsonObject = new JSONObject(line);
            if (!TextUtils.isEmpty(jsonObject.optString(resId)))
                result = jsonObject.optString(resId);
        } catch (Exception e) {
            e.printStackTrace();
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                    e.printStackTrace();

                }
            }
        }
        return result;
    }*/
}
