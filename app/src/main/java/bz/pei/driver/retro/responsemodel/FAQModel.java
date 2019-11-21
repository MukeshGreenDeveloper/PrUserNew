package bz.pei.driver.retro.responsemodel;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;

/**
 * Created by root on 12/12/17.
 */

public class FAQModel {
    public ObservableBoolean clickedArrow=new ObservableBoolean(true);
    @Expose
    public String  id;
    @Expose
    public String  question;
    @Expose
    public String  answer;

    @Override
    public String toString() {
        return question;
    }
    public void onClickArrow(View v){
        clickedArrow.set(!clickedArrow.get());
    }
    @BindingAdapter({"bind:textfont"})
    public static void settextFont(TextView textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }
}
