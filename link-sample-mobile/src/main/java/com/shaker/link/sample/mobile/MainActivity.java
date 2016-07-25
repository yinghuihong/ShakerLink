package com.shaker.link.sample.mobile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    private List<DeviceEntity> mEntities = new ArrayList<>();

    private DeviceAdapter mAdapter;

    private ControlPoint mControlPoint;

    private String mConnectDeviceUUID;

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
                Toast.makeText(MainActivity.this, "Connect... " + position, Toast.LENGTH_SHORT).show();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        DeviceEntity deviceEntity = mEntities.get(position);
                        mControlPoint.connect(deviceEntity.host, deviceEntity.port, deviceEntity.uuid);
                    }
                }.start();
            }
        });
        rvDevices.setAdapter(mAdapter);

        mControlPoint = new ControlPoint(this, this);
        new Thread() {
            @Override
            public void run() {
                super.run();
                mControlPoint.start();
            }
        }.start();
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

    @Override
    protected void onDestroy() {
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
    }

    @Override
    public void socketReceive(SocketClient socketClient, String data) {

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
