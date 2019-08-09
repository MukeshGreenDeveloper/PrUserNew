package bz.pei.driver.Sync;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import bz.pei.driver.App.MyApp;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Location.LocationUpdatesService;

/**
 * Service to handle sync requests.
 * <p>
 * <p>This service is invoked in response to Intents with action android.content.SyncAdapter, and
 * returns a Binder connection to SyncAdapter.
 * <p>
 * <p>For performance, only one sync adapter will be initialized within this application's context.
 * <p>
 * <p>Note: The SyncService itself is not notified when a new sync occurs. It's role is to
 * manage the lifecycle of our {@link SyncAdapter} and provide a handle to said SyncAdapter to the
 * OS on request.
 */
public class SyncService extends Service {
    private static final String TAG = "SyncService";

    public static final Object sSyncAdapterLock = new Object();
    public static SyncAdapter sSyncAdapter = null;
    public static SharedPreferences sharedPrefence = null;
//    public static Socket socket = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * Thread-safe constructor, creates static {@link SyncAdapter} instance.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(MyApp.getmContext(), true);
            }
        }
        if (!SyncUtils.checkIfSyncEnabled(MyApp.getmContext()))
            SyncUtils.CreateSyncAccount(MyApp.getmContext());
        if (!CommonUtils.isMyServiceRunning(MyApp.getmContext(), LocationUpdatesService.class)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                MyApp.getmContext().startForegroundService(new Intent(MyApp.getmContext(), LocationUpdatesService.class));
            else
                MyApp.getmContext().startService(new Intent(MyApp.getmContext(), LocationUpdatesService.class));
        }
        SyncUtils.syncSendData("abcd");
    }


    @Override
    /**
     * Logging-only destructor.
     */
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }

    /**
     * Return Binder handle for IPC communication with {@link SyncAdapter}.
     * <p>
     * <p>New sync requests will be sent directly to the SyncAdapter using this channel.
     *
     * @param intent Calling intent
     * @return Binder handle for {@link SyncAdapter}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }


}
