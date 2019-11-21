package bz.pei.driver.ui.drawerscreen.fragmentz.managedocument;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.DriverModel;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.MyDataComponent;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 10/9/17.
 */

public class ManageDocumentViewModel extends BaseNetwork<RequestModel, ManageDocumentNavigator> {
    @Inject
    HashMap<String, String> Map;

    SharedPrefence sharedPrefence;
    Context context;
    List<RequestModel.Documents> documentsList = new ArrayList<>();
    RequestModel.Documents license_document, car_document, idcard_document, driver_passport_document,
            insurance_document, NTSA_document, good_conduct_document;
    public ObservableBoolean licenseUploaded, carUploaded, idcardUploaded, passportUploaded,
            insuracneUploaded, NTSAUploaded, goodconductUploaded, showOkButton;

    @Inject
    public ManageDocumentViewModel(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        DataBindingUtil.setDefaultComponent(new MyDataComponent(this));

        this.sharedPrefence = sharedPrefence;
        Map = new HashMap<>();
        license_document = new RequestModel.Documents("License");
        car_document = new RequestModel.Documents("Car Photo");
        idcard_document = new RequestModel.Documents("Identity Card");
        driver_passport_document = new RequestModel.Documents("Driver Passport Photo");
        insurance_document = new RequestModel.Documents("Insurance");
        NTSA_document = new RequestModel.Documents("NTSA Sticker");
        good_conduct_document = new RequestModel.Documents("Good Conduct Certificate");
        licenseUploaded = new ObservableBoolean(false);
        carUploaded = new ObservableBoolean(false);
        idcardUploaded = new ObservableBoolean(false);
        passportUploaded = new ObservableBoolean(false);
        insuracneUploaded = new ObservableBoolean(false);
        NTSAUploaded = new ObservableBoolean(false);
        goodconductUploaded = new ObservableBoolean(false);
        showOkButton = new ObservableBoolean(false);
        /*,car_document,idcard_document,driver_passport_document,
                insurance_document,NTSA_document,good_conduct_document*/
    }

    public void setUpData() {
        context = getmNavigator().getAttachedContext();
        setIsLoading(true);
        listDocs(getMap());
    }

    public void onclickPlus(View v) {
        switch (v.getId()) {
            case R.id.item_license:
                getmNavigator().openDocUploadDialog(license_document, true, true);
                break;
            case R.id.item_car_photo:
                getmNavigator().openDocUploadDialog(car_document, false, false);
                break;
            case R.id.item_id_card:
                getmNavigator().openDocUploadDialog(idcard_document, false, true);
                break;
            case R.id.item_driver_passport_photo:
                getmNavigator().openDocUploadDialog(driver_passport_document, false, false);
                break;
            case R.id.item_insurance:
                getmNavigator().openDocUploadDialog(insurance_document, true, false);
                break;
            case R.id.item_ntsa_sticker:
                getmNavigator().openDocUploadDialog(NTSA_document, true, false);
                break;
            case R.id.item_good_conduct_certificate:
                getmNavigator().openDocUploadDialog(good_conduct_document, false, true);
                break;

        }

    }

    @Override
    public HashMap<String, String> getMap() {
        String driverDetails = sharedPrefence.Getvalue(SharedPrefence.DRIVERERDETAILS);
        if (!TextUtils.isEmpty(driverDetails)) {
            DriverModel driverModel = CommonUtils.getSingleObject(driverDetails, DriverModel.class);
            if (driverModel != null) {
                Map.put(Constants.NetworkParameters.id, driverModel.id + "");
                Map.put(Constants.NetworkParameters.token, driverModel.token);

            }
        }
        return Map;
    }


    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        setIsLoading(false);
        if (response != null)
            if (response.success) {
                if (response.successMessage.equalsIgnoreCase("driver_document_uploaded") ||
                        response.successMessage.equalsIgnoreCase("driver_document_listed")) {
                    if (response.documents != null && response.documents.size() > 0) {
                        documentsList = response.documents;
                        checkAvailability(documentsList);
                    }
                }
            }
    }

    private void checkAvailability(List<RequestModel.Documents> documentsList) {
        for (RequestModel.Documents document : documentsList) {
            if (document.document_name.equalsIgnoreCase("License")) {
                license_document = document;
                licenseUploaded.set(true);
            } else if (document.document_name.equalsIgnoreCase("Car Photo")) {
                car_document = document;
                carUploaded.set(true);
            } else if (document.document_name.equalsIgnoreCase("Identity Card")) {
                idcard_document = document;
                idcardUploaded.set(true);
            } else if (document.document_name.equalsIgnoreCase("Driver Passport Photo")) {
                driver_passport_document = document;
                passportUploaded.set(true);
            } else if (document.document_name.equalsIgnoreCase("Insurance")) {
                insurance_document = document;
                insuracneUploaded.set(true);
            } else if (document.document_name.equalsIgnoreCase("NTSA Sticker")) {
                NTSA_document = document;
                NTSAUploaded.set(true);
            } else if (document.document_name.equalsIgnoreCase("Good Conduct Certificate")) {
                good_conduct_document = document;
                goodconductUploaded.set(true);
            }
        }
        if (licenseUploaded.get() && carUploaded.get() && idcardUploaded.get() && passportUploaded.get()
                && insuracneUploaded.get() && NTSAUploaded.get() && goodconductUploaded.get())
            showOkButton.set(true);
    }

    public void onConfirmToHome(View v) {
        getmNavigator().refreshToHome();
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED ||
                e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED ||
                e.getCode() == Constants.ErrorCode.INVALID_LOGIN)
            if (getmNavigator().getAttachedContext() != null) {
                getmNavigator().showMessage(getmNavigator().getAttachedContext().getString(R.string.text_already_login));
                ((DrawerAct) context).logoutApp();
            } else
                getmNavigator().showMessage(e.getMessage());
    }
}
