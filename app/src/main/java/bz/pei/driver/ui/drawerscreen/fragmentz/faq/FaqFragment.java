package bz.pei.driver.ui.drawerscreen.fragmentz.faq;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.retro.responsemodel.FAQModel;
import bz.pei.driver.databinding.FragmentFaqBinding;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 12/20/17.
 */

public class FaqFragment extends BaseFragment<FragmentFaqBinding, FaqFragmentViewModel> implements FaqNavigator {
    public static final String TAG = "FaqFragment";
    @Inject
    FaqFragmentViewModel oneViewModel;
    FragmentFaqBinding binding;
    @Inject
    SharedPrefence sharedPrefence;
    FAQAdapter adapter;

    public FaqFragment() {
        // Required empty public constructor
    }

    public static FaqFragment newInstance() {
        FaqFragment fragment = new FaqFragment();
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
        getBaseActivity().setTitle(R.string.text_faq);
    }

    @Override
    public FaqFragmentViewModel getViewModel() {
        return oneViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_faq;
    }

    @Override
    public void loadFaQItems(List<FAQModel> faqModelList) {
        if (faqModelList != null) {
            adapter = new FAQAdapter(faqModelList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getAttachedContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            binding.recyclerFaqItem.setLayoutManager(layoutManager);
            binding.recyclerFaqItem.setAdapter(adapter);
        }
    }

    @Override
    public Context getAttachedContext() {
        return getBaseActivity();
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public void logoutApp() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logoutApp();
    }


}
