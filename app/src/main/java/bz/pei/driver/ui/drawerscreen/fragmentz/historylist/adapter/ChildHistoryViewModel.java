package bz.pei.driver.ui.drawerscreen.fragmentz.historylist.adapter;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import bz.pei.driver.R;
import bz.pei.driver.retro.responsemodel.HistoryModel;
import bz.pei.driver.utilz.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by root on 1/4/18.
 */

public class ChildHistoryViewModel {
    //    ChidItemViewModelListener mListener;
    HistoryModel history;
    public ObservableField<String> driverurl, carurl, total, requestid, pickadd, dropAdd, DateTime;
    public ObservableBoolean Iscancelled,isShare;
    private ChidItemViewModelListener listener;

    SimpleDateFormat TargetFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH);
    SimpleDateFormat realformatter = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.ENGLISH);

    public ChildHistoryViewModel(HistoryModel request, ChidItemViewModelListener childHistoryViewHolder) {
        /*DataBindingUtil.setDefaultComponent(new MyComponent(this));*/
        this.history = request;
        this.listener = childHistoryViewHolder;
        driverurl = new ObservableField<>(request.usrepic);
        carurl = new ObservableField<>(request.typeIcon);
        DateTime = new ObservableField<>();
        try {
            if (request.tripStartTime != null)
                DateTime.set(TargetFormatter.format(realformatter.parse(request.tripStartTime)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        total = new ObservableField<>(request.currency + CommonUtils.doubleDecimalFromat(Double.valueOf(request.total)));
        requestid = new ObservableField<>(request.requestId + "");
        pickadd = new ObservableField<>(request.pickLocation);
        dropAdd = new ObservableField<>(request.dropLocation);
        isShare = new ObservableBoolean(request.is_share == 1);
        Iscancelled = new ObservableBoolean(request.isCancelled == 1);

    }

    public void onItemClick() {
        listener.onItemClick(history);
    }

    public interface ChidItemViewModelListener {
        void onItemClick(HistoryModel request);
    }

    @BindingAdapter("imageUrlcaricon")
    public static void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).apply(RequestOptions.fitCenterTransform()
                .error(R.drawable.ic_car)
                .placeholder(R.drawable.ic_car))
                .into(imageView);
    }


    @BindingAdapter("imageUrldrivericon")
    public static void setImageUrfl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()
                .error(R.drawable.ic_user)
                .placeholder(R.drawable.ic_user))
                .into(imageView);
    }
}
