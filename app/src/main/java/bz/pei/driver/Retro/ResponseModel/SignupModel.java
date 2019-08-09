package bz.pei.driver.Retro.ResponseModel;

import bz.pei.driver.Retro.Base.BaseResponse;
import com.google.gson.annotations.Expose;

public class SignupModel extends BaseResponse  {
//
//    @SerializedName("driver")
    @Expose
    public DriverModel driver;
    @Expose
    public RequestModel.User user;
   /* @Expose
    public List<VehicleTypeModel.Type> types = null;

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

    }*/


}