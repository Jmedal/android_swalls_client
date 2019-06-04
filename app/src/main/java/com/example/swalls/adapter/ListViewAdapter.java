package com.example.swalls.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.swalls.EduDetailActivity;
import com.example.swalls.R;
import com.example.swalls.adapter.holder.ItemViewHolder;
import com.example.swalls.modal.Edu;

import java.util.ArrayList;
import java.util.List;

/**
 * 要用用于生成显示数据
 *
 * @author huangbq
 *
 */
public class ListViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private List<Edu> data;

    public ListViewAdapter(Context context){
        this.mInflater = LayoutInflater.from(context);
        this.data = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder;
        if (convertView == null) {
            holder = new ItemViewHolder();
            convertView = mInflater.inflate(R.layout.item_layout,null);
            holder.item_title = (TextView)convertView.findViewById(R.id.item_title);
            holder.item_time = (TextView)convertView.findViewById(R.id.item_time);
            holder.item_layout = (LinearLayout)convertView.findViewById(R.id.item_layout);
            convertView.setTag(holder);

        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }
        holder.item_title.setText((String)data.get(position).getEaTitle());
        holder.item_time.setText((String)data.get(position).getEaTime());
        holder.item_layout.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
               Intent intent = new Intent(mInflater.getContext(), EduDetailActivity.class);
               intent.putExtra("id",data.get(position).getId());
               mInflater.getContext().startActivity(intent);
           }
        });
        return convertView;
    }

    public void setData(List<Edu> data) {
        if (data != null)
            this.data = data;
    }
}