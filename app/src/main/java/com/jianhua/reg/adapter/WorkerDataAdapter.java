package com.jianhua.reg.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jianhua.reg.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sjy on 2018/1/15.
 */

public class WorkerDataAdapter extends RecyclerView.Adapter<WorkerDataAdapter.WorkerHolder> {
    private List<String> list = new ArrayList<>();
    LayoutInflater inflater;
    Context context;

    public WorkerDataAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    //构造点击接口
    public OnItemClickListener onItemListener;

    public interface OnItemClickListener {
        void onItemClickListener(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemListener = onItemClickListener;
    }

    @Override
    public WorkerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_person, parent, false);
        WorkerHolder holder = new WorkerHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(WorkerHolder holder, final int position) {
        holder.layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemListener.onItemClickListener(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * 自定义holder
     */
    class WorkerHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_name)
        TextView item_name;
        @BindView(R.id.item_id)
        TextView item_id;
        @BindView(R.id.layout_item)
        RelativeLayout layout_item;

        public WorkerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setTag(this);
        }
    }
}
