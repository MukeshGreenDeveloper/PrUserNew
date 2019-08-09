package bz.pei.driver.ui.DrawerScreen.Fragmentz.complaintone;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.databinding.FragmentComplaintBinding;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

/**
 * Created by root on 12/20/17.
 */

public class ComplaintFragment extends BaseFragment<FragmentComplaintBinding, ComplaintFragmentViewModel> implements ComplaintsNavigator {
    public static final String TAG = "ComplaintFragment";
    @Inject
    ComplaintFragmentViewModel oneViewModel;
    FragmentComplaintBinding binding;
    @Inject
    SharedPrefence sharedPrefence;

    public ComplaintFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComplaintFragment newInstance(String param1, String param2) {
        ComplaintFragment fragment = new ComplaintFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        oneViewModel.setNavigator(this);
        oneViewModel.setUpData();
        getBaseActivity().setTitle(R.string.text_complaint);
    }

    @Override
    public void logoutApp() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logoutApp();
    }

    @Override
    public ComplaintFragmentViewModel getViewModel() {
        return oneViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_complaint;
    }

    @Override
    public void backpressActivity() {
        getBaseActivity().onBackPressed();
    }

    @Override
    public Context getAttachedContext() {
        return getBaseActivity();
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }
}
