package bz.pei.driver.Retro.Base;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import bz.pei.driver.Retro.ResponseModel.FAQModel;
import bz.pei.driver.Retro.ResponseModel.HistoryModel;
import bz.pei.driver.Retro.ResponseModel.TranslationModel;
import bz.pei.driver.utilz.SharedPrefence;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by root on 9/27/17.
 */

public class BaseResponse {
    @SerializedName("success")
    @Expose
    public Boolean success;

    @SerializedName("share_status")
    @Expose
    public Boolean shareStatus;

    @SerializedName("is_share")
    public int is_share;

    @SerializedName("error_code")
    @Expose
    public Integer errorCode;
    @SerializedName("error_message")
    @Expose
    public String errorMessage;
    @SerializedName("success_message")
    @Expose
    public String successMessage;
    @SerializedName("customer_care_number")
    @Expose
    public String customer_care_number;

    @Expose
    public String message;
//    @SerializedName("user")
//    public SignupModel user;

    @SerializedName("token")
    @Expose
    public String token;
    @Expose
    @SerializedName("data")
    public DataObject data;

    @Expose
    private List<HistoryModel> history;
    @Expose
    public List<FAQModel> faq_list;

    public List<HistoryModel> getHistory() {
        return history;
    }

    //    public SignupModel getUser() {
//        return user;
//    }
    public class DataObject {
        @Expose
        public TranslationModel en;
        @Expose
        public TranslationModel es;
        @Expose
        public TranslationModel ar;
        @Expose
        public TranslationModel ja;
        @Expose
        public TranslationModel ko;
        @Expose
        public TranslationModel pt;
        @Expose
        public TranslationModel zh;
    }
    public void saveLanguageTranslations(SharedPrefence sharedPrefence, Gson gson, DataObject dataObject) {
        JSONObject map = null;
        List<String> languages=new ArrayList<>();
        try {
            map = new JSONObject(gson.toJson(dataObject));
            Iterator<String> iterator=map.keys();
            while(iterator.hasNext()) {
                String key = iterator.next();
                sharedPrefence.savevalue(key, map.get(key).toString());
                languages.add(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sharedPrefence.savevalue(SharedPrefence.LANGUAGES, gson.toJson(languages)+"");
    }
}
