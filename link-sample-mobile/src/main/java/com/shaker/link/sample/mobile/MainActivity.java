package com.shaker.link.sample.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shaker.link.core.exception.ShakerLinkException;
import com.shaker.link.core.socket.SocketClient;
import com.shaker.link.core.upnp.ControlPoint;
import com.shaker.link.core.upnp.bean.DeviceModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * main activity
 * Created by yinghuihong on 16/7/22.
 */
public class MainActivity extends AppCompatActivity implements ControlPoint.DeviceListChangedListener, SocketClient.SocketListener {

    @Bind(R.id.rv_devices)
    RecyclerView rvDevices;

    @Bind(R.id.tv_text)
    TextView tvText;

    @Bind(R.id.et_text)
    EditText etText;

    private static final String TAG = MainActivity.class.getSimpleName();

    private List<DeviceEntity> mEntities = new ArrayList<>();

    private DeviceAdapter mAdapter;

    private ControlPoint mControlPoint;

    private String mConnectDeviceUUID;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    // restart device when network is changed
                    Log.e(TAG, "Network is available.");
                    if (mControlPoint != null) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                mControlPoint.restart();
                                try {
                                    mControlPoint.search();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                } else {
                    Log.e(TAG, "Network is unavailable.");
                }
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        rvDevices.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DeviceAdapter(this, mEntities);
        mAdapter.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Toast.makeText(MainActivity.this, "Connect... ", Toast.LENGTH_SHORT).show();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        DeviceEntity deviceEntity = mEntities.get(position);
                        try {
                            mControlPoint.connect(deviceEntity.host, deviceEntity.port, deviceEntity.uuid);
                        } catch (final ShakerLinkException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }.start();
            }
        });
        rvDevices.setAdapter(mAdapter);

        mControlPoint = new ControlPoint(this, this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);
    }

    @OnClick(R.id.btn_search)
    void search() {
        // TODO CP主动间隔搜索
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    mControlPoint.search();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @OnClick(R.id.btn_send)
    void send() {
        try {
            mControlPoint.send(etText.getText().toString());
            tvText.append("Me: " + etText.getText().toString() + "\n");
            etText.getText().clear();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        mControlPoint.close();
        super.onDestroy();
    }

    @Override
    public void deviceListChanged(ControlPoint controlPoint) {
        updateDeviceList();
    }

    private synchronized void updateDeviceList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEntities.clear();
                Map<String, DeviceModel> map = mControlPoint.getDeviceModels();
                for (Map.Entry<String, DeviceModel> entry : map.entrySet()) {
                    DeviceModel model = entry.getValue();
                    DeviceEntity entity = new DeviceEntity();
                    entity.isChecked = model.uuid.equals(mConnectDeviceUUID);
                    entity.uuid = model.uuid;
                    entity.name = model.name;
                    entity.host = model.host;
                    entity.port = model.socketPort;
                    mEntities.add(entity);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void socketCreated(SocketClient socketClient) {
        mConnectDeviceUUID = socketClient.getUuid();
        updateDeviceList();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Connect Success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void socketTimeOut(SocketClient socketClient) {
        mConnectDeviceUUID = null;
        updateDeviceList();
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
        mConnectDeviceUUID = null;
        updateDeviceList();
    }

    @Override
    public void socketPassiveClosed(SocketClient socketClient) {
        mConnectDeviceUUID = null;
        updateDeviceList();
    }

    @Override
    public void socketReceiveException(IOException e) {

    }
}
