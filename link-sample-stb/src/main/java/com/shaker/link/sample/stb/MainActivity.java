package com.shaker.link.sample.stb;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    private Device device;

    @Bind(R.id.tv_text)
    TextView tvText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        device = new Device(this);
        new Thread() {
            @Override
            public void run() {
                super.run();
                device.start();
            }
        }.start();
    }


    @Override
    protected void onDestroy() {
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
