package bz.pei.driver.ui.DrawerScreen.Fragmentz.SupportPage;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import bz.pei.driver.R;
import bz.pei.driver.databinding.FragmentSupportPageBinding;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;


public class SupportFrag extends BaseFragment<FragmentSupportPageBinding, SupportFragViewModel> implements SupportFragNavigator {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "SupportFrag";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentSupportPageBinding supportPageBinding;

    @Inject
    SupportFragViewModel supportFragViewModel;

    public SupportFrag() {
        // Required empty public constructor
    }


    public static SupportFrag newInstance(String param1, String param2) {
        SupportFrag fragment = new SupportFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        supportPageBinding = getViewDataBinding();
        supportFragViewModel.setNavigator(this);
        getBaseActivity().setTitle(R.string.txt_support);

//        supportFragViewModel.GetRefferalDetails();
    }


    @Override
    public SupportFragViewModel getViewModel() {
        return supportFragViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_support_page;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return supportFragViewModel.sharedPrefence;
    }

    @Override
    public Context getAttachedContext() {
        return getContext();
    }


    @Override
    public void logout() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logoutApp();
    }
}
