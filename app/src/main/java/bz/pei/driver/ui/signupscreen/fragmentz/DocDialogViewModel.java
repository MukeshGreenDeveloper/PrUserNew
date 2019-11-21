package bz.pei.driver.ui.signupscreen.fragmentz;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by root on 9/25/17.
 */


public class DocDialogViewModel extends BaseNetwork{
    public ObservableField<String> img_license = new ObservableField<>();
    public ObservableField<String> img_insurance = new ObservableField<>();
    public ObservableField<String> img_rcbook = new ObservableField<>();
    public ObservableField<String> bitmap = new ObservableField<>();
    public ObservableField<String> title_doc = new ObservableField<>("");
    public ObservableField<String> exp_doc = new ObservableField<>("");

    //EmptyViewModel created if we are not gng to access viewmodel..
    @Inject
    Context activity;

    //EmptyViewModel created if we are not gng to access viewmodel..
    @Inject
    public DocDialogViewModel(GitHubService gitHubService,SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService,sharedPrefence,gson);

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
    public void onSuccessfulApi(long taskId, Object response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }
}
