package bz.pei.driver.ui.DrawerScreen.Fragmentz.Feedback;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.Retro.Base.BaseNetwork;
import bz.pei.driver.Retro.GitHubService;
import bz.pei.driver.Retro.ResponseModel.RequestModel;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.MyDataComponent;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

/**
 * Created by root on 12/28/17.
 */

public class FeedbackViewModel extends BaseNetwork<RequestModel, FeedbackNavigator> {
    public ObservableField<String> userPic = new ObservableField<>();
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableInt userReview = new ObservableInt();
    public ObservableField<String> txt_comments = new ObservableField<>();
    GitHubService gitHubService;
    SharedPrefence sharedPrefence;
    HashMap<String, String> map;

    public FeedbackViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, HashMap<String, String> map, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        DataBindingUtil.setDefaultComponent(new MyDataComponent(this));
        this.gitHubService = gitHubService;
        this.sharedPrefence = sharedPrefence;
        this.map = map;
        this.gson = gson;
    }

    public void setUserDetails(RequestModel model) {
        if (model != null)
            if (model.request != null)
                if (model.request.user != null) {
                    userName.set(model.request.user.firstname + " " + model.request.user.lastname);
                    if (!CommonUtils.IsEmpty(model.request.user.profilePic))
                        userPic.set(model.request.user.profilePic);
                }
    }

    public void updateReview(View view) {
        setIsLoading(true);
        reviewUser();
    }

    public boolean validataion() {
        String msg = null;
        if (msg != null) {
            getmNavigator().showMessage(msg);
            return false;
        }
        return true;
    }

    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        setIsLoading(false);
        if (response == null) {
            getmNavigator().showMessage(translationModel./*R.string.*/text_error_contact_server);
            return;
        }
        if (response.success) {
            if (response.successMessage.equalsIgnoreCase("rated successfully")) {
                sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
                sharedPrefence.saveIntValue(SharedPrefence.USER_ID, Constants.NO_ID);
                sharedPrefence.saveIntValue(SharedPrefence.IS_SHARE,Constants.NO_ID);
                sharedPrefence.saveIntValue(SharedPrefence.TRIP_START,Constants.NO_ID);
                getmNavigator().navigateToHomeFragment();
            }

        } else {
            if (response.errorCode == 904) {
                sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
                sharedPrefence.saveIntValue(SharedPrefence.USER_ID, Constants.NO_ID);
                sharedPrefence.saveIntValue(SharedPrefence.IS_SHARE,Constants.NO_ID);
                sharedPrefence.saveIntValue(SharedPrefence.TRIP_START,Constants.NO_ID);
                getmNavigator().navigateToHomeFragment();
            }
            getmNavigator().showMessage(response.errorMessage);
        }

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        e.printStackTrace();
        getmNavigator().showMessage(e.getMessage());
        if (e.getCode() == Constants.ErrorCode.REQUEST_ALREADY_CANCELLED
                || e.getCode() == Constants.ErrorCode.DRIVER_ALREADY_RATED
                || e.getCode() == Constants.ErrorCode.REQUEST_NOT_REGISTERED) {
            sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
            getmNavigator().navigateToHomeFragment();
        }


    }

    @Override
    public HashMap<String, String> getMap() {
        map.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        map.put(Constants.NetworkParameters.REQUEST_ID, sharedPrefence.getIntvalue(SharedPrefence.REQUEST_ID) + "");
        map.put(Constants.NetworkParameters.RATING, userReview.get() + "");
        map.put(Constants.NetworkParameters.COMMENT, CommonUtils.IsEmpty(txt_comments.get()) ? "" : txt_comments.get());
        return map;
    }

    @BindingAdapter("imageUrlFeedback")
    public void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()
                .error(R.drawable.ic_user)
                .placeholder(R.drawable.ic_user))
                .into(imageView);
    }
}
