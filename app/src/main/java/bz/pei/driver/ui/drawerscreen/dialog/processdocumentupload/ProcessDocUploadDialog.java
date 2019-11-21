package bz.pei.driver.ui.drawerscreen.dialog.processdocumentupload;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.DatePicker;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.databinding.DialogProcessUploadDocumentBinding;
import bz.pei.driver.ui.Base.BaseDialog;
import bz.pei.driver.ui.drawerscreen.fragmentz.managedocument.ManageDocumentFragment;
import bz.pei.driver.utilz.Constants;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import static bz.pei.driver.utilz.Constants.Array_permissions;
import static bz.pei.driver.utilz.Constants.REQUEST_PERMISSION;

/*import com.android.databinding.library.baseAdapters.BR;*/

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProcessDocUploadDialog#newInstance} factory method to
 * create an instance of this fragment.
 */

/**/
public class ProcessDocUploadDialog extends BaseDialog<DialogProcessUploadDocumentBinding, ProcessDocUploadDialogViewModel> implements ProcessDocUploadDialogNavigator {

    public static final String TAG = "ProcessDocUploadDialog";
    @Inject
    ProcessDocUploadDialogViewModel docUploadViewModel;
    /*   @Inject
       SharedPrefence sharedPrefence;*/
    public DialogProcessUploadDocumentBinding fragmentMapBinding;

    public ProcessDocUploadDialog() {
        // Required empty public constructor
    }

    public static final String paramDocName = "paramDocName", paramDocNumber = "paramDocNumber",
            paramDocExpiry = "paramDocExpiry", paramDocImageURL = "paramDocImageURL",
            paramExpiryAvailable = "paramExpiryAvailable", paramNumberAvailable = "paramNumberAvailable";

    public static ProcessDocUploadDialog newInstance(String name, String doc_Number, String doc_Expiry, String doc_imageURL,
                                                     boolean expiryAvailable, boolean numberAvailable) {
        ProcessDocUploadDialog fragment = new ProcessDocUploadDialog();
        Bundle bundle = new Bundle();
        bundle.putString(paramDocName, name);
        bundle.putString(paramDocNumber, doc_Number);
        bundle.putString(paramDocExpiry, doc_Expiry);
        bundle.putString(paramDocImageURL, doc_imageURL);
        bundle.putBoolean(paramExpiryAvailable, expiryAvailable);
        bundle.putBoolean(paramNumberAvailable, numberAvailable);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ProcessDocUploadDialog newInstance(String name, boolean expiryAvailable, boolean numberAvailable) {
        ProcessDocUploadDialog fragment = new ProcessDocUploadDialog();
        Bundle bundle = new Bundle();
        bundle.putString(paramDocName, name);
        bundle.putBoolean(paramExpiryAvailable, expiryAvailable);
        bundle.putBoolean(paramNumberAvailable, numberAvailable);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentMapBinding = getDataBinding();
        docUploadViewModel.setNavigator(this);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            docUploadViewModel.title_doc.set(bundle.getString(paramDocName));
            docUploadViewModel.number_doc.set(bundle.getString(paramDocNumber));
            docUploadViewModel.exp_doc.set(bundle.getString(paramDocExpiry));
            docUploadViewModel.bitmap_url.set(bundle.getString(paramDocImageURL));
            docUploadViewModel.exp_Available.set(bundle.getBoolean(paramExpiryAvailable));
            docUploadViewModel.number_Available.set(bundle.getBoolean(paramNumberAvailable));
        }
    }


    @Override
    public Context getAttachedcontext() {
        return getContext();
    }


    @Override
    public ProcessDocUploadDialogViewModel getViewModel() {
        return docUploadViewModel;
    }

    @Override
    public DialogProcessUploadDocumentBinding getDataBinding() {
        return fragmentMapBinding;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_process_upload_document;
    }


    public void galleryIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !getBaseActivity().checkGranted(Array_permissions)) {
            getBaseActivity().requestPermissionsSafely(Array_permissions, REQUEST_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.text_title_select_file)), Constants.SELECT_FILE);
        }
    }

    public void cameraIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !getBaseActivity().checkGranted(Array_permissions)) {
            getBaseActivity().requestPermissionsSafely(Array_permissions, REQUEST_PERMISSION);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Constants.REQUEST_CAMERA);
        }
    }

    DatePickerDialog datePickerDialog;
    Calendar cal = Calendar.getInstance();

    @Override
    public void openDatePicker() {
        Date date = new Date();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        if (datePickerDialog != null) {
            if (datePickerDialog.isShowing())
                datePickerDialog.dismiss();
        }
        datePickerDialog.show();
    }

    public DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            if (docUploadViewModel != null)
                if (docUploadViewModel.exp_doc != null)
//                    docUploadViewModel.exp_doc.set(dayOfMonth + "-" + (month + 1) + "-" + year);
                    docUploadViewModel.exp_doc.set(year + "-" + (month + 1) + "-" + dayOfMonth);
        }
    };

    @Override
    public void dismissSuccessResult() {
        if (getActivity() != null)
            if (getActivity().getSupportFragmentManager() != null)
                if (getActivity().getSupportFragmentManager().findFragmentByTag(ManageDocumentFragment.TAG) != null)
                    ((ManageDocumentFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ManageDocumentFragment.TAG)).documentUpdateDone = true;
        ((ManageDocumentFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ManageDocumentFragment.TAG)).Setup();
        dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.SELECT_FILE) {
                docUploadViewModel.onSelectFromGalleryResult(data);
            } else if (requestCode == Constants.REQUEST_CAMERA) {
                docUploadViewModel.onCaptureImageResult(data);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if ((requestCode == REQUEST_PERMISSION) && checkGranted(grantResults)) {
//        }

    }
}
