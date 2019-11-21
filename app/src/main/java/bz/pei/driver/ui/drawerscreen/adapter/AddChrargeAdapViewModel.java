package bz.pei.driver.ui.drawerscreen.adapter;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.drawerscreen.dialog.displayaddcharge.DisplayChargesDialog;
import bz.pei.driver.utilz.CommonUtils;

/**
 * Created by root on 26/7/18.
 */

public class AddChrargeAdapViewModel {
    private DisplayChargesDialog parentFragment;
    public RequestModel.AdditionalCharge manageDocModel;
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> amount = new ObservableField<>();
    public BaseActivity baseActivity;
    private int DOCUPLOAD_CODE = 500;

    public AddChrargeAdapViewModel(String currency,RequestModel.AdditionalCharge manageDocModel_,
                                   DisplayChargesDialog fragment, BaseActivity baseActivity) {
        this.parentFragment = fragment;
        this.manageDocModel = manageDocModel_;
        name.set(manageDocModel.name);
        amount.set((CommonUtils.IsEmpty(manageDocModel.amount)?"0.00":CommonUtils.doubleDecimalFromat(Double.valueOf(manageDocModel.amount))));
        this.baseActivity = baseActivity;
    }
    public AddChrargeAdapViewModel(String currency, RequestModel.AdditionalCharge manageDocModel_,
                                   BaseActivity baseActivity) {
        this.manageDocModel = manageDocModel_;
        name.set(manageDocModel.name);

        amount.set((CommonUtils.IsEmpty(manageDocModel.amount)?"0.00":CommonUtils.doubleDecimalFromat(Double.valueOf(manageDocModel.amount))));

        this.baseActivity = baseActivity;
    }

    public interface DocDeleteClickListener {
        void onDeleteClick(RequestModel.AdditionalCharge s);

    }

    public void onItemClick(View v) {

    }

    public void onDeleteClick(View v) {
            if (parentFragment != null)
                parentFragment.onDeleteClick(manageDocModel);

    }
    @BindingAdapter({"bind:textfont"})
    public static void settextFont(TextView textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }
}