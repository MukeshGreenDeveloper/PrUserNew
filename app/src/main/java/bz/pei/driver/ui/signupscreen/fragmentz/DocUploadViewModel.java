package bz.pei.driver.ui.signupscreen.fragmentz;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import bz.pei.driver.Pojos.RegisterationModel;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.LoginModel;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.RealPathUtil;
import bz.pei.driver.utilz.SharedPrefence;

import java.io.File;
import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by root on 10/9/17.
 */

public class DocUploadViewModel extends BaseNetwork<LoginModel, DocUploadNavigator> {
    GitHubService gitHubService;

    public ObservableField<String> bitmap_licence = new ObservableField<>("");
    public ObservableField<Boolean> is_licenceOK = new ObservableField<>(false);
    public String licence_title;//= new ObservableField<>();
    public String licence_expdate;//= new ObservableField<>();


    public ObservableField<String> bitmap_insurance = new ObservableField<>("");
    public ObservableField<Boolean> is_insuranceOK = new ObservableField<>(false);
    public String insurance_title;
    public String insurance_expdate;

    public ObservableField<String> bitmap_rcbook = new ObservableField<>("");
    public ObservableField<Boolean> is_rcbookOK = new ObservableField<>(false);
    public String rcbook_title;
    public String rcbook_expdate;

    public ObservableField<File> bitmap = new ObservableField<>();
    public int count = 1;//Current Selection
    String RealPath = null;
    File RealFile;
    //    @Inject
//    HashMap<String,String>Map;
    public static Context context;

    @Inject
    public DocUploadViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {//, @Named(Constants.REG_DATA)HashMap<String,String> map) {
        super(gitHubService,sharedPrefence,gson);
        this.gitHubService = gitHubService;
//        this.Map=map;
    }

    @Override
    public void onSuccessfulApi(long taskId, LoginModel response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        Log.d("Resp", "failure");

    }

    @Override
    public HashMap<String, String> getMap() {
        HashMap<String, String> map = new HashMap<>();
        if (is_licenceOK.get()) {
            map.put(Constants.NetworkParameters.license, bitmap_licence.get());
            map.put(Constants.NetworkParameters.license_name, licence_title);
            map.put(Constants.NetworkParameters.license_date, licence_expdate);
        }
        if (is_insuranceOK.get()) {
            map.put(Constants.NetworkParameters.insurance, bitmap_insurance.get());
            map.put(Constants.NetworkParameters.insurance_name, insurance_title);
            map.put(Constants.NetworkParameters.insurance_date, insurance_expdate);
        }
        if (is_rcbookOK.get()) {
            map.put(Constants.NetworkParameters.rcbook, bitmap_rcbook.get());
            map.put(Constants.NetworkParameters.rcbook_name, rcbook_title);
            map.put(Constants.NetworkParameters.rcbook_date, rcbook_expdate);
        }
        return map;
    }

    public void onClickDocUpload(View view) {

        switch (view.getId()) {
            case R.id.img_license_docuipload:
            case R.id.layout_licence_signup:
                count = Constants.LICENSE_DOC;
                getmNavigator().openDocUploadDialog(bitmap_licence);
                break;
            case R.id.img_insurance_docupload:
            case R.id.layout_insurance_signup:
                count = Constants.INSURANCE_DOC;
                getmNavigator().openDocUploadDialog(bitmap_insurance);
                break;
            case R.id.img_rcbook_docupload:
            case R.id.layout_rcbook_signup:
                count = Constants.RCBOOK_DOC;
                getmNavigator().openDocUploadDialog(bitmap_rcbook);
                break;
        }

    }

