package bz.pei.driver.ui.drawerscreen.dialog.sharebilldialog;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.base.BaseResponse;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

public class ShareBillDialogViewModel extends BaseNetwork<BaseResponse, ShareBillDialogNavigator> {


    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> profileImage = new ObservableField<>();
    public ObservableField<String> totalAmount = new ObservableField<>();

    public ShareBillDialogViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence) {
        super(gitHubService);
    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    @BindingAdapter("ProfileImage")
    public static void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).
                apply(RequestOptions.circleCropTransform().error(R.drawable.ic_user).
                        placeholder(R.drawable.ic_user)).into(imageView);
    }

    public void setDetails(RequestModel model) {
        name.set(model.request.user.firstname + " " + model.request.user.lastname);
        profileImage.set(model.request.user.profilePic);
        totalAmount.set(model.request.bill.currency + model.request.bill.total + "");
    }

    public void onConfirm(View view) {
        getmNavigator().showShareListScrn();

    }
}
