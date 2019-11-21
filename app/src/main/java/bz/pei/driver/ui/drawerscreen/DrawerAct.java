package bz.pei.driver.ui.drawerscreen;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import bz.pei.driver.App.MyApp;
import bz.pei.driver.BR;
import bz.pei.driver.R;
import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.Sync.SensorService;
import bz.pei.driver.Sync.SyncUtils;
import bz.pei.driver.databinding.ActivityDrawerBinding;
import bz.pei.driver.databinding.NavHeaderDrawerBinding;
import bz.pei.driver.ui.acceptreject.AcceptRejectActivity;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.drawerscreen.dialog.logoutdialog.LogoutDialogFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.faq.FaqFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.feedback.FeedbackFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.historylist.HistoryListFrag;
import bz.pei.driver.ui.drawerscreen.fragmentz.managedocument.ManageDocumentFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.MapFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.profile.ProfileFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.setting.SettingFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.sos.SosFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.supportpage.SupportFrag;
import bz.pei.driver.ui.drawerscreen.fragmentz.trip.TripFragment;
import bz.pei.driver.ui.drawerscreen.fragmentz.complaints.ComplaintFragment;
import bz.pei.driver.ui.login.loginviaotp.LoginOTPActivity;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.Location.LocationUpdatesService;
import bz.pei.driver.utilz.MyAccessibilityService;
import bz.pei.driver.utilz.SharedPrefence;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class DrawerAct extends BaseActivity<ActivityDrawerBinding, DrawerViewModel>
        implements NavigationView.OnNavigationItemSelectedListener, DrawerNavigator, HasSupportFragmentInjector, NewRequestListener {

    @Inject
    DrawerViewModel drawerViewModel;

    @Inject
    SharedPrefence sharedPrefence;
    private static Integer requestID = Constants.NO_REQUEST;

    @Inject
    Gson gson;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    ActivityDrawerBinding activityDrawerBinding;
    MapFragment fragment_map;
    private LocationUpdatesService locationService = null;
    // Tracks the bound state of the service.
    private boolean mBound = false, mLocationBound = false;
    private static final int ACCESSIBILITY_ENABLED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivty = getClass().getName();
        activityDrawerBinding = getViewDataBinding();
        drawerViewModel.setNavigator(this);
        setUp();
        drawerViewModel.getCurrentCountry();
        drawerViewModel.getRequestinProgress();
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
                mMessageReceiver, new IntentFilter(Constants.BroadcastsActions.RideFromAdmin));
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
                approveReceiver, new IntentFilter(Constants.BroadcastsActions.APPROVE_DECLINE));
        enableSyncAutoStart();

       /* if (isAccessibilitySettingsOn(this)) {
            Log.e("Access===", "acccess==" + "Accessbility");
        } else Log.e("Access===", "acccess==" + "Accessbility123");*/

    }

    // this is for triggering app when restart
    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + MyAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("AU", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == ACCESSIBILITY_ENABLED) {
            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    private void enableSyncAutoStart() {
      /*  try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            }
            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
*/
        if (!SyncUtils.checkIfSyncEnabled(MyApp.getmContext()))
            SyncUtils.CreateSyncAccount(DrawerAct.this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            CommonUtils.startPowerSaverIntent(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && CommonUtils.isPowerSaveMode(this)
                && sharedPrefence.getbooleanvalue(SharedPrefence.POWER_SAVER_DO_NOT_SHOW))
            openAlert(this, getString(R.string.text_power_saver_title),
                    getString(R.string.text_power_saver_enable_desc),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP)
                                    startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Intent intent = new Intent();
                                    String packageName = getPackageName();
                                    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                                    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                                        intent.setData(Uri.parse("package:" + packageName));
                                        startActivity(intent);
                                    }
                                }
                            } catch (Exception e) {
                                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                            }
                        }
                    });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationorGPSEnabled();
        if (mSensorService == null && mServiceIntent == null) {
            mSensorService = new SensorService(this);
            mServiceIntent = new Intent(this, mSensorService.getClass());
        }
        if (!CommonUtils.isMyServiceRunning(this, mSensorService.getClass())) {
            startService(mServiceIntent);
        }


    }

    @Override
    public DrawerViewModel getViewModel() {
        return drawerViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_drawer;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            boolean ischanged = false;
            for (Fragment f : getSupportFragmentManager().getFragments()) {
                switch ((String) f.getTag()) {
                    case MapFragment.TAG:
                    case TripFragment.TAG:
                    case FeedbackFragment.TAG:
//                        if (f.isVisible())
//                            finish();
                        setTitle(getString(R.string.app_title));
                        break;
                    case SettingFragment.TAG:
                    case ComplaintFragment.TAG:
                    case SosFragment.TAG:
                    case ProfileFragment.TAG:
                    case FaqFragment.TAG:
                    case HistoryListFrag.TAG:
                    case SupportFrag.TAG:
                        getSupportFragmentManager().beginTransaction().remove(f).commit();
                        ischanged = true;
                        break;
                    case ManageDocumentFragment.TAG:
                        if (((ManageDocumentFragment) f).documentUpdateDone) {
                            resumeDriverState();
                            return;
                        } else {
                            getSupportFragmentManager().beginTransaction().remove(f).commit();
                            ischanged = true;
                        }

                        break;
                }
            }
//            if (getSupportFragmentManager().getFragments().size() <= 0)
//                resumeDriverState();
            if (!ischanged)
                openExitDialog();
            else
                setTitle(getString(R.string.app_title));
//                super.onBackPressed();

        }
    }

    private void openExitDialog() {
        AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(this);
        builder1.setMessage(R.string.text_confirm_exit);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                R.string.text_confirm,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        builder1.setNegativeButton(
                getString(R.string.text_cancel).toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        android.support.v7.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    private void setUp() {

        mDrawer = activityDrawerBinding.drawerLayout;
        mToolbar = activityDrawerBinding.drawerToolbar.toolbar;
        mNavigationView = activityDrawerBinding.navView;


        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                mToolbar,
                R.string.open_drawer,
                R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        setupNavMenu();
        setupProfileDAta();
        activityDrawerBinding.drawerToolbar.layoutToggleStatusDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerViewModel.changeStatusString();
            }
        });
        /*drawerViewModel.setupProfile();
        drawerViewModel.buildGoogleApiClient();*/
    }

    public void setupProfileDAta() {
        if (drawerViewModel == null)
            return;
        drawerViewModel.setupProfile(DrawerAct.this);
        setTitle(getString(R.string.app_title));
    }

    public void toggleDriverStatus(boolean isActive) {
        activityDrawerBinding.drawerToolbar.switchToggleSatusDrawrer.setChecked(isActive);
        activityDrawerBinding.drawerToolbar.switchToggleSatusDrawrer.setText(isActive ? getString(R.string.text_online) : getString(R.string.text_offline));
    }

    public void disableToggleStatusIcon(boolean show) {
        activityDrawerBinding.drawerToolbar.switchToggleSatusDrawrer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (id == R.id.nav_Home) {
                    drawerViewModel.getRequestinProgress();
                    setupProfileDAta();
                } else if (id == R.id.nav_profile) {
                    if (sharedPrefence.getIntvalue(SharedPrefence.REQUEST_ID) == Constants.NO_REQUEST)
                        showProfileFragment();
                } else if (id == R.id.nav_history) {
                    showHistoryFragment();
                } else if (id == R.id.nav_manage_doc) {
                    if (sharedPrefence.getIntvalue(SharedPrefence.REQUEST_ID) == Constants.NO_REQUEST)
                        showManageDocumentFragment();
                } else if (id == R.id.nav_complaints) {
                    showComplaintFragment();
                } else if (id == R.id.nav_sos) {
                    showSOSFragment();
                } else if (id == R.id.nav_share) {
                    shareContent("");
                } else if (id == R.id.nav_settings) {
                    if (sharedPrefence.getIntvalue(SharedPrefence.REQUEST_ID) == Constants.NO_REQUEST)
                        showSettingFragment();
                } else if (id == R.id.nav_faq) {
                    showFaqFragment();
                } else if (id == R.id.nav_logout) {
                    showLogoutDialog();
                }
            }
        }, 200);


        return true;
    }

    private void showHistoryFragment() {
        HistoryListFrag fragment = null;
        if (getSupportFragmentManager().findFragmentByTag(HistoryListFrag.TAG) != null)
            fragment = (HistoryListFrag) getSupportFragmentManager().findFragmentByTag(HistoryListFrag.TAG);
        if (fragment != null) {
            if (!fragment.isVisible())
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .replace(R.id.Container, HistoryListFrag.newInstance(HistoryListFrag.TAG), HistoryListFrag.TAG)
                        .commit();
            else {
                getSupportFragmentManager().beginTransaction().remove(fragment);
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .add(R.id.Container, HistoryListFrag.newInstance(HistoryListFrag.TAG), HistoryListFrag.TAG)
                        .commit();
            }
        } else
            getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.Container, HistoryListFrag.newInstance(HistoryListFrag.TAG), HistoryListFrag.TAG)
                    .commit();
    }


    private void showFaqFragment() {
        FaqFragment fragment = null;
        if (getSupportFragmentManager().findFragmentByTag(FaqFragment.TAG) != null)
            fragment = (FaqFragment) getSupportFragmentManager().findFragmentByTag(FaqFragment.TAG);
        if (fragment != null) {
            if (!fragment.isVisible())
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .replace(R.id.Container, FaqFragment.newInstance(), FaqFragment.TAG)
                        .commit();
            else {
                getSupportFragmentManager().beginTransaction().remove(fragment);
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .add(R.id.Container, FaqFragment.newInstance(), FaqFragment.TAG)
                        .commit();
            }
        } else
            getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.Container, FaqFragment.newInstance(), FaqFragment.TAG)
                    .commit();
    }

    private void showLogoutDialog() {
        LogoutDialogFragment fragment = LogoutDialogFragment.newInstance(LogoutDialogFragment.TAG);
        fragment.show(getSupportFragmentManager(), LogoutDialogFragment.TAG);
    }

    public void initializeLogout() {
        if (drawerViewModel != null)
            drawerViewModel.logoutDriver();

    }

    private void showProfileFragment() {
        ProfileFragment fragment = null;
        if (getSupportFragmentManager().findFragmentByTag(ProfileFragment.TAG) != null)
            fragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(ProfileFragment.TAG);
        if (fragment != null) {
            if (!fragment.isVisible())
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .replace(R.id.Container, ProfileFragment.newInstance(ProfileFragment.TAG), ProfileFragment.TAG)
                        .commit();
            else {
                getSupportFragmentManager().beginTransaction().remove(fragment);
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .add(R.id.Container, ProfileFragment.newInstance(ProfileFragment.TAG), ProfileFragment.TAG)
                        .commit();
            }
        } else
            getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.Container, ProfileFragment.newInstance(ProfileFragment.TAG), ProfileFragment.TAG)
                    .commit();
    }


    @Override
    public void logoutApp() {
        sharedPrefence.savevalue(SharedPrefence.DRIVERERDETAILS, null);
        sharedPrefence.saveIntValue(SharedPrefence.REQUEST_ID, Constants.NO_REQUEST);
        sharedPrefence.saveIntValue(SharedPrefence.USER_ID, Constants.NO_ID);
        sharedPrefence.saveIntValue(SharedPrefence.IS_SHARE, Constants.NO_ID);
        sharedPrefence.saveIntValue(SharedPrefence.TRIP_START, Constants.NO_ID);
        sharedPrefence.savevalue(SharedPrefence.DRIVER_ID, "");
        startActivity(new Intent(DrawerAct.this, LoginOTPActivity.class));
        finish();
    }

    @Override
    public void passtoFragmentDriverOfflineOnline(int isActive) {
        toggleDriverStatus(isActive == 1);
        fragment_map = (MapFragment) getSupportFragmentManager().findFragmentByTag(MapFragment.TAG);
        if (fragment_map != null)
            if (fragment_map.isVisible()) {
                fragment_map.setOfflineOnline(isActive);
            }
    }

    @Override
    public void ShareStatus(int isShare) {
        Toast.makeText(getBaseContext(), "Share : " + isShare, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void openApprovalDialog(int driverSate) {
        showApprovalDialog(driverSate);
    }

    @Override
    public BaseActivity getAttachedBaseActivity() {
        return this;
    }

    @Override
    public void openFeedbackFragment(RequestModel model) {
        showFeedbackFragment(model);
    }

    @Override
    public void openTripFragment(RequestModel model) {
        showTripFragment(model);
    }

    private void setupNavMenu() {
        NavHeaderDrawerBinding navHeaderDrawerBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.nav_header_drawer, activityDrawerBinding.navView, false);
        activityDrawerBinding.navView.addHeaderView(navHeaderDrawerBinding.getRoot());
        navHeaderDrawerBinding.setViewModel(drawerViewModel);
        mNavigationView.setNavigationItemSelectedListener(this);
        if (activityDrawerBinding.navView.getMenu() != null) {
            activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Home).setTitle(getString(R.string.txt_Home));
            activityDrawerBinding.navView.getMenu().findItem(R.id.nav_profile).setTitle(getString(R.string.text_profile));
            activityDrawerBinding.navView.getMenu().findItem(R.id.nav_history).setTitle(getString(R.string.text_title_History));
            activityDrawerBinding.navView.getMenu().findItem(R.id.nav_manage_doc).setTitle(getString(R.string.text_manage_documents));
            activityDrawerBinding.navView.getMenu().findItem(R.id.nav_complaints).setTitle(getString(R.string.text_complaint));
            activityDrawerBinding.navView.getMenu().findItem(R.id.nav_sos).setTitle(getString(R.string.txt_sos));
            activityDrawerBinding.navView.getMenu().findItem(R.id.nav_settings).setTitle(getString(R.string.txt_Setting));
            activityDrawerBinding.navView.getMenu().findItem(R.id.nav_share).setTitle(getString(R.string.text_share));
            activityDrawerBinding.navView.getMenu().findItem(R.id.nav_faq).setTitle(getString(R.string.text_faq));
            activityDrawerBinding.navView.getMenu().findItem(R.id.nav_logout).setTitle(getString(R.string.txt_logout));
        }
        showMapFragment();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    private void openSupportFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, SupportFrag.newInstance("", ""), SupportFrag.TAG)
                .commit();
    }

    public void showSettingFragment() {
        SettingFragment fragment = null;
        if (getSupportFragmentManager().findFragmentByTag(SettingFragment.TAG) != null)
            fragment = (SettingFragment) getSupportFragmentManager().findFragmentByTag(SettingFragment.TAG);
        if (fragment != null) {
            if (!fragment.isVisible())
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .replace(R.id.Container, SettingFragment.newInstance("", ""), SettingFragment.TAG)
                        .commitAllowingStateLoss();//commit();
            else {
                getSupportFragmentManager().beginTransaction().remove(fragment);
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .add(R.id.Container, SettingFragment.newInstance("", ""), SettingFragment.TAG)
                        .commitAllowingStateLoss();
            }
        } else
            getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.Container, SettingFragment.newInstance("", ""), SettingFragment.TAG)
                    .commitAllowingStateLoss();//commit();
    }

    public void showComplaintFragment() {
        ComplaintFragment fragment = null;
        if (getSupportFragmentManager().findFragmentByTag(ComplaintFragment.TAG) != null)
            fragment = (ComplaintFragment) getSupportFragmentManager().findFragmentByTag(ComplaintFragment.TAG);
        if (fragment != null) {
            if (!fragment.isVisible())
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .replace(R.id.Container, ComplaintFragment.newInstance("", ""), ComplaintFragment.TAG)
                        .commit();
            else {
                getSupportFragmentManager().beginTransaction().remove(fragment);
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .add(R.id.Container, ComplaintFragment.newInstance("", ""), ComplaintFragment.TAG)
                        .commit();
            }

        } else
            getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.Container, ComplaintFragment.newInstance("", ""), ComplaintFragment.TAG)
                    .commit();
    }

    public void showSOSFragment() {
//
        SosFragment fragment = null;
        if (getSupportFragmentManager().findFragmentByTag(SosFragment.TAG) != null)
            fragment = (SosFragment) getSupportFragmentManager().findFragmentByTag(SosFragment.TAG);
        if (fragment != null) {
            if (!fragment.isVisible())
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .replace(R.id.Container, SosFragment.newInstance("", ""), SosFragment.TAG)
                        .commit();
            else {
                getSupportFragmentManager().beginTransaction().remove(fragment);
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .add(R.id.Container, SosFragment.newInstance("", ""), SosFragment.TAG)
                        .commit();
            }
        } else
            getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.Container, SosFragment.newInstance("", ""), SosFragment.TAG)
                    .commit();
    }

    @Override
    public void finished() {

    }

    @Override
    public void openAcceptReject(RequestModel model, String requestData) {
        if (sharedPrefence.getIntvalue(SharedPrefence.LAST_REQUEST_ID) != model.request.id &&
                sharedPrefence.getIntvalue(SharedPrefence.LAST_REQUEST_ID) < model.request.id &&
                !MyApp.isIsAcceptRejectActivityVisible()) {
            Intent acceptRejectIntent = new Intent(this, AcceptRejectActivity.class);
            acceptRejectIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            acceptRejectIntent.putExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA, requestData);
            startActivity(acceptRejectIntent);
            sharedPrefence.saveIntValue(SharedPrefence.LAST_REQUEST_ID, model.request.id);
        }
    }


    @Override
    public void openMapFragment(int is_active) {
        showMapFragment(is_active);
        disableToggleStatusIcon(true);
    }

    @Override
    public void openShareFragment() {
        showShareFragment();
    }


    public void emitLocationDetails(String id, String lat, String lng, String bearing) {
        drawerViewModel.sendLocation(id, lat, lng, bearing);
    }

    public void emitTripLocationDetails(String jsonObject) {
        drawerViewModel.sendTripLocation(jsonObject);
    }

    Intent mServiceIntent;
    private SensorService mSensorService;

    public void startSocket() {
        drawerViewModel.initiateSocket();
        SyncUtils.CreateSyncAccount(this);
        mSensorService = new SensorService(this);
        mServiceIntent = new Intent(this, mSensorService.getClass());
        if (!CommonUtils.isMyServiceRunning(this, mSensorService.getClass())) {
            startService(mServiceIntent);
        }
    }


    public void stopSocket() {
//        SyncUtils.syncCancel();
    }


    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, LocationUpdatesService.class), mLocationServiceConnection,
                Context.BIND_AUTO_CREATE);
        IntentFilter intentLocationFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        intentLocationFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        IntentFilter intentInternetFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentInternetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(locationReceiver, intentLocationFilter);
        registerReceiver(internetReceiver, intentInternetFilter);
        IntentFilter intentLocation = new IntentFilter(Constants.BroadcastsActions.LOCATION_UPDATING_SERVICE);
        intentLocation.addAction(Constants.BroadcastsActions.LOCATION_UPDATING_SERVICE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationReceiver, intentLocation);
    }

    @Override
    protected void onStop() {
        if (mLocationBound) {
            unbindService(mLocationServiceConnection);
            mLocationBound = false;
        }
        super.onStop();
        unregisterReceiver(locationReceiver);
        unregisterReceiver(internetReceiver);
    }

    public void resumeDriverState() {
        if (drawerViewModel != null)
            drawerViewModel.getRequestinProgress();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationService = new LocationUpdatesService();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
                mMessageReceiver);
        LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
                approveReceiver);
        LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
                mLocationReceiver);
        stopService(mServiceIntent);
        super.onDestroy();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            resumeDriverState();
        }
    };
    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (drawerViewModel != null && intent != null
                    && intent.getStringExtra(Constants.IntentExtras.LOCATION_ID) != null
                    && intent.getStringExtra(Constants.IntentExtras.LOCATION_LAT) != null
                    && intent.getStringExtra(Constants.IntentExtras.LOCATION_LNG) != null
                    && intent.getStringExtra(Constants.IntentExtras.LOCATION_BEARING) != null)
                if (locatoinReceiverListener != null && sharedPrefence.getIntvalue(SharedPrefence.REQUEST_ID) != Constants.NO_REQUEST)
                    locatoinReceiverListener.passLatlng(intent.getStringExtra(Constants.IntentExtras.LOCATION_LAT),
                            intent.getStringExtra(Constants.IntentExtras.LOCATION_LNG),
                            intent.getStringExtra(Constants.IntentExtras.LOCATION_BEARING),intent.getStringExtra(Constants.IntentExtras.LOCATION_ID));
                else
                    drawerViewModel.sendLocation(intent.getStringExtra(Constants.IntentExtras.LOCATION_ID),
                            intent.getStringExtra(Constants.IntentExtras.LOCATION_LAT),
                            intent.getStringExtra(Constants.IntentExtras.LOCATION_LNG),
                            intent.getStringExtra(Constants.IntentExtras.LOCATION_BEARING));

        }
    };
    private BroadcastReceiver approveReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String intentDAta = intent.getStringExtra(Constants.BroadcastsActions.APPROVE_DECLINE);
                if (!CommonUtils.IsEmpty(intentDAta)) {
                    Log.d("keys", "ShowingApproveMessage:" + intentDAta);
                    RequestModel model = CommonUtils.getSingleObject(intentDAta, RequestModel.class);
                    if (model != null)
                        if (model.successMessage != null && model.successMessage.equalsIgnoreCase("Approved")) {
                            if (model.is_approved != null)
                                if (!model.is_approved)
                                    showApprovalDialog(4);
                                else
                                    resumeDriverState();
                        }
                }
            }
        }
    };

    public void removeLocationUpdate() {
        if (locationService != null) {
            locationService.stopSelf();
        }
    }

    private final ServiceConnection mLocationServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            locationService = binder.getService();
            mLocationBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationService = null;
            mLocationBound = false;
        }
    };

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public void sendCancelBroadcast(String cancelDAta) {
        Intent intentBroadcasts = new Intent();
        intentBroadcasts.setAction(Constants.BroadcastsActions.CANCEL_RECEIVER);
        intentBroadcasts.putExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA, cancelDAta);
        LocalBroadcastManager.getInstance(DrawerAct.this).sendBroadcast(intentBroadcasts);
    }

    public interface LocationReceiver {
        public void passLatlng(String lat, String lng, String bearing, String driverId);
    }
}
