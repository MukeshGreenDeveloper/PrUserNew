package bz.pei.driver.ui.SignupScreen.Fragmentz;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;

import bz.pei.driver.BR;
import bz.pei.driver.Pojos.RegisterationModel;
import bz.pei.driver.R;
import bz.pei.driver.databinding.DialogDocumentUploadBinding;
import bz.pei.driver.databinding.FragmentDocuploadBinding;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.SignupScreen.SignupActivity;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

/*import com.android.databinding.library.baseAdapters.BR;*/

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DocUploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**/
public class DocUploadFragment extends BaseFragment<FragmentDocuploadBinding, DocUploadViewModel> implements DocUploadNavigator {


    @Inject
    DocUploadViewModel docUploadViewModel;
    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    SignupActivity signupActivity;
    public FragmentDocuploadBinding fragmentMapBinding;

    public DocUploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DocUploadFragment newInstance() {
        DocUploadFragment fragment = new DocUploadFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                mMessageReceiver, new IntentFilter(Constants.Broadcast_DocmentFrgAction));
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentMapBinding = getViewDataBinding();
        docUploadViewModel.setNavigator(this);
        if (!TextUtils.isEmpty(RegisterationModel.getInstance().license_desc))
            docUploadViewModel.licence_title = RegisterationModel.getInstance().license_desc;
        if (!TextUtils.isEmpty(RegisterationModel.getInstance().license_exp))
            docUploadViewModel.licence_expdate = CommonUtils.reverseFormatDate(RegisterationModel.getInstance().license_exp);
        if (!TextUtils.isEmpty(RegisterationModel.getInstance().license_photo)) {
            docUploadViewModel.bitmap_licence.set(RegisterationModel.getInstance().license_photo);
            docUploadViewModel.is_licenceOK.set(true);
        }
        if (!TextUtils.isEmpty(RegisterationModel.getInstance().insurance_desc))
            docUploadViewModel.insurance_title = RegisterationModel.getInstance().insurance_desc;
        if (!TextUtils.isEmpty(RegisterationModel.getInstance().insurance_exp))
            docUploadViewModel.insurance_expdate = CommonUtils.reverseFormatDate(RegisterationModel.getInstance().insurance_exp);
        if (!TextUtils.isEmpty(RegisterationModel.getInstance().insurance_photo)) {
            docUploadViewModel.bitmap_insurance.set(RegisterationModel.getInstance().insurance_photo);
            docUploadViewModel.is_insuranceOK.set(true);
        }

        if (!TextUtils.isEmpty(RegisterationModel.getInstance().rcBook_desc))
            docUploadViewModel.rcbook_title = RegisterationModel.getInstance().rcBook_desc;
        if (!TextUtils.isEmpty(RegisterationModel.getInstance().rcBook_exp))
            docUploadViewModel.rcbook_expdate = CommonUtils.reverseFormatDate(RegisterationModel.getInstance().rcBook_exp);
        if (!TextUtils.isEmpty(RegisterationModel.getInstance().rcBook_photo)) {
            docUploadViewModel.bitmap_rcbook.set(RegisterationModel.getInstance().rcBook_photo);
            docUploadViewModel.is_rcbookOK.set(true);
        }

