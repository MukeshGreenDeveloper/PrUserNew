package bz.pei.driver.ui.drawerscreen.fragmentz;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by root on 12/4/17.
 */

public class SocketLocationUpdateClass {

    public static int SENT = 4, ERROR = 3, CONNECTING = 2, SENDING = 1;
    private static final String TAG = "TCPClient";
    private final Handler mHandler;
    private String ipNumber, incomingMessage, command;
    BufferedReader in;
    PrintWriter out;
    private MessageCallback listener = null;
    private boolean mRun = false;

    /**
     * TCPClient class constructor, which is created in AsyncTasks after the button click.
     *
     * @param mHandler Handler passed as an argument for updating the UI with sent messages
     * @param command  Command passed as an argument, e.g. "shutdown -r" for restarting computer
     * @param ipNumber String retrieved from IpGetter class that is looking for ip number.
     * @param listener Callback interface object
     */
    public SocketLocationUpdateClass(Handler mHandler, String command, String ipNumber, MessageCallback listener) {
        this.listener = listener;
        this.ipNumber = ipNumber;
        this.command = command;
        this.mHandler = mHandler;
    }

    /**
     * Public method for sending the message via OutputStream object.
     *
     * @param message Message passed as an argument and sent via OutputStream object.
     */
    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
//            mHandler.sendEmptyMessageDelayed(SENDING, 1000);
            Log.d(TAG, "Sent Message: " + message);

        }
    }

    /**
     * Public method for stopping the TCPClient object ( and finalizing it after that ) from AsyncTask
     */
    public void stopClient() {
        Log.d(TAG, "Client stopped!");
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {
            // Creating InetAddress object from ipNumber passed via constructor from IpGetter class.
            InetAddress serverAddress = InetAddress.getByName("http://192.168.1.20:3000/driver/home");

            Log.d(TAG, "Connecting...");

            /**
             * Sending empty message with static int value from MapFragmentViewModel
             * to update UI ( 'Connecting...' ).
             *
             * @see com.example.turnmeoff.CONNECTING
             */
            mHandler.sendEmptyMessageDelayed(CONNECTING, 1000);

            /**
             * Here the socket is created with hardcoded port.
             * Also the port is given in IpGetter class.
             *
             * @see com.example.turnmeoff.IpGetter
             */
//            Socket socket = new ServerSocket().accept();
            Socket socket = new Socket("",3000);


            try {

                // Create PrintWriter object for sending messages to server.
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //Create BufferedReader object for receiving messages from server.
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                Log.d(TAG, "In/Out created");

                //Sending message with command specified by AsyncTask
                this.sendMessage(command);

                //
                mHandler.sendEmptyMessageDelayed(SENDING, 2000);

                //Listen for the incoming messages while mRun = true
                while (mRun) {
                    incomingMessage = in.readLine();
                    if (incomingMessage != null && listener != null) {

                        /**
                         * Incoming message is passed to MessageCallback object.
                         * Next it is retrieved by AsyncTask and passed to onPublishProgress method.
                         *
                         */
                        listener.callbackMessageReceiver(incomingMessage);

                    }
                    incomingMessage = null;

                }

                Log.d(TAG, "Received Message: " + incomingMessage);

            } catch (Exception e) {

                Log.d(TAG, "Error", e);
                mHandler.sendEmptyMessageDelayed(ERROR, 2000);

            } finally {

                out.flush();
                out.close();
                in.close();
                socket.close();
                mHandler.sendEmptyMessageDelayed(SENT, 3000);
                Log.d(TAG, "Socket Closed");
            }

        } catch (Exception e) {

            Log.d(TAG, "Error", e);
            mHandler.sendEmptyMessageDelayed(ERROR, 2000);

        }

    }
}