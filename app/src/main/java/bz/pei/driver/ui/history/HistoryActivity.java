package bz.pei.driver.ui.history;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.android.databinding.library.baseAdapters.BR;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import bz.pei.driver.R;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.databinding.ActivityHistoryBinding;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.history.adapter.AddChargeHistoryAdapter;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class HistoryActivity extends BaseActivity<ActivityHistoryBinding, HistoryViewModel> implements HistoryNavigator {
    @Inject
    HistoryViewModel viemodel;
    String requsetId;
    ActivityHistoryBinding binding;
    Toolbar toolbar;
    @Inject
    SharedPrefence sharedPrefence;
    AddChargeHistoryAdapter adapter;
    LinearLayoutManager mLayoutManager;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivty = getClass().getName();
        binding = getViewDataBinding();
        if (viemodel != null)
            viemodel.setNavigator(this);
        initSetup(savedInstanceState);

    }

    private void initSetup(Bundle savedInstanceState) {
        requsetId = getIntent().getStringExtra(Constants.IntentExtras.REQUEST_DATA);
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!CommonUtils.IsEmpty(requsetId))
            viemodel.getRequestDetails(requsetId);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(viemodel);
    }

    public void setRecyclerAdapter(String currency, List<RequestModel.AdditionalCharge> list) {
        Log.d("keys---", "historyActivity-" + list.size());
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new AddChargeHistoryAdapter(currency, (ArrayList<RequestModel.AdditionalCharge>) list, this);
        binding.recyclerAddChargesHistory.setLayoutManager(mLayoutManager);
        binding.recyclerAddChargesHistory.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public HistoryViewModel getViewModel() {
        return viemodel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModelss;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_history;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        viemodel.offSocket();
    }

    @Override
    public Context getAttachedContext() {
        return getApplicationContext();
    }

    @Override
    public BitmapDescriptor getMarkerIcon(int drawID) {
        return BitmapDescriptorFactory.fromBitmap(CommonUtils.getBitmapFromDrawable(HistoryActivity.this, drawID));
    }

}