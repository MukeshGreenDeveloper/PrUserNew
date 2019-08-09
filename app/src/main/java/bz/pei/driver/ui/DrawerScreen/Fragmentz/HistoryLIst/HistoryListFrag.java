package bz.pei.driver.ui.DrawerScreen.Fragmentz.HistoryLIst;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import bz.pei.driver.R;
import bz.pei.driver.Retro.ResponseModel.HistoryModel;
import bz.pei.driver.databinding.FragmentHistoryListBinding;
import bz.pei.driver.ui.Base.BaseFragment;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.HistoryLIst.adapter.ChildHistOnItemClick;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.HistoryLIst.adapter.HistoryAdapter;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


public class HistoryListFrag extends BaseFragment<FragmentHistoryListBinding, HistoryListViewModel> implements ChildHistOnItemClick, HistoryListNavigator {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "HistoryListFrag";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    @Inject
    HistoryListViewModel historyListViewModel;
    @Inject
    SharedPrefence sharedPrefence;

    @Inject
    @Named("HistoryList")
    LinearLayoutManager mLayoutManager;


    @Inject
    HistoryAdapter adapter;


    FragmentHistoryListBinding fragmentHistoryListBinding;

    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 1000;
    private int currentPage = PAGE_START;


    public HistoryListFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment HistoryListFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryListFrag newInstance(String param1) {
        HistoryListFrag fragment = new HistoryListFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
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
        fragmentHistoryListBinding = getViewDataBinding();
        historyListViewModel.setNavigator(this);
        getBaseActivity().setTitle(R.string.text_title_History);

        Setup();

    }

    @Override
    public HistoryListViewModel getViewModel() {
        return historyListViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_history_list;
    }


    private void Setup() {
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fragmentHistoryListBinding.recyclerViewHistory.setLayoutManager(mLayoutManager);
        fragmentHistoryListBinding.recyclerViewHistory.setItemAnimator(new DefaultItemAnimator());
        fragmentHistoryListBinding.recyclerViewHistory.setAdapter(adapter);

        fragmentHistoryListBinding.recyclerViewHistory.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                historyListViewModel.fetchData(currentPage);


            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        historyListViewModel.fetchData(currentPage);

    }

    @Override
    public void addItem(List<HistoryModel> histories) {
        adapter.addItem(histories);

        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }

    @Override
    public void Dostaff() {
        adapter.removeLoadingFooter();
        isLoading = false;
    }

    @Override
    public void MentionLastPage() {
        if (currentPage != 1) {
            adapter.removeLoadingFooter();
            isLoading = false;
            isLastPage = true;
        }
    }

    @Override
    public Context getAttachedcontext() {
        return getContext();
    }

    @Override
    public void onItemClick(HistoryModel model) {
        if (model != null)
            getBaseActivity().showHistoryActivity(model.id + "");
    }

    @Override
    public void logoutApp() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logoutApp();
    }


    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }
}
