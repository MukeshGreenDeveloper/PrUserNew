package bz.pei.driver.Retro.ResponseModel;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.Expose;
import bz.pei.driver.Retro.Base.BaseResponse;

import java.io.File;
import java.util.List;

/**
 * Created by root on 10/26/17.
 */

public class VehicleTypeModel extends BaseResponse {
    @Expose
    public List<Type> types = null;

    public static class Type {
        @Expose
        public String id;
        @Expose
        public String name;
        @Expose
        public String icon;


        @BindingAdapter("imageUrl")
        public static void setImageUrl(ImageView imageView, File url) {
            Context context = imageView.getContext();
            Glide.with(context).load(url).into(imageView);
        }

    }
}