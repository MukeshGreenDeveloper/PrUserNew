package bz.pei.driver.ui.DrawerScreen.Fragmentz.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.databinding.ActivityProfileBinding;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

import static bz.pei.driver.utilz.Constants.Array_permissions;
import static bz.pei.driver.utilz.Constants.REQUEST_PERMISSION;

public class ProfileFragment extends BaseFragment<ActivityProfileBinding, ProfileViewModel> implements ProfileNavigator {
    @Inject
    ProfileViewModel profileViewModel;
    @Inject
    SharedPrefence sharedPrefence;
    ActivityProfileBinding activityProfileBinding;
    public static final String TAG = "ProfileFragment";
    public static String ARG_PARAM1 = "param";
    public boolean isScroled = false;

    public static ProfileFragment newInstance(String param) {

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activityProfileBinding = getViewDataBinding();
        profileViewModel.setNavigator(this);
        activityProfileBinding.editFnameProfile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isScroled)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            /* scrollView.fullScroll(View.FOCUS_DOWN);*/
                            activityProfileBinding.scrollProfile.scrollTo(0, activityProfileBinding.scrollProfile.getBottom());
                            isScroled = true;
                        }
                    }, 500);

                return false;
            }
        });
        initSetup();
    }

    private void initSetup() {
        getActivity().setTitle(R.string.text_profile);
//        profileViewModel.setDriverDetails(getContext());
        profileViewModel.getDriverProfile();

    }


    @Override
    public void alertSelectCameraGalary() {
        android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(getBaseActivity());
        builder1.setMessage("ChooseOne");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        cameraIntent();
                    }
                });

        builder1.setNegativeButton(
                "Galary",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        galleryIntent();
                    }
                });

        android.support.v7.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void cameraIntent() {
        if(getBaseActivity()!=null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !getBaseActivity().checkGranted(Array_permissions)) {
            getBaseActivity().requestPermissionsSafely(Array_permissions, REQUEST_PERMISSION);
        } else {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, Constants.REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void refreshDrawerActivity() {
        ((DrawerAct) getContext()).setupProfileDAta();
    }

    @Override
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.SELECT_FILE) {
                profileViewModel.onSelectFromGalleryResult(data);
            } else if (requestCode == Constants.REQUEST_CAMERA) {
                profileViewModel.onCaptureImageResult(data);
            }
        }
    }

    @Override
    public void logoutApp() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logoutApp();
    }


    @Override
    public ProfileViewModel getViewModel() {
        return profileViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public Context getAttachedContext() {
        return getContext();
    }
}