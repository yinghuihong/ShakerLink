package com.shaker.link.sample.stb;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.shaker.link.core.upnp.Device;

/**
 * Created by yinghuihong on 16/7/22.
 */
public class MainActivity extends Activity {

    private Device device;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        device = new Device();
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
}