    public void onSelectFromGalleryResult(Intent data) {
        context = getmNavigator().getAttachedcontext();
        if (data != null) {
            try {
                RealPath = RealPathUtil.getRealPath(context, data.getData());
                Log.d("RealPath", RealPath);
                RealFile = new File(RealPath == null ? "" : RealPath);
                if (check_FileSize(RealFile)) {
                    bitmap.set(RealFile);
                    switch (count) {
                        case Constants.LICENSE_DOC:
                            bitmap_licence.set(RealPath);
                            break;
                        case Constants.INSURANCE_DOC:
                            bitmap_insurance.set(RealPath);
                            break;
                        case Constants.RCBOOK_DOC:
                            bitmap_rcbook.set(RealPath);
                            break;
                    }
                    getmNavigator().setupImage(RealPath);
                }

            } catch (Exception e) {
                Toast.makeText(getmNavigator().getAttachedcontext(), context.getString(R.string.txt_fileNotfound), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean check_FileSize(File file){
        long file_size = file.length();
        file_size = file_size / 1024;
//        Toast.makeText(getmNavigator().getAttachedcontext(), "File Size : " + file_size + " KB", Toast.LENGTH_SHORT).show();
        if(file_size>50) {
            Toast.makeText(getmNavigator().getAttachedcontext(),context.getString(R.string.txt_fileSizeTooLarge),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void onCaptureImageResult(Intent data) {
        context = getmNavigator().getAttachedcontext();
        try{
            RealPath = CommonUtils.getImageUri((Bitmap) data.getExtras().get("data"));
        Log.d("RealPath", RealPath);
        RealFile = new File(RealPath == null ? "" : RealPath);
        if (check_FileSize(RealFile)) {
            bitmap.set(RealFile);
            switch (count) {
                case Constants.LICENSE_DOC:
                    bitmap_licence.set(RealPath);
                    break;
                case Constants.INSURANCE_DOC:
                    bitmap_insurance.set(RealPath);
                    break;
                case Constants.RCBOOK_DOC:
                    bitmap_licence.set(RealPath);
                    break;
            }
            getmNavigator().setupImage(RealPath);
        }
        }catch(Exception e){
            Toast.makeText(getmNavigator().getAttachedcontext(),"File Not found",Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validateprepareApiCall() {
        String msg = null;
        if (is_licenceOK.get()) {
            if (CommonUtils.IsEmpty(bitmap_licence.get()))
                msg = "Licence photo left be Empty!";
            else if (CommonUtils.IsEmpty(licence_title))
                msg = "Licence DI number left be Empty!";
            else if (CommonUtils.IsEmpty(licence_expdate))
                msg = "Licence expiry left be Empty!";
        } else if (is_insuranceOK.get()) {
            if (CommonUtils.IsEmpty(bitmap_insurance.get()))
                msg = "Insurance photo left be Empty!";
            else if (CommonUtils.IsEmpty(insurance_title))
                msg = "Insurance Description number left be Empty!";
            else if (CommonUtils.IsEmpty(insurance_expdate))
                msg = "Insurance expiry left be Empty!";
        } else if (is_rcbookOK.get()) {
            if (CommonUtils.IsEmpty(bitmap_rcbook.get()))
                msg = "RC-Book photo left be Empty!";
            else if (CommonUtils.IsEmpty(rcbook_title))
                msg = "RC-Book Description number left be Empty!";
            else if (CommonUtils.IsEmpty(rcbook_expdate))
                msg = "RC-Book expiry left be Empty!";
        }else if(is_licenceOK.get()==false&&is_insuranceOK.get()==false&&is_rcbookOK.get()==false){
            msg = "Required any one legal document for registration!";
        }

        if (msg != null) {
            getmNavigator().showMessage(msg);
            return false;
        } else {
            if (is_licenceOK.get()) {
                RegisterationModel.getInstance().clearLicence();
                RegisterationModel.getInstance().license_photo = bitmap_licence.get();
                RegisterationModel.getInstance().license_desc = licence_title;
                RegisterationModel.getInstance().license_exp = CommonUtils.formatDate(licence_expdate);
            } else
                RegisterationModel.getInstance().clearLicence();
            if (is_insuranceOK.get()) {
                RegisterationModel.getInstance().clearInsurance();
                RegisterationModel.getInstance().insurance_photo = bitmap_insurance.get();
                RegisterationModel.getInstance().insurance_desc = insurance_title;
                RegisterationModel.getInstance().insurance_exp = CommonUtils.formatDate(insurance_expdate);
            } else
                RegisterationModel.getInstance().clearInsurance();
            if (is_rcbookOK.get()) {
                RegisterationModel.getInstance().clearRcbook();
                RegisterationModel.getInstance().rcBook_photo = bitmap_rcbook.get();
                RegisterationModel.getInstance().rcBook_desc = rcbook_title;
                RegisterationModel.getInstance().rcBook_exp = CommonUtils.formatDate(rcbook_expdate);
            } else
                RegisterationModel.getInstance().clearRcbook();
            return true;
        }

    }


}
