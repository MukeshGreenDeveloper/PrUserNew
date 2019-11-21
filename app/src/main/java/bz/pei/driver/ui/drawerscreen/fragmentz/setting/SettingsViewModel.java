package bz.pei.driver.ui.drawerscreen.fragmentz.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import bz.pei.driver.BuildConfig;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.DriverModel;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.ui.Settings.SettingsNavigator;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.Language.LanguageUtil;
import bz.pei.driver.utilz.LocaleHelper;
import bz.pei.driver.utilz.MyDataComponent;
import bz.pei.driver.utilz.SharedPrefence;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 10/9/17.
 */

public class SettingsViewModel extends BaseNetwork<RequestModel, SettingsNavigator> {
    @Inject
    HashMap<String, String> Map;

    SharedPrefence sharedPrefence;
    Context context;
    String lang;

    public ObservableField<String> txt_Language_update = new ObservableField<>("");
    public ObservableField<String> mIsNotifySoundOn = new ObservableField<>("");
    public ObservableField<String> txtBuildDetail = new ObservableField<>("");
    public ObservableField<String> mIsShareSoundOn = new ObservableField<>("");
    public ObservableField<Boolean> isSoundEnabled = new ObservableField<>(false);
    public ObservableField<Boolean> isShareEnabled = new ObservableField<>(false);
    //    public ObservableField<File> bitmap = new ObservableField<>();

    @Inject
    public SettingsViewModel(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        DataBindingUtil.setDefaultComponent(new MyDataComponent(this));

        this.sharedPrefence = sharedPrefence;
        Map = new HashMap<>();
        txtBuildDetail.set(BuildConfig.VERSION_NAME+"\n"+Constants.URL.BaseURL.substring(0,Constants.URL.BaseURL.indexOf(".bz/")));
    }

