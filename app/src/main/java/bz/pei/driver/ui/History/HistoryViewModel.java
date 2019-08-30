package bz.pei.driver.ui.History;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.Retro.Base.BaseNetwork;
import bz.pei.driver.Retro.GitHubService;
import bz.pei.driver.Retro.ResponseModel.LoginModel;
import bz.pei.driver.Retro.ResponseModel.RequestModel;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.MyDataComponent;
import bz.pei.driver.utilz.SharedPrefence;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import io.socket.client.Socket;

import static bz.pei.driver.utilz.CommonUtils.doubleDecimalFromat;

/**
 * Created by root on 10/9/17.
 */

public class HistoryViewModel extends BaseNetwork<RequestModel, HistoryNavigator> implements OnMapReadyCallback {

    @Inject
    Context context;

    SharedPrefence sharedPrefence;
    @Inject
    HashMap<String, String> map;
    @Inject
    Socket mSocket;

    public ObservableBoolean isCompleted = new ObservableBoolean(true);
    public ObservableBoolean isCancelled = new ObservableBoolean(false);
    public ObservableBoolean isShare = new ObservableBoolean(false);
    public ObservableBoolean showBill, enableWaiting, enableRefferal, enablePromo, enableRoundoff, enableServiceTAx, enableWalletDeduction;
    public ObservableBoolean isAddnlChargeAvailable = new ObservableBoolean(false);
    public ObservableField<LatLng> pickupLatlng = new ObservableField<>();
    public ObservableField<LatLng> dropLatlng = new ObservableField<>();
    public ObservableField<String> driverName,
            txt_Distance, txt_Time, txt_pick, txt_drop, txt_basePrice, txt_distanceCost,
            txt_price_perDistance, txt_TimeCost, txt_price_pertime, txt_refferalBonus,
            txt_promoBonus, txt_waitingCost, txt_total, txt_walletAmount, txt_paymentMode, txt_roundOffAmount, txt_serviceTax, bitmap_profilePic, datformated,
            requstID, totalAdditionalCharge;
    public ObservableInt driverRating = new ObservableInt();
    public static GoogleMap googleMap;
    String currency;
    private String requestID, driver_ID;
    private boolean isAlreadyDrawer = false;
    SimpleDateFormat TargetFormatter = new SimpleDateFormat("EE, MMM dd, hh:mm aa", Locale.US);
    SimpleDateFormat realformatter = new SimpleDateFormat("yyyy-MM-dd kk:mm");

