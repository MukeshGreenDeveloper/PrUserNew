package bz.pei.driver.ui.signupscreen.fragmentz;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import javax.inject.Inject;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.databinding.FragmentSignupBinding;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.signupscreen.SignupActivity;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import static bz.pei.driver.utilz.Constants.Array_permissions;
import static bz.pei.driver.utilz.Constants.REQUEST_PERMISSION;

/*import com.android.databinding.library.baseAdapters.BR;*/

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**/
public class SignupFragment extends BaseFragment<FragmentSignupBinding, SignupFragmentViewModel> implements SignupFragmentNavigator {


    @Inject
    SignupFragmentViewModel signupFragmentViewModel;
    @Inject
    SharedPrefence sharedPrefence;
    public FragmentSignupBinding fragmentMapBinding;
    @Inject
    SignupActivity signupActivity;
    String CountryCode, countryShort;
    EditText et_fname;
    ScrollView scrollView;
    boolean isScroled = false;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (signupFragmentViewModel != null)
            signupFragmentViewModel.SetValue();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                mMessageReceiver, new IntentFilter(Constants.Broadcast_SignupFrgAction));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                vehicleTypeChangeReceiver, new IntentFilter(Constants.Broadcast_VehicleTypeChangeAction));
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentMapBinding = getViewDataBinding();
        signupFragmentViewModel.setNavigator(this);

        signupFragmentViewModel.SetValue();
        signupFragmentViewModel.getAreasForDropDown();
        scrollView = fragmentMapBinding.scrollViewRegistration;
        et_fname = fragmentMapBinding.editFnameSignup;
        et_fname.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isScroled)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            /* scrollView.fullScroll(View.FOCUS_DOWN);*/
                            scrollView.scrollTo(0, scrollView.getBottom());
                            isScroled = true;
                        }
                    }, 500);

                return false;
            }
        });
        CheckBox ck_TandC = (CheckBox) view.findViewById(R.id.ck_TandC);
        TextView img_tandc = (TextView) view.findViewById(R.id.img_tandc);

        img_tandc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), TermsandCons.class));
            }
        });
    }

    @Override
    public void alertSelectCameraGalary() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getAttachedcontext());
        builder1.setMessage(R.string.text_choose);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                R.string.text_camera,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        cameraIntent();
                    }
                });

        builder1.setNegativeButton(
                R.string.text_galary,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        galleryIntent();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void confromNxt() {
        Intent intent1 = new Intent(Constants.Broadcast_SignupAction);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent1);
    }

    @Override
    public int spinSelectionPosition() {
        return fragmentMapBinding.spinAreaSignup.getSelectedItemPosition();
    }

    @Override
    public String getCountryCode() {
        return CountryCode;
    }

    @Override
    public String getCountryShortName() {
        return countryShort;
    }

    @Override
    public void setCurrentCountryCode(String countryCode) {
        Constants.COUNTRY_CODE = countryCode;

    }


    @Override
    public SignupFragmentViewModel getViewModel() {
        return signupFragmentViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_signup;
    }


    @Override
    public Context getAttachedcontext() {
        return getContext();
    }

    @Override
    public void galleryIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !signupActivity.checkGranted(Array_permissions)) {
            signupActivity.requestPermissionsSafely(Array_permissions, REQUEST_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.text_title_select_file)), Constants.SELECT_FILE);
        }
    }

    @Override
    public void cameraIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !signupActivity.checkGranted(Array_permissions)) {
            signupActivity.requestPermissionsSafely(Array_permissions, REQUEST_PERMISSION);
        } else {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Constants.REQUEST_CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.SELECT_FILE) {
                signupFragmentViewModel.onSelectFromGalleryResult(data);
            } else if (requestCode == Constants.REQUEST_CAMERA) {
                signupFragmentViewModel.onCaptureImageResult(data);
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
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(vehicleTypeChangeReceiver);
        super.onDestroyView();

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (signupFragmentViewModel.validataion()) {
                signupFragmentViewModel.checkEmailPhnoAvail();
            }

        }
    };

    private BroadcastReceiver vehicleTypeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


        }
    };

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }
}