    public void setUpData() {
        context = getmNavigator().getAttachedContext();
        lang = sharedPrefence.Getvalue(SharedPrefence.LANGUANGE);
        if (CommonUtils.IsEmpty(lang))
            sharedPrefence.savevalue(SharedPrefence.LANGUANGE, "en");
        txt_Language_update.set(CommonUtils.IsEmpty(lang) ? "en" : lang);

        mIsNotifySoundOn.set(context.getString(R.string.txt_on));
        isSoundEnabled.set(true);
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            getShrareStatusDriver(getMap());
        }
        if (sharedPrefence.getbooleanvalue(Constants.ShareState)) {
            mIsShareSoundOn.set(context.getString(R.string.txt_on));
            isShareEnabled.set(true);
        } else {
            mIsShareSoundOn.set(context.getString(R.string.txt_OFF));
            isShareEnabled.set(false);
        }
    }

    @Override
    public HashMap<String, String> getMap() {
        String driverDetails = sharedPrefence.Getvalue(SharedPrefence.DRIVERERDETAILS);
        if (!TextUtils.isEmpty(driverDetails)) {
            DriverModel driverModel = CommonUtils.getSingleObject(driverDetails, DriverModel.class);
            if (driverModel != null) {
                Map.put(Constants.NetworkParameters.id, driverModel.id + "");
                Map.put(Constants.NetworkParameters.token, driverModel.token);
            }
        }
        return Map;
    }


    @BindingAdapter("imageUrlSettings")
    public void setImageUrl(ImageView imageView, File url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).apply(RequestOptions.circleCropTransform().error(R.drawable.ic_user).placeholder(R.drawable.ic_user)).into(imageView);
    }

    /* @BindingAdapter("text_localize")
       public void setTxt_Language_update(TextView imageView, String url) {
       }
   */
    public Switch.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mIsNotifySoundOn.set(b ? translationModel.txt_on/*context.getString(R.string.txt_on) */ :
                    translationModel.txt_OFF/*context.getString(R.string.txt_OFF)*/);
            sharedPrefence.savebooleanValue(SharedPrefence.SOUND, b);
        }
    };
    public Switch.OnClickListener checkedShareListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            setIsLoading(true);
            toggleONOFFShare(getMap());
        }
    };


    public void showAlertDialog(View view) {
        final List<String> items = new ArrayList<>();
        if (sharedPrefence.Getvalue(SharedPrefence.LANGUAGES) != null) {
            for (String key : gson.fromJson(sharedPrefence.Getvalue(SharedPrefence.LANGUAGES), String[].class))
                switch (key) {
                    case "en":
                        items.add(getmNavigator().getAttachedContext().getString(R.string.text_English));
                        break;
                    case "ar":
                        items.add(getmNavigator().getAttachedContext().getString(R.string.text_Arabic));
                        break;
                    case "es":
                        items.add(getmNavigator().getAttachedContext().getString(R.string.text_Spanish));
                        break;
                    case "ja":
                        items.add(getmNavigator().getAttachedContext().getString(R.string.text_Japanese));
                        break;
                    case "ko":
                        items.add(getmNavigator().getAttachedContext().getString(R.string.text_Korean));
                        break;
                    case "pt":
                        items.add(getmNavigator().getAttachedContext().getString(R.string.text_Portugese));
                        break;
                    case "zh":
                        items.add(getmNavigator().getAttachedContext().getString(R.string.text_Chinese));
                        break;
                }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.text_select_language);
        builder.setItems(items.toArray(new String[items.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                txt_Language_update.set(items.get(item));
                String language = "English";
                language = items.get(item);
                String loc;
                if (language.equalsIgnoreCase(getmNavigator().getAttachedContext().getString(R.string.text_Portugese))) {
                    loc = "pt";
                } else if (language.equalsIgnoreCase(getmNavigator().getAttachedContext().getString(R.string.text_Arabic))) {
                    loc = "ar";
                } else if (language.equalsIgnoreCase(getmNavigator().getAttachedContext().getString(R.string.text_Chinese))) {
                    loc = "zh";
                } else if (language.equalsIgnoreCase(getmNavigator().getAttachedContext().getString(R.string.text_Spanish))) {
                    loc = "es";
                } else if (language.equalsIgnoreCase(getmNavigator().getAttachedContext().getString(R.string.text_Japanese))) {
                    loc = "ja";
                } else if (language.equalsIgnoreCase(getmNavigator().getAttachedContext().getString(R.string.text_Korean))) {
                    loc = "ko";
                } else {
                    loc = "en";
                }
                sharedPrefence.savevalue(SharedPrefence.LANGUANGE, loc);
                txt_Language_update.set(loc);
                if ("ar".equalsIgnoreCase(loc)) {
                    Locale locale = Locale.getDefault();
                    locale.setDefault(new Locale("ar"));
                    LanguageUtil.changeLanguageType(context, locale);
                } else if (!TextUtils.isEmpty(loc)) {
                    Locale locale = Locale.getDefault();
                    locale.setDefault(new Locale(loc));
                    LanguageUtil.changeLanguageType(context, locale);
                } else {
                    LanguageUtil.changeLanguageType(context, Locale.ENGLISH);
                }
                if ("ar".equalsIgnoreCase(loc)) {
                    Locale locale = Locale.getDefault();
                    locale.setDefault(new Locale("ar"));
                    LanguageUtil.changeLanguageType(context, locale);
                } else if (!TextUtils.isEmpty(loc)) {
                    Locale locale = Locale.getDefault();
                    locale.setDefault(new Locale(loc));
                    LanguageUtil.changeLanguageType(context, locale);
                } else {
                    LanguageUtil.changeLanguageType(context, Locale.ENGLISH);
                }
//                context.startActivity(new Intent(context, DrawerAct.class));
//                ((DrawerAct) context).finish();
                getmNavigator().refreshScreen();


            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void updateViews(String languageCode) {
        LocaleHelper.setLocale(context, languageCode);
        Resources resources = context.getResources();
    }

    public void changeShareState(int is_share, boolean show) {
        if (show)
            if (is_share == 1) {
                getmNavigator().showMessage(translationModel.text_share + " " + translationModel.txt_on);
            } else {
                getmNavigator().showMessage(translationModel.text_share + " " + translationModel.txt_OFF);
            }
        sharedPrefence.savebooleanValue(Constants.ShareState, is_share == 1 ? true : false);
        mIsShareSoundOn.set(is_share == 1 ? translationModel.txt_on : translationModel.txt_OFF);
        isShareEnabled.set(is_share == 1 ? true : false);

    }

    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        setIsLoading(false);
        if (response.successMessage.contains("driver_toogle_shared_state_successfully")) {
            //"Status Updated"))
            if (response != null)
                if (response.driver.share_state >= 0) {
                    changeShareState(response.driver.share_state, true);
                    getmNavigator().changeShareState(response.driver.share_state);
                }
        } else if (response.successMessage.contains("driver_toogle_shared_state_get_successfully"))
            //"Status Updated"))
            if (response != null)
                if (response.driver.share_state >= 0) {
                    changeShareState(response.driver.share_state, false);
                    getmNavigator().changeShareState(response.driver.share_state);
                }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED ||
                e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED ||
                e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if(getmNavigator().getAttachedContext()!=null)
                getmNavigator().showMessage(getmNavigator().getAttachedContext().getString(R.string.text_already_login));
            getmNavigator().logout();
        }
        else
            getmNavigator().showMessage(e.getMessage());
    }
}
