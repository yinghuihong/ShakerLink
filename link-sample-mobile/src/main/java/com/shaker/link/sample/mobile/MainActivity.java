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
import com.shaker.link.core.upnp.ControlPoint;
import com.shaker.link.core.upnp.bean.DeviceModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yinghuihong on 16/7/22.
 */
public class MainActivity extends AppCompatActivity implements ControlPoint.DeviceListChangedListener {

    @Bind(R.id.rv_devices)
    RecyclerView rvDevices;

    private List<DeviceEntity> mEntities = new ArrayList<>();

    private DeviceAdapter mAdapter;

    private ControlPoint controlPoint;

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
                Toast.makeText(MainActivity.this, "Click " + position, Toast.LENGTH_SHORT).show();
                for (DeviceEntity entity : mEntities) {
                    entity.isChecked = false;
                }
                mEntities.get(position).isChecked = true;
                // TODO connect socket server
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        controlPoint.connect(mEntities.get(position).host, mEntities.get(position).port);
                    }
                }.start();
            }
        });
        rvDevices.setAdapter(mAdapter);

        controlPoint = new ControlPoint(this);
        new Thread() {
            @Override
            public void run() {
                super.run();
                controlPoint.start();
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
                    controlPoint.search();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        controlPoint.close();
        super.onDestroy();
    }

    @Override
    public void deviceListChanged(final ControlPoint controlPoint) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEntities.clear();
                Map<String, DeviceModel> map = controlPoint.getDeviceModels();
                for (Map.Entry<String, DeviceModel> entry : map.entrySet()) {
                    DeviceModel model = entry.getValue();
                    DeviceEntity entity = new DeviceEntity();
                    entity.name = model.name;
                    entity.host = model.host;
                    entity.port = model.socketPort;
                    mEntities.add(entity);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

}
