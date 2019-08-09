package bz.pei.driver.ui.DrawerScreen.Dialog.BillDialog;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.view.View;

import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.Retro.Base.BaseNetwork;
import bz.pei.driver.Retro.GitHubService;
import bz.pei.driver.Retro.ResponseModel.RequestModel;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

import static bz.pei.driver.utilz.CommonUtils.doubleDecimalFromat;

/**
 * Created by root on 12/28/17.
 */

public class BillDialogViewModel extends BaseNetwork<RequestModel, BillDialogNavigator> {
    public String time, distance, baseprice, distanceCost, distanceperunit, timeCost, timecostperunit,
            waitingPrice, serviceTAx, refferalBonus, promoBonus, walletAmount, roundOffAmount, total, totalAdditionalCharge;
    public String currency;
    public Context context;
    public boolean isWalletTrip,enableWaiting,enableRefferal,enablePromo,enableRoundoff,enableServiceTAx,enableWalletDeduction,enableAdditionalChrg;
    public ObservableBoolean isAddnlChargeAvailable = new ObservableBoolean(false);

    public BillDialogViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
    }

    public void setBillDetails(RequestModel model) {
        context = getmNavigator().getAttachedContext();
        if (model != null)
            if (model.request != null) {
                if (translationModel == null) {
                    time = doubleDecimalFromat(Double.valueOf(model.request.time != null ? model.request.time : "0")) + " " + context.getString(R.string.txt_min);
                    isWalletTrip = model.request.payment_opt == Constants.WALLET || model.request.payment_opt == Constants.WALLET_CASH;
                    if (model.request.bill != null) {
                        currency = CommonUtils.IsEmpty(model.request.bill.currency) ? context.getString(R.string.text_currency) : model.request.bill.currency;
                        RequestModel.Bill bill = model.request.bill;
                        baseprice = doubleDecimalFromat(bill.base_price);
                        distanceCost = doubleDecimalFromat(bill.distance_price);
                        distanceperunit = currency + bill.price_per_distance + " / " +
                                (bill.unit_in_words != null ? bill.unit_in_words : context.getString(R.string.text_km));
                        distance = doubleDecimalFromat(Double.valueOf(model.request.distance != null ? model.request.distance : "0")) + " " +
                                (bill.unit_in_words != null ? bill.unit_in_words : context.getString(R.string.text_km));
                        timeCost = doubleDecimalFromat(bill.time_price);
                        timecostperunit = currency + doubleDecimalFromat(bill.price_per_time) + " / " + context.getString(R.string.txt_min);

                        enableWaiting=(bill.waiting_price!=null &&bill.waiting_price>0);
                        waitingPrice = doubleDecimalFromat(bill.waiting_price);

                        enableServiceTAx=(bill.service_tax!=null &&bill.service_tax>0);
                        serviceTAx = doubleDecimalFromat(bill.service_tax);

                        enableRefferal=(bill.referral_amount!=null &&bill.referral_amount>0);
                        refferalBonus = "-" + doubleDecimalFromat(bill.referral_amount);

                        enablePromo=(bill.promo_amount!=null &&bill.promo_amount>0);
                        promoBonus = "-" + doubleDecimalFromat(bill.promo_amount);

                        enableWalletDeduction=(bill.wallet_amount!=null &&bill.wallet_amount>0);
                        walletAmount = doubleDecimalFromat(bill.wallet_amount);

                        enableRoundoff=(bill.round_up_minimum_price!=null &&bill.round_up_minimum_price>0);
                        roundOffAmount = doubleDecimalFromat(bill.round_up_minimum_price);

                        enableAdditionalChrg=(bill.totalAdditionalCharge!=null &&bill.totalAdditionalCharge>0);
                        totalAdditionalCharge = doubleDecimalFromat(bill.totalAdditionalCharge);
                        total = currency + " "+doubleDecimalFromat(bill.total);
                    }
                } else {
                    time = doubleDecimalFromat(Double.valueOf(model.request.time != null ? model.request.time : "0")) + " " + translationModel.txt_min;
                    isWalletTrip = model.request.payment_opt == Constants.WALLET || model.request.payment_opt == Constants.WALLET_CASH;
                    if (model.request.bill != null) {
                        currency = CommonUtils.IsEmpty(model.request.bill.currency) ? translationModel.text_currency : model.request.bill.currency;
                        RequestModel.Bill bill = model.request.bill;
                        baseprice =  doubleDecimalFromat(bill.base_price);
                        distanceCost =  doubleDecimalFromat(bill.distance_price);
                        distanceperunit = currency + bill.price_per_distance + " / " +
                                (bill.unit_in_words != null ? bill.unit_in_words : translationModel.text_km);
                        distance = doubleDecimalFromat(Double.valueOf(model.request.distance != null ? model.request.distance : "0")) + " " +
                                (bill.unit_in_words != null ? bill.unit_in_words : translationModel.text_km);
                        timeCost =  doubleDecimalFromat(bill.time_price);
                        timecostperunit = currency + doubleDecimalFromat(bill.price_per_time) + " / " + translationModel.txt_min;

                        enableWaiting=(bill.waiting_price!=null &&bill.waiting_price>0);
                        waitingPrice =  doubleDecimalFromat(bill.waiting_price);

                        enableServiceTAx=(bill.service_tax!=null &&bill.service_tax>0);
                        serviceTAx = doubleDecimalFromat(bill.service_tax);

                        enableRefferal=(bill.referral_amount!=null &&bill.referral_amount>0);
                        refferalBonus = "-" +  doubleDecimalFromat(bill.referral_amount);

                        enablePromo=(bill.promo_amount!=null &&bill.promo_amount>0);
                        promoBonus = "-" + doubleDecimalFromat(bill.promo_amount);

                        enableWalletDeduction=(bill.wallet_amount!=null &&bill.wallet_amount>0);
                        walletAmount =  doubleDecimalFromat(bill.wallet_amount);

                        enableRoundoff=(bill.round_up_minimum_price!=null &&bill.round_up_minimum_price>0);
                        roundOffAmount =  doubleDecimalFromat(bill.round_up_minimum_price);

                        enableAdditionalChrg=(bill.totalAdditionalCharge!=null &&bill.totalAdditionalCharge>0);
                        totalAdditionalCharge = doubleDecimalFromat(bill.totalAdditionalCharge);
                        total = currency +" "+doubleDecimalFromat(bill.total);
                    }

                }

            }
    }

    public void onConfirm(View v) {
        getmNavigator().dismissDialog();
    }

    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }
}
