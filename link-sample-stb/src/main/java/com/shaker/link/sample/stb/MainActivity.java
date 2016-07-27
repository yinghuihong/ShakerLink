package com.shaker.link.sample.stb;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shaker.link.core.socket.SocketClient;
import com.shaker.link.core.upnp.Device;

import java.io.IOException;

/**
 * Created by yinghuihong on 16/7/22.
 */
public class MainActivity extends Activity implements SocketClient.SocketListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Device device;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    // restart device when network is changed
                    Log.e(TAG, "Network is available.");
                    if (device != null) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                device.restart();
                            }
                        }.start();
                    }
                } else {
                    Log.e(TAG, "Network is unavailable.");
                }
            }
        }
    };

    @Bind(R.id.tv_text)
    TextView tvText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        device = new Device(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        new Thread() {
            @Override
            public void run() {
                super.run();
                device.close();
            }
        }.start();
        super.onDestroy();
    }

    @Override
    public void socketCreated(SocketClient socketClient) {

    }

    @Override
    public void socketTimeOut(SocketClient socketClient) {

    }

    @Override
    public void socketReceive(SocketClient socketClient, final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvText.append("RECEIVE: " + data + "\n");
            }
        });
    }

    @Override
    public void socketActiveClosed(SocketClient socketClient) {

    }

    @Override
    public void socketPassiveClosed(SocketClient socketClient) {

    }

    @Override
    public void socketReceiveException(IOException e) {

    }
}
