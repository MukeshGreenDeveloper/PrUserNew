package bz.pei.driver.ui.drawerscreen.fragmentz.sos;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.databinding.FragmentSosBinding;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SosFragment #newInstance} factory method to
 * create an instance of this fragment.
 */

/**/
public class SosFragment extends BaseFragment<FragmentSosBinding, SOSViewModel> implements SosNavigator {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "SOSFragment";

    private String mParam1;
    private String mParam2;
    @Inject
    SOSViewModel sosViewModel;
    @Inject
    SharedPrefence sharedPrefence;
    FragmentSosBinding fragmentSosBinding;

    public SosFragment() {
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
    public static SosFragment newInstance(String param1, String param2) {
        SosFragment fragment = new SosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentSosBinding = getViewDataBinding();
        sosViewModel.setNavigator(this);
        sosViewModel.setUpData();
        getBaseActivity().setTitle(R.string.txt_sos);
    }


    @Override
    public SOSViewModel getViewModel() {
        return sosViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sos;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }
    @Override
    public Context getAttachedContext() {
        return getBaseActivity();
    }
}
