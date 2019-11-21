package bz.pei.driver.ui.drawerscreen.fragmentz.managedocument;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.databinding.FragmentManageDocumentBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.drawerscreen.dialog.processdocumentupload.ProcessDocUploadDialog;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * interface
 * to handle interaction events.
 * Use the {@link ManageDocumentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageDocumentFragment extends BaseFragment<FragmentManageDocumentBinding, ManageDocumentViewModel> implements ManageDocumentNavigator {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "ManageDocumentFragment";

    @Inject
    ManageDocumentViewModel manageDocumentViewModel;
    @Inject
    SharedPrefence sharedPrefence;
    public FragmentManageDocumentBinding fragmentSettingBinding;
    public boolean documentUpdateDone = false;

    public ManageDocumentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageDocumentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageDocumentFragment newInstance(String param1, String param2) {
        ManageDocumentFragment fragment = new ManageDocumentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentSettingBinding = getViewDataBinding();
        manageDocumentViewModel.setNavigator(this);


        Setup();
    }

    public void Setup() {
        getBaseActivity().setTitle(R.string.text_manage_documents);
        manageDocumentViewModel.setUpData();
    }

    @Override
    public ManageDocumentViewModel getViewModel() {
        return manageDocumentViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_manage_document;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }


    @Override
    public BaseActivity getAttachedContext() {
        return getBaseActivity();
    }

    @Override
    public void openDocUploadDialog(RequestModel.Documents document_Item, boolean expiryAvailable, boolean numberAvailable) {
//        ProcessDocUploadDialog dialog = ProcessDocUploadDialog.newInstance("License",true,true);
        if (!TextUtils.isEmpty(document_Item.document))
            ProcessDocUploadDialog.newInstance(document_Item.document_name, document_Item.identify_number,
                    document_Item.document_ex_date, document_Item.document,
                    expiryAvailable, numberAvailable).show(getAttachedContext().getSupportFragmentManager(), ProcessDocUploadDialog.TAG);
        else
            ProcessDocUploadDialog.newInstance(document_Item.document_name,
                    expiryAvailable, numberAvailable).show(getAttachedContext().getSupportFragmentManager(), ProcessDocUploadDialog.TAG);
    }
    @Override
    public void logoutApp() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logoutApp();
    }


    @Override
    public void refreshToHome() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).resumeDriverState();
    }
}
