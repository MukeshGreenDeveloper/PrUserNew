package bz.pei.driver.ui.DrawerScreen.Fragmentz;

/**
 * Created by root on 12/4/17.
 */

public interface MessageCallback {
    /**
     * Method overriden in AsyncTask 'doInBackground' method while creating the TCPClient object.
     * @param message Received message from server app.
     */
    public void callbackMessageReceiver(String message);

}
