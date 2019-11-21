package bz.pei.driver.ui.drawerscreen.dialog.processdocumentupload;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import bz.pei.driver.R;
import bz.pei.driver.retro.base.BaseNetwork;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.retro.responsemodel.DriverModel;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Exception.CustomException;
import bz.pei.driver.utilz.RealPathUtil;
import bz.pei.driver.utilz.SharedPrefence;

import java.io.File;
import java.util.HashMap;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by root on 9/25/17.
 */


public class ProcessDocUploadDialogViewModel extends BaseNetwork<RequestModel, ProcessDocUploadDialogNavigator> {
    public ObservableField<String> bitmap_url = new ObservableField<>();
    public ObservableField<File> bitmap = new ObservableField<>();
    public ObservableField<String> title_doc = new ObservableField<>("");
    public ObservableField<String> number_doc = new ObservableField<>("");
    public ObservableField<String> exp_doc = new ObservableField<>("");
    public ObservableBoolean exp_Available = new ObservableBoolean();
    public ObservableBoolean number_Available = new ObservableBoolean();
    public SharedPrefence sharedPrefence;

    //EmptyViewModel created if we are not gng to access viewmodel..
    @Inject
    public ProcessDocUploadDialogViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson, HashMap<String, String> hashMap) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
    }

    @BindingAdapter("imageUrlDocument")
    public static void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        if (!TextUtils.isEmpty(url))
            Glide.with(context).load(url).apply(RequestOptions.centerCropTransform()
                    .error(R.drawable.ic_img_docupload)
                    .placeholder(R.drawable.ic_img_docupload))
                    .into(imageView);
    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }

    @Override
    public void onSuccessfulApi(long taskId, RequestModel response) {
        setIsLoading(false);
        if (response != null)
            if (response.success) {
                if (response.successMessage.equalsIgnoreCase("driver_document_uploaded") ||
                        response.successMessage.equalsIgnoreCase("driver_document_listed")) {
                        getmNavigator().dismissSuccessResult();
                }
            }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        e.printStackTrace();
        if (e.getMessage() != null)
            getmNavigator().showMessage(e.getMessage());
    }

    public void onClickCamera(View v) {
        getmNavigator().cameraIntent();
    }

    public boolean validateDocuUpload() {
        String msg = null;
        if (TextUtils.isEmpty(RealPath)) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_error_doc_pic_empty);
        } /*else if (TextUtils.isEmpty(title_doc.get())) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_error_doc_title_empty);
        } */ else if (exp_Available.get() && TextUtils.isEmpty(exp_doc.get())) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_error_doc_expiry_empty);
        } else if (exp_Available.get() && !CommonUtils.validateDate1(exp_doc.get())) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_error_doc_expiry_notvalid);
        } /*else if (exp_Available.get() && CommonUtils.formatDate(exp_doc.get()) == null) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_error_doc_expiry_notvalid);
        }*/ else if (number_Available.get() && CommonUtils.IsEmpty(number_doc.get())) {
            msg = getmNavigator().getAttachedcontext().getString(R.string.text_doc_identic_num);
        }
        if (msg != null)
            getmNavigator().showMessage(msg);
        return msg == null;
    }

    public void onClickSubmit(View v) {
        if (!validateDocuUpload())
            return;
        requestbody.put(Constants.NetworkParameters.document_name, RequestBody.create(MediaType.parse("text/plain"), title_doc.get()));
        String driverDetails = sharedPrefence.Getvalue(SharedPrefence.DRIVERERDETAILS);
        if (!TextUtils.isEmpty(driverDetails)) {
            DriverModel driverModel = CommonUtils.getSingleObject(driverDetails, DriverModel.class);
            if (driverModel != null) {
                if (!CommonUtils.IsEmpty(driverModel.id + ""))
                    requestbody.put(Constants.NetworkParameters.id, RequestBody.create(MediaType.parse("text/plain"), driverModel.id + ""));
                if (!CommonUtils.IsEmpty(driverModel.token))
                    requestbody.put(Constants.NetworkParameters.token, RequestBody.create(MediaType.parse("text/plain"), driverModel.token + ""));
            }

        }


        if (!CommonUtils.IsEmpty(RealPath)) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), new File(RealPath));
            body_profile_pic = MultipartBody.Part.createFormData(Constants.NetworkParameters.document_image, new File(RealPath).getName(), reqFile);
        }
        if (exp_Available.get())
            requestbody.put(Constants.NetworkParameters.expiry_date, RequestBody.create(MediaType.parse("text/plain"), exp_doc.get()+" 00:00:00"));
        if (number_Available.get())
            requestbody.put(Constants.NetworkParameters.identify_number, RequestBody.create(MediaType.parse("text/plain"), number_doc.get()));
        if (requestbody != null) {
            setIsLoading(true);
            uploadDoc();
        }
    }

    public void onClickDatePicker(View v) {
        getmNavigator().openDatePicker();
    }

    public void onClickGalary(View v) {
        getmNavigator().galleryIntent();
    }

    public void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                RealPath = RealPathUtil.getRealPath(getmNavigator().getAttachedcontext(), data.getData());
                Log.d("RealPath", RealPath);
                RealFile = new File(RealPath == null ? "" : RealPath);
                if (check_FileSize(RealFile)) {
                    bitmap.set(RealFile);
                    bitmap_url.set(RealPath);
                }

            } catch (Exception e) {
                Toast.makeText(getmNavigator().getAttachedcontext(), getmNavigator().getAttachedcontext().getString(R.string.txt_fileNotfound), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean check_FileSize(File file) {
        long file_size = file.length();
        file_size = file_size / 1024;
        if (file_size > 50) {
            Toast.makeText(getmNavigator().getAttachedcontext(), getmNavigator().getAttachedcontext().getString(R.string.txt_fileSizeTooLarge), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    String RealPath;
    File RealFile;

    public void onCaptureImageResult(Intent data) {
        getmNavigator().getAttachedcontext();
        try {
            RealPath = CommonUtils.getImageUri((Bitmap) data.getExtras().get("data"));
            Log.d("RealPath", RealPath);
            RealFile = new File(RealPath == null ? "" : RealPath);
            if (check_FileSize(RealFile)) {
                bitmap.set(RealFile);
                bitmap_url.set(RealPath);
            }
        } catch (Exception e) {
            Toast.makeText(getmNavigator().getAttachedcontext(), "File Not found", Toast.LENGTH_SHORT).show();
        }
    }
}
