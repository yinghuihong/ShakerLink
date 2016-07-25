package com.shaker.link.sample.mobile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

import java.util.List;

/**
 * Created by yinghuihong on 16/7/22.
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private Context mContext;

    private List<DeviceEntity> mEntities;

    public DeviceAdapter(Context context, List<DeviceEntity> entities) {
        this.mContext = context;
        this.mEntities = entities;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeviceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_device_item, parent, false), onItemClickListener);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        holder.bind(mEntities.get(position));
    }

    @Override
    public int getItemCount() {
        return mEntities.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.rb_connect)
        RadioButton rbConnect;

        @Bind(R.id.tv_name)
        TextView tvName;

        DeviceViewHolder(View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(view, getAdapterPosition());
                    }
                }
            });
        }

        void bind(DeviceEntity entity) {
            rbConnect.setChecked(entity.isChecked);
            tvName.setText(entity.name);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
