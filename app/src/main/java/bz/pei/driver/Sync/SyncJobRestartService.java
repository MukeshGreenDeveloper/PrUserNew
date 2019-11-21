package bz.pei.driver.Sync;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import android.util.Log;

public class SyncJobRestartService extends JobIntentService {

    public static final int JOB_ID = 0x01;

    public static void enqueueWork(Context context, Intent work) {
        Log.d("keys", "hava1----------------------------------started");
        SyncUtils.CreateSyncAccount(context);
//        enqueueWork(context, SensorService.class, JOB_ID, work);
//       context.sta(new Intent(context,SensorService.class));
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // your code
    }

}