    @Inject
    public HistoryViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                            SharedPrefence sharedPrefence,
                            Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        DataBindingUtil.setDefaultComponent(new MyDataComponent(this));
        enableWaiting = new ObservableBoolean(false);
        enableRefferal = new ObservableBoolean(false);
        enablePromo = new ObservableBoolean(false);
        enableRoundoff = new ObservableBoolean(false);
        enableServiceTAx = new ObservableBoolean(false);
        enableWalletDeduction = new ObservableBoolean(false);
        showBill = new ObservableBoolean(false);
        driverName = new ObservableField<>();
        txt_Distance = new ObservableField<>();
        txt_Time = new ObservableField<>();
        txt_pick = new ObservableField<>();
        txt_drop = new ObservableField<>();
        txt_basePrice = new ObservableField<>();
        txt_distanceCost = new ObservableField<>();
        txt_price_perDistance = new ObservableField<>();
        txt_TimeCost = new ObservableField<>();
        txt_price_pertime = new ObservableField<>();
        txt_refferalBonus = new ObservableField<>();
        txt_promoBonus = new ObservableField<>();
        txt_waitingCost = new ObservableField<>();
        txt_total = new ObservableField<>();
        txt_walletAmount = new ObservableField<>();
        txt_paymentMode = new ObservableField<>();
        txt_serviceTax = new ObservableField<>();
        txt_roundOffAmount = new ObservableField<>("0.00");
        bitmap_profilePic = new ObservableField<>();
        requstID = new ObservableField<>();
        datformated = new ObservableField<>();
        totalAdditionalCharge = new ObservableField<>("0");
    }

    @BindingAdapter({"bind:userPic"})
    public void userPicture(ImageView view, String profilePicURL) {
        if (!CommonUtils.IsEmpty(profilePicURL))
            Glide.with(view.getContext()).load(profilePicURL).
                    apply(RequestOptions.circleCropTransform().error(R.drawable.ic_user).
                            placeholder(R.drawable.ic_user)).into(view);
    }

    public void initializeValues(RequestModel model) {
        context = getmNavigator().getAttachedContext();
        if (model.request != null) {
            if (model.request.user != null) {
                bitmap_profilePic.set(model.request.user.profilePic);
                driverName.set(model.request.user.firstname + " " + model.request.user.lastname);
                driverRating.set((int) model.request.user.review);
            }
            pickupLatlng.set(model.request.getPickupLatlng());
            dropLatlng.set(model.request.getDropLatlng());
            isCompleted.set(model.request.is_completed == 1);
            isShare.set(model.request.is_share == 1);
            txt_Time.set(CommonUtils.doubleDecimalFromat(Double.valueOf(model.request.time)) + " " + context.getString(R.string.txt_min));
            txt_pick.set(model.request.pickLocation);
            txt_drop.set(model.request.dropLocation);
            showBill.set(model.request.is_cancelled == 0);
            isCancelled.set(model.request.is_cancelled == 1);
            if (model.request.bill != null && model.request.bill.base_price != null) {
                RequestModel.Bill bill = model.request.bill;
                currency = bill.currency;
                if (model.request.is_share == 1)
                    txt_basePrice.set(CommonUtils.doubleDecimalFromat(bill.ride_fare));
                else
                    txt_basePrice.set(CommonUtils.doubleDecimalFromat(bill.base_price));

                txt_distanceCost.set(CommonUtils.doubleDecimalFromat(bill.distance_price));
                txt_price_perDistance.set(currency + CommonUtils.doubleDecimalFromat(Double.valueOf(bill.price_per_distance)) + " / " + (bill.unit_in_words != null ? bill.unit_in_words : context.getString(R.string.text_km)));
                txt_Distance.set(CommonUtils.doubleDecimalFromat(Double.valueOf(model.request.distance != null ? model.request.distance : "0")) + " " +
                        (bill.unit_in_words != null ? bill.unit_in_words : context.getString(R.string.text_km)));
                txt_TimeCost.set(CommonUtils.doubleDecimalFromat(bill.time_price));
                txt_price_pertime.set(currency + CommonUtils.doubleDecimalFromat(bill.price_per_time) + " / " + context.getString(R.string.txt_min));

                enableRefferal.set(bill.referral_amount!=null &&bill.referral_amount>0);
                txt_refferalBonus.set("-" + CommonUtils.doubleDecimalFromat(bill.referral_amount));

                enablePromo.set(bill.promo_amount!=null &&bill.promo_amount>0);
                txt_promoBonus.set("-" + CommonUtils.doubleDecimalFromat(bill.promo_amount));

                enableServiceTAx.set(bill.service_tax!=null &&bill.service_tax>0);
                txt_serviceTax.set(CommonUtils.doubleDecimalFromat(bill.service_tax));

                enableWaiting.set(bill.waiting_price!=null &&bill.waiting_price>0);
                txt_waitingCost.set(CommonUtils.doubleDecimalFromat(bill.waiting_price));

                txt_walletAmount.set(currency + " " +CommonUtils.doubleDecimalFromat ((model.request.payment_opt == Constants.WALLET || model.request.payment_opt == Constants.WALLET_CASH) ? bill.wallet_amount : bill.total));

                enableRoundoff.set(bill.round_up_minimum_price!=null &&bill.round_up_minimum_price>0);
                txt_roundOffAmount.set((bill.round_up_minimum_price != null ? doubleDecimalFromat(bill.round_up_minimum_price) : "0.00"));
                totalAdditionalCharge.set(doubleDecimalFromat(bill.totalAdditionalCharge));
                txt_total.set(currency + " " +CommonUtils.doubleDecimalFromat(bill.total));
                showBill.set(bill.show_bill == 1);
                showBill.set(model.request.is_cancelled == 0);
                txt_paymentMode.set(context.getString(
                        model.request.payment_opt == 1 ? R.string.txt_cash :
                                model.request.payment_opt == 0 ? R.string.txt_card :
                                        model.request.payment_opt == 4 ? R.string.text_corporate :
                                                R.string.text_wallet));
                if (bill.additionalCharge != null && getmNavigator() != null) {
                    getmNavigator().setRecyclerAdapter(bill.currency, bill.additionalCharge);
                    isAddnlChargeAvailable.set(bill.additionalCharge.size() > 0);
                } else
                    isAddnlChargeAvailable.set(false);


            }
            try {
                datformated.set(TargetFormatter.format(realformatter.parse(model.request.trip_start_time)));
                requstID.set(model.request.requestId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            getDriverDetails();
        }

        if (!isAlreadyDrawer)
            if (pickupLatlng.get() != null && dropLatlng.get() != null)
                drawPathPickupDrop(pickupLatlng.get(), dropLatlng.get());

    }

    public void getDriverDetails() {
        if (sharedPrefence != null && driver_ID == null) {
            String driverDetails = sharedPrefence.Getvalue(SharedPrefence.DRIVERERDETAILS);
            LoginModel model = new Gson().fromJson(driverDetails, LoginModel.class);
            if (model != null)
                driver_ID = model.id + "";
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        if (!isAlreadyDrawer)
            if (pickupLatlng.get() != null && dropLatlng.get() != null)
                drawPathPickupDrop(pickupLatlng.get(), dropLatlng.get());
        if (googleMap != null)
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return true;
                }
            });
    }

    public void drawPathPickupDrop(final LatLng pickupLatLng, final LatLng dropLatLng) {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions()
                    .position(pickupLatLng)
                    .anchor(0.5f, 0.5f)
//                    .icon(CommonUtils.getBitmapDescriptor(context, R.drawable.ic_pickup)));
//                    .icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.getBitmapFromDrawable(getmNavigator().getAttachedContext(), R.drawable.ic_pickup))));
                    .icon(getmNavigator().getMarkerIcon(R.drawable.ic_pickup)));
            googleMap.addMarker(new MarkerOptions()
                    .position(dropLatLng)
                    .anchor(0.5f, 0.5f)
//                    .icon(CommonUtils.getBitmapDescriptor(context, R.drawable.ic_drop)));
                    .icon(getmNavigator().getMarkerIcon(R.drawable.ic_drop)));
        }
        buildLatlongBound(pickupLatLng, dropLatLng);
        isAlreadyDrawer = true;
    }

    public void buildLatlongBound(LatLng pickup, LatLng drop) {
        LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
        latlngBuilder.include(pickup);
        latlngBuilder.include(drop);
        if (googleMap != null)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latlngBuilder.build(), 80));
    }


    public void getRequestDetails(String requestID) {
        this.requestID = requestID;
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            getSingleHistory();
        }
    }

    @Override
    public HashMap<String, String> getMap() {
        map.put(Constants.NetworkParameters.REQUEST_ID, requestID);
        map.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        return map;
    }

    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        setIsLoading(false);
        if (response == null) {
            getmNavigator().showMessage(R.string.text_error_contact_server);
            return;
        }
        if (response.success) {
            if (response.successMessage.equalsIgnoreCase("driver_single_history")) {
                initializeValues(response);
            }
        } else
            getmNavigator().showMessage(response.errorMessage);
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        e.printStackTrace();
        getmNavigator().showMessage(e.getMessage());
    }

}
