package bz.pei.driver.FCM;

import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import bz.pei.driver.utilz.SharedPrefence;

/**
 * Created by root on 12/15/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]

    SharedPrefence sharedPrefence;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "RefreshToken: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
//        **********************
        sharedPrefence = new SharedPrefence(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit());
        if (sharedPrefence != null)
            sharedPrefence.savevalue(SharedPrefence.DEVICE_TOKEN, refreshedToken);
    }
}
