package bz.pei.driver.ui.drawerscreen.dialog.approval;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.base.BaseResponse;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by root on 9/25/17.
 */


public class ApprovalViewModel extends BaseNetwork<BaseResponse, ApprovalFragmentNavigator> {
    public ObservableField<String> detail_description = new ObservableField<>("");
    public ObservableField<String> buttonText = new ObservableField<>("");
    public ObservableBoolean isManageDocument = new ObservableBoolean(false);
    public String customerCareNumber;
    //EmptyViewModel created if we are not gng to access viewmodel..
    @Inject
    Context activity;
    SharedPrefence sharedPrefence;

    //EmptyViewModel created if we are not gng to access viewmodel..
    @Inject
    public ApprovalViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        if (!CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.CUSTOMER_CARE_NO)))
            customerCareNumber = sharedPrefence.Getvalue(SharedPrefence.CUSTOMER_CARE_NO);

    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).apply(RequestOptions.centerCropTransform()
                .error(R.drawable.ic_img_docupload)
                .placeholder(R.drawable.ic_img_docupload))
                .into(imageView);
    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }

    @BindingAdapter({"bind:textfont"})
    public static void settextFont(TextView textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    @BindingAdapter({"bind:Editfont"})
    public static void setEditFont(EditText textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    @BindingAdapter({"bind:Buttonfont"})
    public static void setButtonFont(Button textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    public void onCancelButon(View v) {
        if (isManageDocument.get()) {
            getmNavigator().openManageDocScreen();
        } else {
            if (!CommonUtils.IsEmpty(customerCareNumber))
                getmNavigator().makeCallCustomerCare(customerCareNumber);

        }
    }
}