        /*public ObservableField<String> bitmap_licence = new ObservableField<>("");
        public ObservableField<Boolean> is_licenceOK = new ObservableField<>(false);
        public String licence_title;//= new ObservableField<>();
        public String licence_expdate;*/
        /* docDialogViewModel.title_doc.set(docUploadViewModel.licence_title);
                docDialogViewModel.exp_doc.set(docUploadViewModel.licence_expdate);
                docDialogViewModel.bitmap = docUploadViewModel.is_licenceOK.get() ? imgURL : new ObservableField<>("");
                break;
            case Constants.INSURANCE_DOC:
                docDialogViewModel.title_doc.set(docUploadViewModel.insurance_title);
                docDialogViewModel.exp_doc.set(docUploadViewModel.insurance_expdate);
                docDialogViewModel.bitmap = docUploadViewModel.is_insuranceOK.get() ? imgURL : new ObservableField<>("");
                break;
            case Constants.RCBOOK_DOC:
                docDialogViewModel.title_doc.set(docUploadViewModel.rcbook_title);
                docDialogViewModel.exp_doc.set(docUploadViewModel.rcbook_expdate);
                docDialogViewModel.bitmap = docUploadViewModel.is_rcbookOK.get() ? imgURL : new ObservableField<>("");
                break;*/

    }


    @Override
    public Context getAttachedcontext() {
        return getContext();
    }


    @Override
    public DocUploadViewModel getViewModel() {
        return docUploadViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_docupload;
    }


    @Override
    public void openMainActivity() {

    }

    Dialog dialog_docUpload;
    DialogDocumentUploadBinding binding;
    //    String imgLicence, imgInsurance, imgRcBook;
    @Inject
    DocDialogViewModel docDialogViewModel;
    private String current = "";
    DatePickerDialog datePickerDialog;
    Calendar cal = Calendar.getInstance();

    @Override
    public void openDocUploadDialog(ObservableField<String> imgURL) {


        dialog_docUpload = new Dialog(getActivity());
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_document_upload,
                null, false);

        switch (docUploadViewModel.count) {
            case Constants.LICENSE_DOC:
                docDialogViewModel.title_doc.set(docUploadViewModel.licence_title);
                docDialogViewModel.exp_doc.set(docUploadViewModel.licence_expdate);
                docDialogViewModel.bitmap = docUploadViewModel.is_licenceOK.get() ? imgURL : new ObservableField<>("");
                break;
            case Constants.INSURANCE_DOC:
                docDialogViewModel.title_doc.set(docUploadViewModel.insurance_title);
                docDialogViewModel.exp_doc.set(docUploadViewModel.insurance_expdate);
                docDialogViewModel.bitmap = docUploadViewModel.is_insuranceOK.get() ? imgURL : new ObservableField<>("");
                break;
            case Constants.RCBOOK_DOC:
                docDialogViewModel.title_doc.set(docUploadViewModel.rcbook_title);
                docDialogViewModel.exp_doc.set(docUploadViewModel.rcbook_expdate);
                docDialogViewModel.bitmap = docUploadViewModel.is_rcbookOK.get() ? imgURL : new ObservableField<>("");
                break;
        }
        dialog_docUpload.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_docUpload.setContentView(binding.getRoot());
        binding.setViewModel(docDialogViewModel);
        binding.layoutCameraDocUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraIntent();
            }
        });
        binding.layoutGalaryDocUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryIntent();
            }
        });
        binding.btnSubmitDocupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = null;
                if (TextUtils.isEmpty(docDialogViewModel.bitmap.get())) {
                    msg = getString(R.string.text_error_doc_pic_empty);
                } else if (TextUtils.isEmpty(docDialogViewModel.title_doc.get())) {
                    msg = getString(R.string.text_error_doc_title_empty);
                } else if (TextUtils.isEmpty(docDialogViewModel.exp_doc.get())) {
                    msg = getString(R.string.text_error_doc_expiry_empty);
                } else if (!CommonUtils.validateDate(docDialogViewModel.exp_doc.get())) {
                    msg = getString(R.string.text_error_doc_expiry_notvalid);
                } else if (CommonUtils.formatDate(docDialogViewModel.exp_doc.get()) == null) {
                    msg = getString(R.string.text_error_doc_expiry_notvalid);
                }
                if (msg != null)
                    showMessage(msg);
                else {
                    dialog_docUpload.dismiss();
                    switch (docUploadViewModel.count) {
                        case Constants.LICENSE_DOC:
                            docUploadViewModel.licence_title = docDialogViewModel.title_doc.get();
                            docUploadViewModel.licence_expdate = docDialogViewModel.exp_doc.get();
                            docUploadViewModel.is_licenceOK.set(true);
                            RegisterationModel.getInstance().license_photo = docDialogViewModel.bitmap.get();
                            RegisterationModel.getInstance().license_desc = docDialogViewModel.title_doc.get();
                            RegisterationModel.getInstance().license_exp = CommonUtils.formatDate(docDialogViewModel.exp_doc.get());

                            break;
                        case Constants.INSURANCE_DOC:
                            docUploadViewModel.insurance_title = docDialogViewModel.title_doc.get();
                            docUploadViewModel.insurance_expdate = docDialogViewModel.exp_doc.get();
                            docUploadViewModel.is_insuranceOK.set(true);
                            RegisterationModel.getInstance().insurance_photo = docDialogViewModel.bitmap.get();
                            RegisterationModel.getInstance().insurance_desc = docDialogViewModel.title_doc.get();
                            RegisterationModel.getInstance().insurance_exp = CommonUtils.formatDate(docDialogViewModel.exp_doc.get());

                            break;
                        case Constants.RCBOOK_DOC:
                            docUploadViewModel.rcbook_title = docDialogViewModel.title_doc.get();
                            docUploadViewModel.rcbook_expdate = docDialogViewModel.exp_doc.get();
                            docUploadViewModel.is_rcbookOK.set(true);
                            RegisterationModel.getInstance().rcBook_photo = docDialogViewModel.bitmap.get();
                            RegisterationModel.getInstance().rcBook_desc = docDialogViewModel.title_doc.get();
                            RegisterationModel.getInstance().rcBook_exp = CommonUtils.formatDate(docDialogViewModel.exp_doc.get());
                            break;
                    }
                }
            }
        });
        /*binding.editExpiryDocupload.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
        binding.imgExpiryDocupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        dialog_docUpload.show();
    }

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
            if (docDialogViewModel != null)
                if (docDialogViewModel.exp_doc != null)
                    docDialogViewModel.exp_doc.set(dayOfMonth + "-" + (month + 1) + "-" + year);
        }
    };
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (docUploadViewModel.validateprepareApiCall()) {
                Intent intent1 = new Intent(Constants.Broadcast_SignupAction);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent1);
            }
        }
    };


    @Override
    public void setupImage(String url) {
        if (binding.imgPicDocupload != null && url != null)
//            Glide.with(this).load(url).apply(RequestOptions.centerCropTransform()
//                    .error(R.drawable.ic_img_docupload)
//                    .placeholder(R.drawable.ic_img_docupload))
//                    .into(binding.imgPicDocupload);
            docDialogViewModel.bitmap.set(url);
    }

    private void galleryIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !signupActivity.checkGranted(Constants.Array_permissions)) {
            signupActivity.requestPermissionsSafely(Constants.Array_permissions, Constants.REQUEST_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.text_title_select_file)), Constants.SELECT_FILE);
        }
    }

    private void cameraIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !signupActivity.checkGranted(Constants.Array_permissions)) {
            signupActivity.requestPermissionsSafely(Constants.Array_permissions, Constants.REQUEST_PERMISSION);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Constants.REQUEST_CAMERA);
        }
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

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(
                mMessageReceiver);
        super.onDestroyView();

    }
    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }
}
