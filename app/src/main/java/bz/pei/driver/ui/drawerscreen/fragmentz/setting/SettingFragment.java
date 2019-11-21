package bz.pei.driver.ui.drawerscreen.fragmentz.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.databinding.FragmentSettingBinding;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.ui.drawerscreen.fragmentz.MapFragment;
import bz.pei.driver.ui.Settings.SettingsNavigator;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * interface
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends BaseFragment<FragmentSettingBinding, SettingsViewModel> implements SettingsNavigator {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "SettingFragment";

    @Inject
    SettingsViewModel settingsViewModel;
    @Inject
    SharedPrefence sharedPrefence;
    public FragmentSettingBinding fragmentSettingBinding;
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
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
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        fragmentSettingBinding = getViewDataBinding();
        settingsViewModel.setNavigator(this);


        Setup();
    }

    private void Setup() {
        getBaseActivity().setTitle(R.string.txt_Setting);
        settingsViewModel.setUpData();
    }

    @Override
    public void logout() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logoutApp();
    }

    @Override
    public void changeShareState(int share_state) {
        if(getActivity()!=null &&getActivity().getSupportFragmentManager()!=null
                &&getActivity().getSupportFragmentManager().findFragmentByTag(MapFragment.TAG)!=null)
            ((MapFragment)getActivity().getSupportFragmentManager().findFragmentByTag(MapFragment.TAG)).setShareState(share_state);
    }



    @Override
    public SettingsViewModel getViewModel() {
        return settingsViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_setting;
    }
    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public Context getAttachedContext() {
        return getBaseActivity();
    }
    @Override
    public void refreshScreen() {
        Intent intent=new Intent(getActivity(), DrawerAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().finish();
        getActivity().startActivity(intent);

    }
}
