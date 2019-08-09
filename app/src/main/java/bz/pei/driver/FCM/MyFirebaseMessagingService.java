package bz.pei.driver.FCM;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import bz.pei.driver.App.MyApp;
import bz.pei.driver.R;
import bz.pei.driver.Retro.ResponseModel.RequestModel;
import bz.pei.driver.ui.AcceptReject.AcceptRejectActivity;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;
import bz.pei.driver.utilz.CommonUtils;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.List;

/**
 * Created by root on 12/15/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
                if (remoteMessage.getData().get("message") != null) {
                    RequestModel model = CommonUtils.getSingleObject(remoteMessage.getData().get("message"), RequestModel.class);
                    if (model != null)
                        if (model.success != null)
                            if (model.success) {
                                if (model.is_cancelled == 1) {
                                    sendCancelNotification(model.message, remoteMessage.getData().get("message"));
                                } else {
                                    if (model.successMessage.equalsIgnoreCase("Create_Request_successfully")) {

                                        displayNotification((getString(R.string.text_new_request)), false, remoteMessage.getData().get("message"));
                                        if (PreferenceManager.getDefaultSharedPreferences(this).getInt(SharedPrefence.LAST_REQUEST_ID, Constants.NO_REQUEST) == Constants.NO_REQUEST
                                                || (PreferenceManager.getDefaultSharedPreferences(this).getInt(SharedPrefence.LAST_REQUEST_ID, Constants.NO_REQUEST) != model.request.id
                                                && PreferenceManager.getDefaultSharedPreferences(this).getInt(SharedPrefence.LAST_REQUEST_ID, Constants.NO_REQUEST) < model.request.id))
                                            if (!BaseActivity.isRunning(this)) {
                                                Intent acceptRejectIntent = new Intent(this, AcceptRejectActivity.class);
                                                acceptRejectIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                acceptRejectIntent.putExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA, remoteMessage.getData().get("message"));
                                                startActivity(acceptRejectIntent);
                                            } else {
                                                if (!MyApp.isIsAcceptRejectActivityVisible())
                                                    if (BaseActivity.isAppIsInBackground(this)) {
                                                        Intent acceptRejectIntent = new Intent(this, AcceptRejectActivity.class);
                                                        acceptRejectIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        acceptRejectIntent.putExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA, remoteMessage.getData().get("message"));
                                                        startActivity(acceptRejectIntent);
                                                    } else {
                                                        Intent intentBroadcast = new Intent();
                                                        intentBroadcast.setAction(Constants.BroadcastsActions.NEW_REQUEST);
                                                        intentBroadcast.putExtra(Constants.BroadcastsActions.NEW_REQUEST, remoteMessage.getData().get("message"));
                                                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
                                                    }
                                            }
                                    } else if (model.successMessage.equalsIgnoreCase("Approved")) {

                                        if (model.is_approved != null)
                                            sendNotificationApprovedDeclined(model.is_approved ? getString(R.string.text_approved) : getString(R.string.text_declined), remoteMessage.getData().get("message"));
                                        else
                                            sendNotificationApprovedDeclined(model.successMessage, remoteMessage.getData().get("message"));
                                    } else if (model.successMessage.equalsIgnoreCase("dispatch_user_successfully")) {
                                        displayNotification("You have been assinged ride by admin."/*, remoteMessage.getData().get("message")*/);
                                    } else if (model.successMessage.equalsIgnoreCase("Status Updated"))
                                        sendNotification(remoteMessage.getData().get("title"));
                                }

                            } else {
                                if (model.successMessage.equalsIgnoreCase("collect_by_cash"))
                                    sendNotification(model.message, remoteMessage.getData().get("message"));
                                else if (model.successMessage.equalsIgnoreCase("collect_balance_amount"))
                                    sendNotification(model.message, remoteMessage.getData().get("message"));
                                else if (model.successMessage.equalsIgnoreCase("another_user_loggedin"))
                                    sendNotification(model.message, remoteMessage.getData().get("message"));

                            }
                } else {
                    // Handle message within 10 seconds
                    handleNow();
                }
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {

    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNewRequestNotification(String messageBody, String messageForIntente) {
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(Constants.BroadcastsActions.NEW_REQUEST);
        intentBroadcast.putExtra(Constants.BroadcastsActions.NEW_REQUEST, messageForIntente);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
        Intent intent = new Intent(this, DrawerAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.continuous_beep);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_small_logo)//ic_launcher_small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                        .setContentText(messageBody)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, getString(R.string.default_notification_channel_id), importance);
            notificationManager.createNotificationChannel(mChannel);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setSound(defaultSoundUri, audioAttributes);
        }
        notificationManager.notify(R.string.default_notification_channel_id, notificationBuilder.build());
        wakupScreen();
    }

    private void displayNotification(String messageBody, boolean isContinusSound, String requestDAta) {
        Intent resultIntent = new Intent(this, AcceptRejectActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra(Constants.IntentExtras.ACCEPT_REJECT_DATA, requestDAta);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + (isContinusSound ? R.raw.continuous_beep : R.raw.beep));
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_small_logo)//ic_launcher_small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                        .setContentText(messageBody)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, getString(R.string.default_notification_channel_id), importance);
            notificationManager.createNotificationChannel(mChannel);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setSound(defaultSoundUri, audioAttributes);
        }
        notificationManager.notify(R.string.default_notification_channel_id, notificationBuilder.build());
        wakupScreen();
    }

    public static void displayNotificationConnectivity(Context context, String messageBody) {
        Intent intent = new Intent(context, DrawerAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = context.getString(R.string.connectivity_notification_channel_id);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_small_logo)//ic_launcher_small)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round))
                        .setContentText(messageBody)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, context.getString(R.string.connectivity_notification_channel_id), importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(R.string.connectivity_notification_channel_id, notificationBuilder.build());
        wakupScreen(context);
    }

    private void sendNotification(String messageBody, String messageForIntente) {
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(Constants.BroadcastsActions.NEW_REQUEST);
        intentBroadcast.putExtra(Constants.BroadcastsActions.NEW_REQUEST, messageForIntente);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
        Intent intent = new Intent(this, DrawerAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_small_logo)//ic_launcher_small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                        .setContentText(messageBody)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, getString(R.string.default_notification_channel_id), importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(R.string.default_notification_channel_id, notificationBuilder.build());
        wakupScreen();
    }

    private void sendCancelNotification(String messageBody, String messageForIntente) {
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(Constants.BroadcastsActions.NEW_REQUEST);
        intentBroadcast.putExtra(Constants.BroadcastsActions.NEW_REQUEST, messageForIntente);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
        Intent intent = new Intent(this, DrawerAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.cancel_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_small_logo)//ic_launcher_small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                        .setContentText(messageBody)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, getString(R.string.cancel_notification_channel_id), importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(R.string.cancel_notification_channel_id, notificationBuilder.build());
        wakupScreen();
    }

    private void sendNotificationApprovedDeclined(String messageBody, String messageForIntente) {
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(Constants.BroadcastsActions.APPROVE_DECLINE);
        intentBroadcast.putExtra(Constants.BroadcastsActions.APPROVE_DECLINE, messageForIntente);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
        Intent intent = new Intent(this, DrawerAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_small_logo)//ic_launcher_small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                        .setContentText(messageBody)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, getString(R.string.default_notification_channel_id), importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(R.string.default_notification_channel_id, notificationBuilder.build());
        wakupScreen();
    }


    private void sendNotification(String messageBody) {
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(Constants.BroadcastsActions.RideFromAdmin);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
        Intent intent = new Intent(this, DrawerAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.default_notification_ride_admin);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_small_logo)//ic_launcher_small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                        .setContentText(messageBody)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, getString(R.string.default_notification_ride_admin), importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(R.string.default_notification_ride_admin, notificationBuilder.build());
        wakupScreen();
    }

    private void displayNotification(String messageBody) {
        Intent intent = new Intent(this, DrawerAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.default_notification_ride_admin);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_small_logo)//ic_launcher_small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                        .setContentText(messageBody)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, getString(R.string.default_notification_ride_admin), importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(R.string.default_notification_ride_admin, notificationBuilder.build());
        wakupScreen();
    }


    public static void cancelNotification(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(R.string.default_notification_channel_id);
    }

    public static void cancelConnectivityNotification(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(R.string.connectivity_notification_channel_id);
    }

    public static void wakupScreen(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (isScreenOn == false) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
            wl_cpu.acquire(10000);
        }
    }

    private void wakupScreen() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (isScreenOn == false) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
            wl_cpu.acquire(10000);
        }
    }

    public static boolean isActivityOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {//send Broadcast for accept or reject
            return false;
        }
        final String packageName = context.getPackageName();    // open Application
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
